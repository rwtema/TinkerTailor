package com.rwtema.tinkertailor.manual;

import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;
import com.rwtema.tinkertailor.modifiers.Modifier;
import com.rwtema.tinkertailor.modifiers.ModifierRegistry;
import com.rwtema.tinkertailor.nbt.TinkersTailorConstants;
import com.rwtema.tinkertailor.utils.Lang;
import com.rwtema.tinkertailor.utils.oremapping.ItemValueMap;
import cpw.mods.fml.common.Loader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;
import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

public class PageModifier extends PageBase {


	Modifier modifier;
	String desc;

	Random rand = new Random();

	@Override
	protected void render(boolean isTranslatable) {

		drawCenteredString("\u00a7n" + modifier.getLocalizedName(), 4);

		rand.setSeed(curTime());

		ItemValueMap[] recipe = modifier.itemModifier.recipe;
		if (recipe.length == 0) {

		} else if (recipe.length == 1) {

			List<ItemStack> stacks = new ArrayList<ItemStack>();
			for (ItemValueMap map : recipe) {
				map.addItemsToList(stacks);
			}

			TreeMultimap<Integer, ItemStack> maps = TreeMultimap.create(Ordering.natural(), new Comparator<ItemStack>() {
				@Override
				public int compare(ItemStack o1, ItemStack o2) {
					int compare = Double.compare(o1.getItem().hashCode(), o2.getItem().hashCode());
					if (compare != 0) return compare;
					return -Double.compare(o1.getItemDamage(), o2.getItemDamage());
				}
			});

			for (Iterator<ItemStack> iterator = stacks.iterator(); iterator.hasNext(); ) {
				ItemStack stack = iterator.next();
				int value = modifier.itemModifier.getValue(stack);
				if (value <= 0) iterator.remove();
				else {
					maps.put(value, stack);
				}
			}


			Integer[] keys = maps.keySet().toArray(new Integer[maps.keySet().size()]);

			if (keys.length != 0) {
				Integer i = keys[curTime() % keys.length];

				NavigableSet<ItemStack> collection = maps.get(i);
				ItemStack[] itemStacks = collection.toArray(new ItemStack[collection.size()]);
				ItemStack item = itemStacks[(curTime() / keys.length) % itemStacks.length];

				startRenderingItem();

				String s = Lang.translate("Value") + ": " + i;

				int x = (178 - 18 - 2 - manual.fonts.getStringWidth(s)) / 2;
				renderStack(item, x, 20);
				manual.fonts.drawString(s, x + 20, 24, 0);

				stopRenderingItem();

			}

		} else {

			startRenderingItem();

			int x = (PAGE_WIDTH - recipe.length * 18) / 2;
			for (int i = 0; i < recipe.length; i++) {
				ItemValueMap map = recipe[i];
				if (map == null) continue;

				ArrayList<ItemStack> items = new ArrayList<ItemStack>();
				map.addItemsToList(items);
				ItemStack[] itemStacks = items.toArray(new ItemStack[items.size()]);
				if (itemStacks.length == 0) continue;
				ItemStack item = itemStacks[rand.nextInt(itemStacks.length)];
				renderStack(item, x + i * 18, 20);
			}

			stopRenderingItem();

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

		yPos += drawTextLine("- " + Lang.translate("Item Restrictions") + ": " + restText, 4, yPos);

		if (modifier.requiredMods != null) {
			StringBuilder builder = new StringBuilder().append("- ").append(Lang.translate("Requires Mods")).append(": ");
			boolean flag = false;
			for (String requiredMod : modifier.requiredMods) {
				boolean missing = !Loader.isModLoaded(requiredMod);
				if (flag) builder.append(", ");
				else flag = true;

				if (missing)
					builder.append(EnumChatFormatting.RED);

				builder.append(requiredMod);

				if (missing) {
					builder.append(" (").append(Lang.translate("Missing")).append(")").append(EnumChatFormatting.BLACK);
				}


			}
			yPos += drawTextLine(builder.toString(), 4, yPos);
		}
	}


	@Override
	protected void loadData() throws IOException {
		String modifierName = loadText("modifier");
		modifier = ModifierRegistry.modifiers.get(modifierName);
		if (modifier == null) throw new IOException("Modifier Does Not Exist");
		desc = loadText("desc");
	}
}
