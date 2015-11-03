package com.rwtema.tinkertailor.modifiers;

import com.rwtema.tinkertailor.utils.oremapping.ItemValueMap;
import net.minecraft.item.ItemStack;

public class ModifierDurability extends Modifier {
	private final int maxDurability;

	protected ModifierDurability(String name, int maxLevel, int modifierStep, int maxDurability, ItemValueMap... recipe) {
		super(name, maxLevel * modifierStep, recipe);
		this.maxDurability = maxDurability;
		this.modifierStep = modifierStep;
		setNegativeMaloderous();
	}

	@Override
	public int durabilityBoost(ItemStack stack, int level) {
		return (level * maxDurability) / maxLevel;
	}
}
