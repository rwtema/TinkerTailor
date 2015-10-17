package com.rwtema.tinkertailor.manual;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemStack;

public class GuiPassthrough extends GuiScreen {
	public static final GuiPassthrough instance = new GuiPassthrough();


	public static void drawToolTip(ItemStack stack, int x, int y) {
		Minecraft mc = Minecraft.getMinecraft();
		final ScaledResolution scaledresolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
		instance.mc = mc;
		instance.fontRendererObj = mc.fontRenderer;
		instance.width = scaledresolution.getScaledWidth();
		instance.height = scaledresolution.getScaledHeight();
		instance.renderToolTip(stack, x, y);

	}


}
