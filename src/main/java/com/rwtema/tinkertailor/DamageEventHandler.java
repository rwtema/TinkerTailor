package com.rwtema.tinkertailor;

import com.rwtema.tinkertailor.caches.Caches;
import com.rwtema.tinkertailor.items.ArmorCore;
import com.rwtema.tinkertailor.modifiers.ModifierInstance;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import gnu.trove.map.hash.TIntDoubleHashMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.tools.ToolMaterial;

public class DamageEventHandler {

	private static final float MIN_REDUCTION = 0.2F;

	public static final DamageEventHandler instance = new DamageEventHandler();

	public void register() {
		FMLCommonHandler.instance().bus().register(this);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void livingHurt(LivingHurtEvent event) {
		float dR = 0;


		float bonusResistance = 0;
		for (ItemStack itemStack : event.entityLiving.getLastActiveItems()) {
			if (itemStack != null && itemStack.getItem() instanceof ArmorCore) {
				ArmorCore armorCore = (ArmorCore) itemStack.getItem();
				dR += armorCore.getDamageResistance(itemStack);

				for (ModifierInstance modifierInstance : Caches.modifiers.get(itemStack)) {
					bonusResistance += modifierInstance.modifier.getBonusResistance(event.entityLiving, event.source, event.ammount, itemStack, armorCore.armorType, modifierInstance.level);
				}
			}
		}

		dR = MathHelper.clamp_float(dR / 100F, 0F, 0.8F);
		bonusResistance = MathHelper.clamp_float(bonusResistance / 100F, 0, 0.8F);

		float damage = event.ammount;
		if (!event.source.isUnblockable()) {
			damage = damage * (1 - dR);
		}

		damage = damage * (1 - bonusResistance);

		event.ammount = damage;
	}

	@SuppressWarnings("ExternalizableWithoutPublicNoArgConstructor")
	public static final TIntDoubleHashMap matDRcache = new TIntDoubleHashMap() {
		@Override
		public double getNoEntryValue() {
			return -1;
		}

		@Override
		public double get(int key) {
			double i = super.get(key);
			if (i <= 0) {
				i = 0;
				ToolMaterial toolMaterial = TConstructRegistry.toolMaterials.get(key);
				if (toolMaterial != null) {
					i = materialDR.get(toolMaterial);
				}
				put(key, i);
			}
			return i;
		}
	};


	@SuppressWarnings("ExternalizableWithoutPublicNoArgConstructor")
	public static final TObjectDoubleHashMap<ToolMaterial> materialDR = new TObjectDoubleHashMap<ToolMaterial>(10, 0.5F, -1) {
		@Override
		public double get(Object key) {
			double v = super.get(key);
			if (v == -1) {
				v = getBaseDR((ToolMaterial) key);
				put((ToolMaterial) key, v);
			}

			return v;
		}
	};

	private static double getBaseDR(ToolMaterial material) {
		double q = getRawQ(material);

		return 25 / (1 + Math.exp(-q));
	}

	public static double getRawQ(ToolMaterial material) {
		int durability = MathHelper.clamp_int(material.durability(), 1, 1600);

		return -8.8308398829
				+ 0.0006962965 * material.toolSpeed()
				+ 0.7126838194 * material.handleDurability()
				- 0.0036839928 * durability
				+ 0.5798563938 * material.harvestLevel()
				+ 1.3925798111 * Math.log(durability);
	}
}
