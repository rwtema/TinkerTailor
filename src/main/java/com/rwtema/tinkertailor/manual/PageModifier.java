package com.rwtema.tinkertailor.manual;

import com.rwtema.tinkertailor.modifiers.Modifier;
import com.rwtema.tinkertailor.modifiers.ModifierRegistry;
import com.rwtema.tinkertailor.modifiers.itemmodifier.ModOreModifier;
import com.rwtema.tinkertailor.nbt.TinkersTailorConstants;
import com.rwtema.tinkertailor.utils.Lang;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.oredict.OreDictionary;

public class PageModifier extends PageBase {

	String text;
	Modifier modifier;
	String desc;

	Random rand = new Random();

	@Override
	protected void render(boolean isTranslatable) {

		drawCenteredString("\u00a7n" + modifier.getLocalizedName(), 4);

		if (modifier.itemModifier instanceof ModOreModifier) {
			if (!modifier.itemModifier.stacks.isEmpty()) {

				ModOreModifier itemModifier = (ModOreModifier) modifier.itemModifier;

				List<ItemStack> stacks = new ArrayList<ItemStack>(modifier.itemModifier.stacks);
				for (Iterator<ItemStack> iterator = stacks.iterator(); iterator.hasNext(); ) {
					ItemStack stack = iterator.next();
					if (itemModifier.getValue(stack) <= 0) iterator.remove();
				}

				if (!stacks.isEmpty()) {
					startRenderingItem();

					ItemStack itemStack = stacks.get(curTime() % stacks.size());

					String s = Lang.translate("Value") + ": " + itemModifier.getValue(itemStack);

					int x = (178 - 18 - 2 - manual.fonts.getStringWidth(s)) / 2;
					renderStack(itemStack, x, 20);
					manual.fonts.drawString(s, x + 20, 24, 0);

					stopRenderingItem();
				}
			}
		} else {
			List stacks = modifier.itemModifier.stacks;
			if (!stacks.isEmpty()) {
				startRenderingItem();

				rand.setSeed(curTime());
				int x = (PAGE_WIDTH - stacks.size() * 18) / 2;
				for (int i = 0; i < stacks.size(); i++) {
					ItemStack item = (ItemStack) stacks.get(i);
					if (item == null || item.getItem() == null) continue;

					if (item.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
						ArrayList<ItemStack> list = new ArrayList<ItemStack>();
						item.getItem().getSubItems(item.getItem(), item.getItem().getCreativeTab(), list);
						if (!list.isEmpty()) {
							item = list.get(rand.nextInt(list.size()));
						}
					}

					renderStack(item, x + i * 18, 20);
				}

				stopRenderingItem();
			}
		}

		int yPos = 42;
		yPos += drawTextBlock(desc, 4, yPos);
		yPos += drawBlankLine();
		for (String s : modifier.getDocLines()) {
			yPos += drawTextBlock(s, 4, yPos);
		}
		yPos += drawTextLine("- " + Lang.translate("Max Level") + ": " + (modifier.getMaxLevel() / modifier.getModifierStep()), 4, yPos);

		yPos += drawTextLine("- " + Lang.translate("Cost per Level") + ": " + modifier.getModifierStep(), 4, yPos);
		if (!modifier.itemModifier.useModifiers)
			yPos += drawTextLine("- " + Lang.translate("Does not use up modifiers"), 4, yPos);
		yPos += drawBlankLine();
		int allowedArmorTypes = modifier.allowedArmorTypes;
		String restText;
		if (allowedArmorTypes == 0) {
			restText = Lang.translate("None");
		} else {
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < 4; i++) {
				if ((allowedArmorTypes & (1 << i)) == 0) {
					builder.append(StatCollector.translateToLocal(TinkersTailorConstants.UNLOCAL_NAMES[i])).append(' ');
				}
			}
			builder.append(Lang.translate("Only"));
			restText = builder.toString();
		}

		drawTextLine("- " + Lang.translate("Item Restrictions") + ": " + restText, 4, yPos);

	}


	@Override
	protected void loadData() throws IOException {
		text = loadText("text");
		String modifierName = loadText("modifier");
		modifier = ModifierRegistry.modifiers.get(modifierName);
		assert modifier != null;

		desc = loadText("desc");
	}
}
