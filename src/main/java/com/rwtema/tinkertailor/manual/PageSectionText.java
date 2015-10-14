package com.rwtema.tinkertailor.manual;

import java.io.IOException;
import net.minecraft.util.EnumChatFormatting;

public class PageSectionText extends PageBase {
	String text;
	String section;

	@Override
	protected void render(boolean isTranslatable) {
		drawCenteredString(EnumChatFormatting.UNDERLINE + section, 4);
		drawTextBlock(text, 4, 16);
	}

	@Override
	protected void loadData() throws IOException {
		section = loadText("section");
		text = loadText();
	}
}
