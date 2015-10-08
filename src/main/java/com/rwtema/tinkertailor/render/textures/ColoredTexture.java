package com.rwtema.tinkertailor.render.textures;

import java.awt.image.BufferedImage;
import net.minecraft.util.ResourceLocation;

public class ColoredTexture extends ProcessedTexture {
	private final int color;


	public ColoredTexture(ResourceLocation location, ResourceLocation fallback, int color) {
		super(location, fallback);
		this.color = color;
	}

	@Override
	protected int processPixel(BufferedImage img, int x, int y, int color) {
		return mixColor(color, this.color);
	}

//	@Override
//	protected int processPixel(BufferedImage img, int x, int y, int color) {
//		int alpha = rgb.getAlpha(color);
//		if (alpha == 0)
//			return color;
//
//		float[] hsb_base = Color.RGBtoHSB(rgb.getRed(this.color), rgb.getGreen(this.color), rgb.getBlue(this.color), null);
//		float[] hsb = Color.RGBtoHSB(rgb.getRed(color), rgb.getGreen(color), rgb.getBlue(color), null);
//
//		hsb[0] = hsb_base[0];
//		hsb[1] = hsb_base[1];
//
//		return (Color.HSBtoRGB(hsb[0],hsb[1],hsb[2]) & 0x00FFFFFF) | (alpha << 24);
//
////		float[] col_base = getColor(this.color);
////		float[] col = getColor(color);
//////		float[] hsl = ColorHelper.RGBtoHSL(getColor(color), null);
////
////
////		float[] hsl2 = ColorHelper.RGBtoHSL(getColor(this.color), null);
////		float[] hsl = ColorHelper.RGBtoHSL(getColor(color), null);
////
////		hsl[0] = hsl2[0];
////		hsl[1] = hsl2[1];
////		float p = 1;
////		hsl[2] = p * hsl[2] + (1 - p) * hsl2[2];
////
////		float[] col2 = ColorHelper.HSLtoRGB(hsl, null);
////
////		for (int i = 0; i < col.length; i++) {
////			col[i] = (col[i] * col_base[i] + col2[i]) / 2;
////		}
//
////		return getColor(col, alpha);
//	}
}
