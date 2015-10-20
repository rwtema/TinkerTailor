package com.rwtema.tinkertailor.modifiers;

import com.rwtema.tinkertailor.utils.oremapping.OreIntMap;
import net.minecraft.item.ItemStack;

public class ModifierSimple extends Modifier {
	protected ModifierSimple(String name, int maxLevel, OreIntMap... itemStacks) {
		super(name, maxLevel, itemStacks);

	}

	public boolean hasEffect(ItemStack itemStack) {
		return level.get(itemStack) > 0;
	}
}
