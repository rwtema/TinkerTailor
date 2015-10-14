package com.rwtema.tinkertailor.manual;

import java.io.IOException;
import org.lwjgl.opengl.GL11;

public class PageTitle extends PageBase {
	@Override
	protected void render(boolean isTranslatable) {
		GL11.glPushMatrix();
		GL11.glScaled(2, 2, 2);
		manual.fonts.drawString("\u00a7n" + title, ((PAGE_WIDTH - manual.fonts.getStringWidth(title) * 2) / 2) / 2, 54 / 2, 0);
		GL11.glPopMatrix();

		drawCenteredString(text, 76);
	}

	String title, text;

	@Override
	protected void loadData() throws IOException {
		title = loadText("title");
		text = loadText();
	}
}
