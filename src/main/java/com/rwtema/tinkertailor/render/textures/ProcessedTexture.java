package com.rwtema.tinkertailor.render.textures;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ProcessedTexture extends SimpleTexture {
	static final ColorModel rgb = ColorModel.getRGBdefault();
	private static final Logger logger = LogManager.getLogger();
	protected final ResourceLocation fallback;
	ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_CIEXYZ);

	public ProcessedTexture(ResourceLocation p_i1275_1_, ResourceLocation fallback) {
		super(p_i1275_1_);
		this.fallback = fallback;
	}

	private static float[] getColor(int col) {
		float factor = 1 / 255.0F;
		return new float[]{
				rgb.getRed(col) * factor,
				rgb.getGreen(col) * factor,
				rgb.getBlue(col) * factor
		};
	}

	private static int getColor(float[] col, int alpha) {
		int r = (int) (col[0] * 255.0F);
		int g = (int) (col[1] * 255.0F);
		int b = (int) (col[2] * 255.0F);

		return (alpha << 24) | (r << 16) | (g << 8) | (b);
	}

	protected static int mixColor(int color, int c) {
		int alpha = rgb.getAlpha(color);
		if (alpha <= 10) return color;

		float[] col_base = getColor(c);
		float[] col = getColor(color);

		float[] hsl2 = ColorHelper.RGBtoHSL(getColor(c), null);
		float[] hsl = ColorHelper.RGBtoHSL(getColor(color), null);

		float p = 0.5F;
		hsl[0] = hsl2[0];
		hsl[1] = hsl2[1];


		float[] col2 = ColorHelper.HSLtoRGB(hsl, null);

		float lum = col[0] * 0.1769F + col[1] * 0.8124F + 0.0106F * col[2];

		for (int i = 0; i < col.length; i++) {
			col[i] = (lum * col_base[i] * p + col2[i] * (1 - p));
		}

		return getColor(col, alpha);

//		float[] hsb_base = Color.RGBtoHSB(rgb.getRed(c), rgb.getGreen(c), rgb.getBlue(c), null);
//		float[] hsb = Color.RGBtoHSB(rgb.getRed(color), rgb.getGreen(color), rgb.getBlue(color), null);
//
//		hsb[0] = hsb_base[0];
//		hsb[1] = hsb_base[1];
//
//		return (Color.HSBtoRGB(hsb[0],hsb[1],hsb[2]) & 0x00FFFFFF) | (rgb.getAlpha(color) << 24);
	}

	public void loadTexture(IResourceManager manager) throws IOException {
		try {
			super.loadTexture(manager);
		} catch (IOException exception) {
			this.deleteGlTexture();
			InputStream inputstream = null;

			try {
				IResource iresource = manager.getResource(fallback);
				inputstream = iresource.getInputStream();
				BufferedImage baseImage = ImageIO.read(inputstream);
				boolean flag = false;
				boolean flag1 = false;

				BufferedImage destImage = new BufferedImage(baseImage.getWidth(), baseImage.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);


				if (iresource.hasMetadata()) {
					try {
						TextureMetadataSection texturemetadatasection = (TextureMetadataSection) iresource.getMetadata("texture");

						if (texturemetadatasection != null) {
							flag = texturemetadatasection.getTextureBlur();
							flag1 = texturemetadatasection.getTextureClamp();
						}
					} catch (RuntimeException runtimeexception) {
						logger.warn("Failed reading metadata of: " + this.textureLocation, runtimeexception);
					}
				}

				processTexture(destImage, baseImage, manager);

				TextureUtil.uploadTextureImageAllocate(this.getGlTextureId(), destImage, flag, flag1);
			} finally {
				if (inputstream != null) {
					inputstream.close();
				}
			}
		}
	}

	protected void processTexture(BufferedImage destImage, BufferedImage baseImage, IResourceManager mgr) throws IOException {

		for (int x = 0; x < baseImage.getWidth(); x++) {
			for (int y = 0; y < baseImage.getHeight(); y++) {
				int c = baseImage.getRGB(x, y);
				c = processPixel(baseImage, x, y, c);
				destImage.setRGB(x, y, c);
			}
		}
	}

	protected abstract int processPixel(BufferedImage img, int x, int y, int color);
}
