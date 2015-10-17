package com.rwtema.tinkertailor.manual;

import com.rwtema.tinkertailor.nbt.TinkersTailorConstants;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import mantle.client.pages.BookPage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class PageBase extends BookPage {

	public static final int PAGE_WIDTH = 178;
	protected final int offset = TinkersTailorConstants.RANDOM.nextInt(100);
	protected int localWidth;
	protected int localHeight;
	Exception exception = null;
	boolean drawBlankPage = false;
	Element element;
	int mouseX = 0;
	int mouseY = 0;

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
		this.localWidth = localWidth;
		this.localHeight = localHeight;
		if (drawBlankPage) {
			return;
		}

		if (exception != null) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			exception.printStackTrace(pw);
			String par1Str = sw.toString();
			par1Str = par1Str.replaceAll("\\r", "");
			manual.fonts.drawSplitString(par1Str, localWidth + 4, localHeight + 4, PAGE_WIDTH - 8, 0);
		} else {
			Minecraft mc = Minecraft.getMinecraft();
			final ScaledResolution scaledresolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
			int i = scaledresolution.getScaledWidth();
			int j = scaledresolution.getScaledHeight();
			mouseX = Mouse.getX() * i / mc.displayWidth;
			mouseY = j - Mouse.getY() * j / mc.displayHeight - 1;


			GL11.glPushMatrix();
			GL11.glTranslated(localWidth, localHeight, 0);

			try {
				//GL11.glEnable(GL11.GL_DEPTH_TEST);
				render(isTranslatable);
				//GL11.glDisable(GL11.GL_DEPTH_TEST);
			} catch (Exception err) {
				if (err != exception)
					err.printStackTrace();
				exception = err;
			}
			GL11.glPopMatrix();
		}
	}

	private boolean isMouseInArea(int x0, int y0, int w, int h) {
		int mX = mouseX - localWidth, mY = mouseY - localHeight;
		return mX >= x0 - 1 && mX < x0 + w + 1 && mY >= y0 - 1 && mY < y0 + h + 1;
	}

	protected abstract void render(boolean isTranslatable);

	protected void renderStack(ItemStack itemStack, int x, int y) {
		renderStack(itemStack, x, y, 1);
	}

	protected void renderStack(ItemStack itemStack, int x, int y, double scale) {
		renderStack(itemStack, x, y, scale, true);
	}

	protected void renderStack(ItemStack itemStack, int x, int y, double scale, boolean shouldRenderOverlay) {
		if (itemStack == null) return;
		GL11.glPushMatrix();
		if (!(scale == 1)) {

			GL11.glTranslatef(x, y, 0);
			GL11.glScaled(scale, scale, scale);
		}

		GL11.glTranslatef(0.0F, 0.0F, 32.0F);
		RenderHelper.enableGUIStandardItemLighting();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glEnable(GL11.GL_LIGHTING);
		manual.renderitem.zLevel = 100F;
		manual.renderitem.renderItemAndEffectIntoGUI(manual.fonts, Minecraft.getMinecraft().renderEngine, itemStack, scale == 1 ? x : 0, scale == 1 ? y : 0);
		manual.renderitem.zLevel = 0F;

		OpenGlHelper.glBlendFunc(770, 771, 1, 0);

		GL11.glColor4d(1, 1, 1, 1);
		if (shouldRenderOverlay && itemStack.stackSize > 1)
			manual.renderitem.renderItemOverlayIntoGUI(manual.fonts, manual.getMC().renderEngine, itemStack, (localWidth + 106) / 2, (localHeight + 74) / 2, String.valueOf(itemStack.stackSize));


		GL11.glPopMatrix();

		if (GuiCustomManual.tooltip == null && isMouseInArea(x, y, (int) (16 * scale), (int) (16 * scale))) {
			GuiCustomManual.tooltip = itemStack;
		}
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

	protected int curTime() {
		return offset + ((int) System.currentTimeMillis() / 1000);
	}

	protected int getStringWidth(String s) {
		return manual.fonts.getStringWidth(s);
	}
}
