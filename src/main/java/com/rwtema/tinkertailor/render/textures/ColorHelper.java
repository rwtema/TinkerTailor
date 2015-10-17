package com.rwtema.tinkertailor.render.textures;

public class ColorHelper {
	public static float[] HSLtoRGB(float[] hsl, float[] rgb) {
		if (rgb == null) {
			rgb = new float[3];
		}
		float hue = hsl[0];
		float saturation = hsl[1];
		float lightness = hsl[2];

		if (saturation > 0.0f) {
			hue = (hue < 1.0f) ? hue * 6.0f : 0.0f;
			float q = lightness + saturation * ((lightness > 0.5f) ? 1.0f - lightness : lightness);
			float p = 2.0f * lightness - q;
			rgb[0] = normalize(q, p, (hue < 4.0f) ? (hue + 2.0f) : (hue - 4.0f));
			rgb[1] = normalize(q, p, hue);
			rgb[2] = normalize(q, p, (hue < 2.0f) ? (hue + 4.0f) : (hue - 2.0f));
		} else {
			rgb[0] = lightness;
			rgb[1] = lightness;
			rgb[2] = lightness;
		}
		return rgb;
	}

	public static float[] RGBtoHSL(float[] rgb, float[] hsl) {
		if (hsl == null) {
			hsl = new float[3];
		}
		float max = max(rgb[0], rgb[1], rgb[2]);
		float min = min(rgb[0], rgb[1], rgb[2]);

		float summa = max + min;
		float saturation = max - min;
		if (saturation > 0.0f) {
			saturation /= (summa > 1.0f)
					? 2.0f - summa
					: summa;
		}
		hsl[0] = getHue(rgb[0], rgb[1], rgb[2], max, min);
		hsl[1] = saturation;
		hsl[2] = summa / 2.0f;
		return hsl;
	}

	static float min(float red, float green, float blue) {
		float min = (red < green) ? red : green;
		return (min < blue) ? min : blue;
	}

	static float max(float red, float green, float blue) {
		float max = (red > green) ? red : green;
		return (max > blue) ? max : blue;
	}

	static float getHue(float red, float green, float blue, float max, float min) {
		float hue = max - min;
		if (hue > 0.0f) {
			if (max == red) {
				hue = (green - blue) / hue;
				if (hue < 0.0f) {
					hue += 6.0f;
				}
			} else if (max == green) {
				hue = 2.0f + (blue - red) / hue;
			} else /*max == blue*/ {
				hue = 4.0f + (red - green) / hue;
			}
			hue /= 6.0f;
		}
		return hue;
	}

	private static float normalize(float q, float p, float color) {
		if (color < 1.0f) {
			return p + (q - p) * color;
		}
		if (color < 3.0f) {
			return q;
		}
		if (color < 4.0f) {
			return p + (q - p) * (4.0f - color);
		}
		return p;
	}

	public static float getR(int col) {
		return ((col >> 16) & 255) / 255.0F;
	}

	public static float getG(int col) {
		return ((col >> 8) & 255) / 255.0F;
	}

	public static float getB(int col) {
		return ((col) & 255) / 255.0F;
	}
}
