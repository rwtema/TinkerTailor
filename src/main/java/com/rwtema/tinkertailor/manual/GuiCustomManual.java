package com.rwtema.tinkertailor.manual;

import mantle.books.BookData;
import mantle.client.gui.GuiManual;
import net.minecraft.item.ItemStack;

public class GuiCustomManual extends GuiManual {
	public static ItemStack tooltip = null;
	public final BookData data;

	public GuiCustomManual(ItemStack stack, BookData data) {
		super(stack, data);
		this.data = data;
	}

	@Override
	public void drawScreen(int x, int y, float partial) {
		tooltip = null;
		super.drawScreen(x, y, partial);
		if (tooltip != null) {
			renderToolTip(tooltip, x, y);
			tooltip = null;
		}
	}

}
