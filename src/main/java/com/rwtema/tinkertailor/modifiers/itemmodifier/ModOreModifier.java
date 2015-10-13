package com.rwtema.tinkertailor.modifiers.itemmodifier;

import com.rwtema.tinkertailor.modifiers.Modifier;
import com.rwtema.tinkertailor.utils.oremapping.OreIntMap;
import net.minecraft.item.ItemStack;

public class ModOreModifier extends ModArmorModifier {

	final OreIntMap oreMap;

	public ModOreModifier(Modifier modifier, OreIntMap oreMap) {
		super(modifier, oreMap.makeItemStackList());
		this.oreMap = oreMap;
	}

	@Override
	protected int totalValue(ItemStack[] recipe) {
		int v = 0;
		for (ItemStack itemStack : recipe) {
			if (itemStack != null)
				v += getValue(itemStack);
		}
		return v;
	}

	public int getValue(ItemStack itemStack) {
		return oreMap.get(itemStack);
	}

	@Override
	public boolean matches(ItemStack[] recipe, ItemStack input) {
		if (!canModify(input, recipe))
			return false;

		boolean canCraft = false;
		for (ItemStack craftingStack : recipe) {
			if (craftingStack != null) {
				canCraft = true;

				if (getValue(craftingStack) <= 0)
					return false;
			}
		}

		return canCraft;
	}
}
