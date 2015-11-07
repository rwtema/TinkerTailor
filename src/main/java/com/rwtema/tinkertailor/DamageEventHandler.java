package com.rwtema.tinkertailor;

import com.rwtema.tinkertailor.caches.Caches;
import com.rwtema.tinkertailor.items.ArmorCore;
import com.rwtema.tinkertailor.modifiers.ModifierInstance;
import com.rwtema.tinkertailor.nbt.TinkersTailorConstants;
import com.rwtema.tinkertailor.utils.functions.ICallableClient;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.TIntDoubleHashMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.tools.ToolMaterial;

public class DamageEventHandler {

	public static final DamageEventHandler instance = new DamageEventHandler();
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
	private static final float MIN_REDUCTION = 0.2F;
	private static TIntArrayList orderedMaterials = null;

	private static double getBaseDR(ToolMaterial material) {
		double q = getRawQ(material);

		return 23 / (1 + Math.exp(-q));
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

	public static TIntArrayList getOrderedMaterialList() {
		if (orderedMaterials == null) {
			TreeMap<Double, Integer> materialTreeMap = new TreeMap<Double, Integer>();
			for (Map.Entry<Integer, ToolMaterial> integerToolMaterialEntry : TConstructRegistry.toolMaterials.entrySet()) {
				double v = matDRcache.get(integerToolMaterialEntry.getKey());
				materialTreeMap.put(v, integerToolMaterialEntry.getKey());
			}

			Collection<Integer> values = materialTreeMap.values();
			orderedMaterials = new TIntArrayList();
			orderedMaterials.addAll(values);
		}
		return orderedMaterials;
	}

	public static int getRandomMaterial(Random random) {
		TIntArrayList orderedMaterials = getOrderedMaterialList();
		return orderedMaterials.get((int) (random.nextFloat() * (1-random.nextFloat()) * random.nextFloat() * orderedMaterials.size()));
	}

	public void register() {
		FMLCommonHandler.instance().bus().register(this);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void livingHurt(final LivingHurtEvent event) {
		if (event.ammount == 0) return;

		float dR = 0;

		float[] dRR = new float[4];
		float[] bRR = new float[4];

		float bR = 0;
		for (ItemStack itemStack : event.entityLiving.getLastActiveItems()) {
			if (itemStack != null && itemStack.getItem() instanceof ArmorCore) {
				if (ArmorCore.isBroken(itemStack)) continue;
				ArmorCore armorCore = (ArmorCore) itemStack.getItem();


				float damageResistance = armorCore.getDamageResistance(itemStack);
				dRR[armorCore.armorType] += damageResistance;
				dR += damageResistance;

				for (ModifierInstance modifierInstance : Caches.modifiers.get(itemStack)) {
					float bonusResistance = modifierInstance.modifier.getBonusResistance(event.entityLiving, event.source, event.ammount, itemStack, armorCore.armorType, modifierInstance.level);
					bRR[armorCore.armorType] += Math.max(bonusResistance, 0);
					bR += bonusResistance;
				}
			}
		}

		float[] damageToDistribute = new float[4];

		float damage = event.ammount;
		float prevDamage;
		if (dR > 0 && !event.source.isUnblockable()) {
			prevDamage = damage;
			damage = damage * (1 - MathHelper.clamp_float(dR / 100F, 0F, 0.95F));
			assignDamage(dR, dRR, damageToDistribute, damage, prevDamage);
		}

		if (bR > 0) {
			prevDamage = damage;
			damage = damage * (1 - Math.min(bR / 100F, 0.8F));
			assignDamage(bR, bRR, damageToDistribute, damage, prevDamage);
		} else if (bR < 0) {
			damage = damage * (1 - bR / 100F);
		}

		if (bR == 0 && dR == 0) return;


		for (ItemStack itemStack : event.entityLiving.getLastActiveItems()) {
			if (itemStack != null && itemStack.getItem() instanceof ArmorCore) {
				ArmorCore item = (ArmorCore) itemStack.getItem();
				final int slot = item.armorType;
				int dmg = (int) Math.ceil(damageToDistribute[slot]);
				item.damage(event.entityLiving, itemStack, event.source, dmg);

				TinkersTailor.proxy.run(new ICallableClient() {
					@Override
					public void run() {
						ArmorCore.armors[slot].getModelArmor().setInvisible(event.entityLiving, false);
					}
				});
			}
		}

		event.ammount = damage;
	}

	private void assignDamage(float dR, float[] dRR, float[] damageToDistribute, float damage, float prevDamage) {
		float r = (prevDamage - damage) / dR;

		for (int i = 0; i < 4; i++) {
			damageToDistribute[i] += r * dRR[i];
		}
	}

	public static int getRandomMaterial() {
		return getRandomMaterial(TinkersTailorConstants.RANDOM);
	}
}
