package com.rwtema.tinkertailor.render.font;

import java.util.HashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class CustomFontRenderer extends FontRenderer {

	public HashMap<Character, ICharRenderer> renderOverrides = new HashMap<Character, ICharRenderer>();

	{
		RenderSprite.addRenderer('♡', Gui.icons, 52, 0, 9, 9, this); // heart
		RenderSprite.addRenderer('♥', Gui.icons, 124, 0, 9, 9, this); // wither heart
		RenderSprite.addRenderer('⌔', Gui.icons, 34, 9, 9, 9, this); // shield
		RenderSprite.addRenderer('○', Gui.icons, 16, 18, 9, 9, this); // bubble
		RenderSprite.addRenderer('◌', Gui.icons, 25, 18, 9, 9, this); // bubble broken
		RenderSprite.addRenderer('⧰', Gui.icons, 52, 27, 9, 9, this); // hunger
		RenderSprite.addRenderer('❣', Gui.icons, 52, 45, 9, 9, this); // hardcore heart

		ResourceLocation beacon = new ResourceLocation("textures/gui/container/beacon.png");
		RenderSprite.addRenderer('✕', beacon, 113, 222, 15, 15, this); // cross mark
		RenderSprite.addRenderer('✔', beacon, 90, 223, 16, 14, this); // check mark
	}

	@Override
	public void bindTexture(ResourceLocation location) {
		super.bindTexture(location);
	}

	public static final CustomFontRenderer instance = new CustomFontRenderer();
	private float r;
	private float g;
	private float b;
	private float a;


	public CustomFontRenderer() {
		super(Minecraft.getMinecraft().gameSettings, new ResourceLocation("textures/font/ascii.png"), Minecraft.getMinecraft().renderEngine, false);
		((SimpleReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(this);
		onResourceManagerReload(Minecraft.getMinecraft().getResourceManager());
	}

	@Override
	public int getCharWidth(char chr) {
		ICharRenderer charRenderer = renderOverrides.get(chr);
		if (charRenderer != null)
			return charRenderer.getCharWidth(chr, this);
		else
			return super.getCharWidth(chr);
	}

	@Override
	protected float renderUnicodeChar(char chr, boolean italic) {
		ICharRenderer charRenderer = renderOverrides.get(chr);
		if (charRenderer != null) {
			return charRenderer.renderChar(chr, italic, posX, posY, this);
		} else
			return super.renderUnicodeChar(chr, italic);
	}


	public void resetColor() {
		GL11.glColor4f(r, g, b, a);
	}


	private char n = '\u0378';

	public char getNextBlankChar() {
		do {
			n++;
		} while (glyphWidth[n] != 0 && renderOverrides.containsKey(n));
		return n;
	}

	@Override
	public int drawString(String text, int x, int y, int color, boolean shadow) {
		setBaseColor(color);
		return super.drawString(text, x, y, color, shadow);
	}

	@Override
	public void drawSplitString(String text, int x, int y, int w, int color) {
		setBaseColor(color);
		super.drawSplitString(text, x, y, w, color);
	}

	private void setBaseColor(int color) {
		this.baseColor = color;

		if ((color & 0xfc000000) == 0)
			color |= 0xff000000;

		col_r = (float) (color >> 16 & 255) / 255.0F;
		col_g = (float) (color >> 8 & 255) / 255.0F;
		col_b = (float) (color & 255) / 255.0F;
		col_a = (float) (color >> 24 & 255) / 255.0F;

		color = (color & 16579836) >> 2 | color & -16777216;
		ds_r = (float) (color >> 16 & 255) / 255.0F;
		ds_g = (float) (color >> 8 & 255) / 255.0F;
		ds_b = (float) (color & 255) / 255.0F;
		ds_a = (float) (color >> 24 & 255) / 255.0F;
	}

	@Override
	protected void setColor(float r, float g, float b, float a) {
		if (baseColor != 0) {
			if (r == col_r && g == col_g && b == col_b && a == col_a)
				dropShadow = false;

			if (r == ds_r && g == ds_g && b == ds_b && a == ds_a)
				dropShadow = true;
		}

		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
		super.setColor(r, g, b, a);
	}


	public boolean dropShadow;
	private int baseColor = 0;
	private float col_r;
	private float col_g;
	private float col_b;
	private float col_a;
	private float ds_r;
	private float ds_g;
	private float ds_b;
	private float ds_a;
}
