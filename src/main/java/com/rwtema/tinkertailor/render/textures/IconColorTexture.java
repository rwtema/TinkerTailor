package com.rwtema.tinkertailor.render.textures;

import com.rwtema.tinkertailor.utils.RandomHelper;
import gnu.trove.list.array.TIntArrayList;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class IconColorTexture extends ColoredTexture {
	protected final String[] icons;

	protected TIntArrayList cols = new TIntArrayList();

	public IconColorTexture(ResourceLocation location, ResourceLocation fallback, int color, String[] icons) {
		super(location, fallback, color);
		this.icons = icons;
	}


	@Override
	protected void processTexture(BufferedImage destImage, BufferedImage baseImage, IResourceManager mgr) throws IOException {
		addColors(cols, icons, mgr);

		super.processTexture(destImage, baseImage, mgr);
	}

	public static TIntArrayList addColors(TIntArrayList cols, String[] icons, IResourceManager mgr) {
		cols.clear();

		for (String icon : icons) {
			ResourceLocation loc = new ResourceLocation(icon);

			IResource resource;
			try {
				resource = mgr.getResource(completeResourceLocation(loc, "textures/blocks"));
			} catch (IOException err) {
				try {
					resource = mgr.getResource(completeResourceLocation(loc, "textures/items"));
				} catch (IOException err2) {
					resource = null;
				}
			}

			if (resource != null) {
				InputStream inputStream = null;

				try {
					inputStream = resource.getInputStream();
					BufferedImage img2 = ImageIO.read(inputStream);
					int w = img2.getWidth();
					int h = img2.getHeight();
					int[] rgb = new int[w * h];
					img2.getRGB(0, 0, w, h, rgb, 0, w);
					for (int col : rgb) {
						if (ProcessedTexture.rgb.getAlpha(col) > 10) {
							cols.add(makeSolidAlpha(col));
						}
					}
				} catch (IOException ignore) {

				} finally {
					if (inputStream != null)
						try {
							inputStream.close();
						} catch (IOException ignore) {

						}
				}

			}
		}
		return cols;
	}

	@Override
	protected int processPixel(BufferedImage img, int x, int y, int color) {
		if (cols.isEmpty())
			return super.processPixel(img, x, y, color);

		return mixColor(color, getMixedCol(x, y));
	}

	private static ResourceLocation completeResourceLocation(ResourceLocation location, String basePath) {
		return new ResourceLocation(location.getResourceDomain(), String.format("%s/%s%s", basePath, location.getResourcePath(), ".png"));
	}

	private int getMixedCol(int x, int y) {
		int rawCol = getRawCol(x, y);
		return avgColors(
				rawCol,
				rawCol,
				rawCol,
				rawCol,
				getRawCol(x + 1, y),
				getRawCol(x, y - 1),
				getRawCol(x, y + 1),
				getRawCol(x - 1, y)
		);

	}

	private int getRawCol(int x, int y) {
		return cols.get(RandomHelper.nextInt(color + x + y * width, cols.size()));
	}
}
