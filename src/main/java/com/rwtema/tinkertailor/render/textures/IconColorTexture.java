package com.rwtema.tinkertailor.render.textures;

import gnu.trove.list.array.TIntArrayList;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import javax.imageio.ImageIO;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class IconColorTexture extends ProcessedTexture {
	Random random = new Random(0);

	ResourceLocation base;

	int[] cols;

	public IconColorTexture(ResourceLocation p_i1275_1_, ResourceLocation fallback) {
		super(p_i1275_1_, fallback);
	}

	@Override
	protected void processTexture(BufferedImage img, BufferedImage baseImage, IResourceManager manager) throws IOException {
		InputStream inputstream = null;
		try {
			IResource iresource = manager.getResource(base);
			inputstream = iresource.getInputStream();
			BufferedImage img2 = ImageIO.read(inputstream);

			int h = img2.getHeight();
			int w = img2.getWidth();
			int[] arr = new int[w * h];
			img2.getRGB(0, 0, w, h, arr, 0, w);
			TIntArrayList list = new TIntArrayList();
			for (int i : arr) {
				if (rgb.getAlpha(i) > 40) {
					list.add(i);
				}
			}

			cols = list.toArray();
			random.setSeed(0);

		} finally {
			if (inputstream != null) {
				inputstream.close();
			}
		}
		super.processTexture(img, baseImage, manager);
	}

	@Override
	protected int processPixel(BufferedImage img, int x, int y, int color) {
		int alpha = rgb.getAlpha(color);
		if (alpha == 0)
			return color;

		int c = this.cols[random.nextInt(cols.length)];
		return mixColor(color, c);
	}

}
