package com.rwtema.tinkertailor.render.textures;

import java.awt.image.BufferedImage;
import net.minecraft.util.ResourceLocation;

public class ColoredTexture extends ProcessedTexture {
	protected final int color;


	public ColoredTexture(ResourceLocation location, ResourceLocation fallback, int color) {
		super(location, fallback);
		this.color = color;
	}

	@Override
	protected int processPixel(BufferedImage img, int x, int y, int color) {
		return mixColor(color, this.color);
	}
}
