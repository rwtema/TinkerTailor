package com.rwtema.tinkertailor.modifiers;

import com.rwtema.tinkertailor.utils.oremapping.ItemValueMap;
import net.minecraft.item.ItemStack;

public class ModifierSimple extends Modifier {
	public ModifierSimple(String name, int maxLevel, ItemValueMap... itemStacks) {
		super(name, maxLevel, itemStacks);

	}

	public boolean hasEffect(ItemStack itemStack) {
		return level.get(itemStack) > 0;
	}
}
