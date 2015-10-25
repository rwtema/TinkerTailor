package com.rwtema.tinkertailor.render.textures;

import com.rwtema.tinkertailor.utils.RandomHelper;
import gnu.trove.list.array.TIntArrayList;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class IconColorTexture extends ColoredTexture {
	protected final List<ArmorColors.TextureDetails> icons;

	protected TIntArrayList cols;

	public IconColorTexture(ResourceLocation location, ResourceLocation fallback, int color, List<ArmorColors.TextureDetails> icons) {
		super(location, fallback, color);
		this.icons = icons;
	}


	@Override
	protected void processTexture(BufferedImage destImage, BufferedImage baseImage, IResourceManager mgr) throws IOException {
		cols = getColors(icons, mgr);
		super.processTexture(destImage, baseImage, mgr);
	}

	public static TIntArrayList getColors(List<ArmorColors.TextureDetails> icons, IResourceManager mgr) {
		TIntArrayList cols = new TIntArrayList();

		for (ArmorColors.TextureDetails textureDetails : icons) {
			cols.addAll(textureDetails.gettIntArrayList(mgr));
		}

		return cols;
	}

	@Override
	protected int processPixel(BufferedImage img, int x, int y, int color) {
		if (cols.isEmpty())
			return super.processPixel(img, x, y, color);
		else
			return mixColor(color, getMixedCol(x, y));
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
