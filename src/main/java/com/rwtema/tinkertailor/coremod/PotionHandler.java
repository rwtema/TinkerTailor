package com.rwtema.tinkertailor.coremod;

import com.rwtema.tinkertailor.caches.Caches;
import java.util.HashMap;
import java.util.HashSet;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class PotionHandler {
	public static void spawnParticle(World world, String particle, double x, double y, double z, double r, double g, double b, EntityLivingBase entity, HashMap<Integer, PotionEffect> activePotions) {
		HashSet<Integer> ids = new HashSet<Integer>();
		ids.addAll(activePotions.keySet());
		for (ItemStack itemStack : entity.getLastActiveItems()) {
			if (itemStack != null)
				ids.removeAll(Caches.potionIds.get(itemStack));
		}

		if (!ids.isEmpty())
			world.spawnParticle(particle, x, y, z, r, g, b);
	}


}
