package com.rwtema.tinkertailor.manual;

import java.io.IOException;
import org.lwjgl.opengl.GL11;

public class PageTitle extends PageBase {
	String title, text;

	@Override
	protected void render(boolean isTranslatable) {
		GL11.glPushMatrix();
		GL11.glScaled(2, 2, 2);
		manual.fonts.drawString("\u00a7n" + title, (PAGE_WIDTH / 2 - manual.fonts.getStringWidth(title)) / 2, 54 / 2, 0);
		GL11.glPopMatrix();

		drawCenteredString(text, 76);
	}

	@Override
	protected void loadData() throws IOException {
		title = loadText("title");
		text = loadText();
	}
}
