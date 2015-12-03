package com.rwtema.tinkertailor.coremod;

import com.rwtema.tinkertailor.modifiers.ModifierPotion;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

@SuppressWarnings("unused")
public class AltPotionHandler {
	public static int isPotionActive(EntityLivingBase base, int potionID) {
		if (getActivePotionEffect(base, potionID) != null)
			return 1;
		return -1;
	}

	public static PotionEffect getActivePotionEffect(EntityLivingBase base, Potion potion) {
		return getActivePotionEffect(base, potion.id);
	}

	public static PotionEffect getActivePotionEffect(EntityLivingBase base, int id) {
		ModifierPotion modifier = ModifierPotion.modifiers[id];
		if (modifier == null) return null;

		int level = modifier.multiLevelMax.get(base.getLastActiveItems());

		if (level == 0) return null;

		return modifier.getEffect(level);
	}
}
