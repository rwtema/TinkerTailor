package com.rwtema.tinkertailor.manual;

import java.io.IOException;
import org.lwjgl.opengl.GL11;

public class PageTitle extends PageBase {
	@Override
	protected void render(int localWidth, int localHeight, boolean isTranslatable) {
		GL11.glPushMatrix();
		GL11.glScaled(2, 2, 2);
		manual.fonts.drawString("\u00a7n" + title, (localWidth + (PAGE_WIDTH - manual.fonts.getStringWidth(title) * 2) / 2) / 2, (localHeight + 54) / 2, 0);
		GL11.glPopMatrix();

		drawCenteredString(text, 76, localWidth, localHeight);
		//manual.fonts.drawSplitString(text, localWidth, localHeight + 104 + 10 * 2, 178, 0);
	}

	String title, text;

	@Override
	protected void loadData() throws IOException {
		title = loadText("title");
		text = loadText();
	}
}
