package com.rwtema.tinkertailor.modifiers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public interface IArmorModifierTick {
	public void onArmorTick(EntityLivingBase entity, ItemStack item, int slot, int level);
}
