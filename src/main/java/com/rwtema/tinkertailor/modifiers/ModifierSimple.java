package com.rwtema.tinkertailor.modifiers;

import com.rwtema.tinkertailor.modifiers.itemmodifier.ModArmorModifier;
import net.minecraft.item.ItemStack;
import tconstruct.library.modifier.ItemModifier;

public class ModifierSimple extends Modifier {
	private final ItemStack[] itemStacks;

	protected ModifierSimple(String name, int maxLevel, ItemStack... itemStacks) {
		super(name, maxLevel, itemStacks);
		this.itemStacks = itemStacks;
	}

	@Override
	public ItemModifier createItemModifier() {
		return new ModArmorModifier(this, itemStacks);
	}
}
