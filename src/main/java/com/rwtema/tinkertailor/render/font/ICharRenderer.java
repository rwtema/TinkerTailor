package com.rwtema.tinkertailor.render.font;

public interface ICharRenderer {
	float renderChar(char letter, boolean italicFlag, float x, float y, CustomFontRenderer fontRenderer);

	int getCharWidth(char letter, CustomFontRenderer fontRenderer);
}
