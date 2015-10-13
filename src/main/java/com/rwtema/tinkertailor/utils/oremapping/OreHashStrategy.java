package com.rwtema.tinkertailor.utils.oremapping;

import gnu.trove.strategy.HashingStrategy;
import net.minecraft.item.ItemStack;

public class OreHashStrategy implements HashingStrategy<ItemStack> {
	public final static OreHashStrategy INSTANCE = new OreHashStrategy();

	@Override
	public int computeHashCode(ItemStack itemstack) {
		return itemstack.getItem().hashCode() * 31 + itemstack.getItemDamage();
	}

	@Override
	public boolean equals(ItemStack o1, ItemStack o2) {
		return o2.getItem() == o1.getItem() && o2.getItemDamage() == o1.getItemDamage();
	}
}
