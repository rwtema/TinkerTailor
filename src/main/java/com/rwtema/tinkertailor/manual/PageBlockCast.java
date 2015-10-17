package com.rwtema.tinkertailor.manual;

import com.rwtema.tinkertailor.items.ArmorCore;
import java.util.ArrayList;
import mantle.lib.client.MantleClientRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.oredict.OreDictionary;
import org.w3c.dom.NodeList;
import tconstruct.library.TConstructRegistry;

public class PageBlockCast extends PageBase {
	private static final ResourceLocation background = new ResourceLocation("tinker", "textures/gui/booksmeltery.png");
	String text;
	ItemStack[] icons;

	@Override
	public void loadData() {
		NodeList nodes = element.getElementsByTagName("text");
		if (nodes != null)
			text = nodes.item(0).getTextContent();

		nodes = element.getElementsByTagName("recipe");
		if (nodes != null)
			icons = MantleClientRegistry.getRecipeIcons(nodes.item(0).getTextContent());
	}

	@Override
	public void render(boolean isTranslatable) {
		if (text != null) {
			if (isTranslatable)
				text = StatCollector.translateToLocal(text);
			drawCenteredString("\u00a7n" + text, 4);
			//manual.fonts.drawString("\u00a7n" + text, localWidth + 70, localHeight + 4, 0);
		}

		ItemStack ingred;
		if (icons[2] != null && icons[2].getItemDamage() == OreDictionary.WILDCARD_VALUE && icons[2].getItem() instanceof ArmorCore) {
			ArrayList<Integer> materials = new ArrayList<Integer>(TConstructRegistry.toolMaterials.keySet());
			int mat = materials.get(curTime() % materials.size());
			ingred = ((ArmorCore) icons[2].getItem()).createDefaultStack(mat);
		} else ingred = icons[2];

		startRenderingItem();

		renderStack(icons[0], 138, 110, 2);
		renderStack(icons[1], 70, 74, 2);
		renderStack(ingred, 70, 110, 2);

		stopRenderingItem();

		String ingr = StatCollector.translateToLocal("manual.page.casting1");
		if (isTranslatable)
			ingr = StatCollector.translateToLocal(ingr);
		int y = 32;
		y += drawTextBlock(ingr + ":", 110, y);
		y += drawTextBlock("- " + icons[1].getDisplayName(), 110, y);
		if (ingred != null)
			drawTextBlock("- " + ingred.getDisplayName(), 110, y);

	}

	@Override
	public void renderBackgroundLayer(int localWidth, int localHeight) {
		manual.getMC().getTextureManager().bindTexture(background);
		manual.drawTexturedModalRect(localWidth, localHeight + 32, 0, 0, 174, 115);
		manual.drawTexturedModalRect(localWidth + 62, localHeight + 105, 2, 118, 45, 45);
	}
}
