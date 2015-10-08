package com.rwtema.tinkertailor.render.font;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class FontRendererShields extends FontRenderer {
	public final static char shield_char = '\u26e8';

	public static final FontRendererShields instance = new FontRendererShields();
	private float r;
	private float g;
	private float b;
	private float a;

	public FontRendererShields() {
		super(Minecraft.getMinecraft().gameSettings, new ResourceLocation("textures/font/ascii.png"), Minecraft.getMinecraft().renderEngine, false);
		((SimpleReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(this);
		onResourceManagerReload(Minecraft.getMinecraft().getResourceManager());
	}

	@Override
	public int getCharWidth(char chr) {
		return chr == shield_char ? 8 : super.getCharWidth(chr);
	}

	@Override
	protected float renderUnicodeChar(char p_78277_1_, boolean p_78277_2_) {
		if (p_78277_1_ == shield_char) {
			return drawShield(p_78277_2_);
		} else
			return super.renderUnicodeChar(p_78277_1_, p_78277_2_);
	}

	@Override
	protected void setColor(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
		super.setColor(r, g, b, a);
	}

	private float drawShield(boolean p_78277_2_) {

		GL11.glColor4f(1, 1, 1, 1);
		bindTexture(Gui.icons);

		float u = 34;
		float v = 9;
		float w = 9 - 0.02F;
		float h = 9 - 0.02F;

		float f5 = p_78277_2_ ? 1.0F : 0.0F;
		GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
		GL11.glTexCoord2f(u / 256.0F, v / 256.0F);
		GL11.glVertex3f(this.posX + f5, this.posY, 0.0F);
		GL11.glTexCoord2f(u / 256.0F, (v + h) / 256.0F);
		GL11.glVertex3f(this.posX - f5, this.posY + 7.99F, 0.0F);
		GL11.glTexCoord2f((u + w) / 256.0F, v / 256.0F);
		GL11.glVertex3f(this.posX + 7.99F + f5, this.posY, 0.0F);
		GL11.glTexCoord2f((u + w) / 256.0F, (v + h) / 256.0F);
		GL11.glVertex3f(this.posX + 7.99F - f5, this.posY + 7.99F, 0.0F);
		GL11.glEnd();

		bindTexture(locationFontTexture);
		GL11.glColor4f(r, g, b, a);

		return (8 + 0.02F);
	}
}
