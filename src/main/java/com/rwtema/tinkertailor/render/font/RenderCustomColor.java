package com.rwtema.tinkertailor.render.font;

import com.rwtema.tinkertailor.render.textures.ColorHelper;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import java.util.HashMap;
import net.minecraft.client.gui.FontRenderer;

public class RenderCustomColor implements ICharRenderer {
	public static HashMap<Character, Integer> colors = new HashMap<Character, Integer>();
	public static HashMap<Integer, String> col2String = new HashMap<Integer, String>();

	public static RenderCustomColor instance = new RenderCustomColor();

	static {
		int[] regularCols = ObfuscationReflectionHelper.getPrivateValue(FontRenderer.class, CustomFontRenderer.instance, "colorCode", "field_78285_g");
		for (int i = 0; i < 16; i++) {
			String s;
			if (i < 10) {
				s = "\u00a7" + i;
			} else {
				s = "\u00a7" + (char) ('a' + (i - 10));
			}
			col2String.put(makeColorKey(regularCols[i]), s);
		}
	}

	public static String getCol(int color) {
		String code = col2String.get(makeColorKey(color));

		if (code == null) {
			char c = CustomFontRenderer.instance.getNextBlankChar();
			code = String.valueOf(c);

			CustomFontRenderer.instance.renderOverrides.put(c, instance);
			colors.put(c, color);
			col2String.put(makeColorKey(color), code);
		}
		return code;
	}

	private static int makeColorKey(int c) {
		return c & 0xfcfcfcfc;
	}

	@Override
	public float renderChar(char letter, boolean italicFlag, float x, float y, CustomFontRenderer fontRenderer) {
		Integer col = colors.get(letter);
		if (col != null) {
			if (fontRenderer.dropShadow)
				fontRenderer.setColor(ColorHelper.getR(col) / 2, ColorHelper.getG(col) / 2, ColorHelper.getB(col) / 2, 1);
			else
				fontRenderer.setColor(ColorHelper.getR(col), ColorHelper.getG(col), ColorHelper.getB(col), 1);
		}
		return 0;
	}

	@Override
	public int getCharWidth(char letter, CustomFontRenderer fontRenderer) {
		return 0;
	}

}
