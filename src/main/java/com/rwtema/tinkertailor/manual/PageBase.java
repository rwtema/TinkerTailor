package com.rwtema.tinkertailor.manual;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import mantle.client.pages.BookPage;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class PageBase extends BookPage {

	public static final int PAGE_WIDTH = 178;
	Exception exception = null;
	boolean drawBlankPage = false;
	Element element;

	@Override
	public final void readPageFromXML(Element element) {
		try {
			this.element = element;
			loadData();
		} catch (Exception exception) {
			this.exception = exception;
		}
		this.element = null;
	}

	@Override
	public final void renderContentLayer(int localWidth, int localHeight, boolean isTranslatable) {
		if (drawBlankPage) {
			return;
		}

		if (exception != null) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			exception.printStackTrace(pw);

			manual.fonts.drawSplitString(sw.toString(), localWidth + 4, localHeight + 4, PAGE_WIDTH - 8, 0);
		} else {
			GL11.glPushMatrix();
			GL11.glTranslated(localWidth, localHeight, 0);
			try {
				render(isTranslatable);
			} catch (Exception err) {
				exception = err;
			}
			GL11.glPopMatrix();
		}
	}

	protected abstract void render(boolean isTranslatable);

	protected void renderStack(ItemStack itemStack, int x, int y) {
		manual.renderitem.renderItemAndEffectIntoGUI(manual.fonts, manual.getMC().renderEngine, itemStack, x, y);
	}

	protected void stopRenderingItem() {
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
	}

	protected void startRenderingItem() {
		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		RenderHelper.enableGUIStandardItemLighting();
	}

	protected int drawCenteredString(String text, int height) {
		List<String> strings = manual.fonts.listFormattedStringToWidth(text, PAGE_WIDTH);
		for (String string : strings) {
			drawCenteredString(string, height, PAGE_WIDTH);
			height += 9;
		}

		return strings.size();
	}

	protected int drawTextLine(String text, int x, int y) {
		manual.fonts.drawString(text, x, y, 0);
		return manual.fonts.FONT_HEIGHT;
	}

	protected int drawBlankLine() {
		return manual.fonts.FONT_HEIGHT;
	}

	protected int drawTextBlock(String text, int x, int y) {
		return drawTextBlock(text, x, y, PAGE_WIDTH - x);
	}

	protected int drawTextBlock(String text, int x, int y, int maxWidth) {
		manual.fonts.drawSplitString(text, x, y, maxWidth, 0);
		return manual.fonts.listFormattedStringToWidth(text, maxWidth).size() * manual.fonts.FONT_HEIGHT;
	}

	protected int drawCenteredString(String text, int height, int width) {
		manual.fonts.drawString(text, (width - manual.fonts.getStringWidth(text)) / 2, height, 0);
		return manual.fonts.FONT_HEIGHT;
	}

	protected abstract void loadData() throws IOException;

	protected final String loadText() throws IOException {
		String text = loadText("text");
		text = text.replace("\\n", "\n");
		return text;
	}

	protected final NodeList loadNodeList(String name) throws IOException {
		return loadNodeList(name, true);
	}

	protected final NodeList loadNodeList(String name, boolean assertNonEmpty) throws IOException {
		NodeList nodes = element.getElementsByTagName(name);
		if (nodes == null) throw new IOException("No tags of type " + name + " where found");
		return nodes;
	}

	protected final String loadText(String name) throws IOException {
		String text;
		NodeList nodes = loadNodeList(name);

		Node item = nodes.item(0);
		if (item == null) throw new IOException("Element " + name + " not found");
		text = item.getTextContent();


		return text;
	}

	protected final String[] loadTextArray(String name) throws IOException {
		NodeList nodeList = loadNodeList(name, false);
		int n = nodeList.getLength();
		String[] data = new String[n];
		for (int i = 0; i < n; i++) {
			data[i] = nodeList.item(i).getTextContent();
		}

		return data;
	}

	protected static int curTime() {
		return ((int) System.currentTimeMillis() / 2000);
	}
}
