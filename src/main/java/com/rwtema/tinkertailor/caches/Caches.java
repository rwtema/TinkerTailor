package com.rwtema.tinkertailor.caches;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.rwtema.tinkertailor.DamageEventHandler;
import com.rwtema.tinkertailor.caches.base.WeakItemStackCache;
import com.rwtema.tinkertailor.items.ArmorCore;
import com.rwtema.tinkertailor.modifiers.Modifier;
import com.rwtema.tinkertailor.modifiers.ModifierInstance;
import com.rwtema.tinkertailor.modifiers.ModifierPotion;
import com.rwtema.tinkertailor.modifiers.ModifierRegistry;
import com.rwtema.tinkertailor.nbt.TinkersTailorConstants;
import com.rwtema.tinkertailor.render.textures.ArmorTextureManager;
import com.rwtema.tinkertailor.utils.functions.IntFunction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.tools.ToolMaterial;

public class Caches {

	public static final WeakItemStackCache<Integer> color = new WeakItemStackCache<Integer>() {

		@Nonnull
		@Override
		protected Integer calc(@Nonnull ItemStack stack) {
			NBTTagCompound tags = stack.getTagCompound();

			if (tags != null) {
				tags = stack.getTagCompound().getCompoundTag(TinkersTailorConstants.NBT_MAINTAG);
				int matid = tags.getInteger(TinkersTailorConstants.NBT_MAINTAG_MATERIAL);
				return ArmorTextureManager.getColor(matid);
			}
			return 16777215;
		}

		@Override
		protected Integer getNullValue() {
			return 16777215;
		}
	};

	public static final WeakItemStackCache<Integer> material = new WeakItemStackCache<Integer>() {

		@Nonnull
		@Override
		protected Integer calc(@Nonnull ItemStack stack) {
			if (stack.getItem() instanceof ArmorCore) {
				NBTTagCompound tags = stack.getTagCompound();

				if (tags != null) {
					tags = tags.getCompoundTag(TinkersTailorConstants.NBT_MAINTAG);
					return tags.getInteger(TinkersTailorConstants.NBT_MAINTAG_MATERIAL);
				}
			}
			return -1;
		}
	};

	public static final WeakItemStackCache<List<ModifierInstance>> bonusModifiers = new WeakModifierListCache() {
		@Override
		protected boolean shouldAdd(ItemStack key, ModifierInstance modifierInstance) {
			return modifierInstance.modifier.givesBonusResistance();
		}
	};


	public static final WeakItemStackCache<Boolean> broken = new WeakItemStackCache<Boolean>() {
		@Nonnull
		@Override
		protected Boolean calc(@Nonnull ItemStack key) {
			return ArmorCore.isBroken(key);
		}
	};

	public static final WeakItemStackCache<List<ModifierInstance>> modifiers = new WeakModifierListCache() {

		@Override
		protected boolean shouldAdd(ItemStack key, ModifierInstance modifierInstance) {
			return true;
		}
	};

	public static final WeakItemStackCache<List<ModifierInstance>> allModifiers = new WeakItemStackCache<List<ModifierInstance>>() {
		@Nonnull
		@SuppressWarnings("unchecked")
		@Override
		protected List<ModifierInstance> calc(@Nonnull ItemStack stack) {
			if (!(stack.getItem() instanceof ArmorCore))
				return ImmutableList.of();

			NBTTagCompound tag = stack.getTagCompound();
			if (tag == null)
				return ImmutableList.of();

			boolean isBroken = broken.get(stack);

			NBTTagCompound tags_data = stack.getTagCompound().getCompoundTag(TinkersTailorConstants.NBT_MAINTAG);

			Set<String> keys = (Set<String>) tags_data.func_150296_c();

			List<ModifierInstance> list = new ArrayList<ModifierInstance>(keys.size());
			for (String key : keys) {
				Modifier modifier = ModifierRegistry.modifiers.get(key);
				if (modifier != null) {
					int level = tags_data.getInteger(key);
					if (level != 0 && !(isBroken && modifier.worksIfBroken(level)))
						list.add(new ModifierInstance(modifier, level));
				}
			}

			Collections.sort(list);
			return ImmutableList.copyOf(list);
		}

		@Override
		protected List<ModifierInstance> getNullValue() {
			return ImmutableList.of();
		}
	};

	public static final WeakItemStackCache<Integer> slot = new WeakItemStackCache<Integer>() {
		@Nonnull
		@Override
		protected Integer calc(@Nonnull ItemStack key) {
			if (!(key.getItem() instanceof ArmorCore))
				return -1;
			return ((ArmorCore) key.getItem()).armorType;
		}
	};
	public static final WeakItemStackCache<Integer> maxDurability = new WeakItemStackCache<Integer>() {

		@Nonnull
		@Override
		protected Integer calc(@Nonnull ItemStack stack) {
			Integer matID = material.get(stack);
			if (matID == -1) return 0;
			ToolMaterial toolMaterial = TConstructRegistry.toolMaterials.get(matID);
			int durability = toolMaterial.durability();

			Integer slot = Caches.slot.get(stack);
			durability = (int) Math.ceil(durability * ArmorCore.ratings[slot] / 0.5F);

			for (ModifierInstance modifierInstance : modifiers.get(stack)) {
				durability += modifierInstance.modifier.durabilityBoost(stack, modifierInstance.level);
			}

			if (durability <= 0) return 1;
			if (durability > 32767) return 32767;
			return durability;
		}
	};

	public static final WeakItemStackCache<Float> damageResistance = new WeakItemStackCache<Float>() {
		@Nonnull
		@Override
		protected Float calc(@Nonnull ItemStack stack) {
			return (float) (DamageEventHandler.matDRcache.get(Caches.material.get(stack)) * ArmorCore.ratings[slot.get(stack)]) * 4;
		}
	};

	public static final WeakItemStackCache<Multimap<String, AttributeModifier>> attributes = new WeakItemStackCache<Multimap<String, AttributeModifier>>() {
		@Nonnull
		@Override
		protected Multimap<String, AttributeModifier> calc(@Nonnull ItemStack key) {
			Multimap<String, AttributeModifier> map = HashMultimap.create();
			List<ModifierInstance> modifierInstances = modifiers.get(key);
			for (ModifierInstance modifier : modifierInstances) {
				modifier.modifier.addAttributes(key, slot.get(key), modifier.level, map);
			}

			return map;
		}
	};

	public static final WeakItemStackCache<List<ModifierInstance>> tickers = new WeakModifierListCache() {
		@Override
		protected boolean shouldAdd(ItemStack key, ModifierInstance modifierInstance) {
			return modifierInstance.modifier.doesTick(key, modifierInstance.level);
		}
	};

	public static final WeakItemStackCache<List<ModifierInstance>> potionModifiers = new WeakModifierListCache() {
		@Override
		protected boolean shouldAdd(ItemStack key, ModifierInstance modifierInstance) {
			return modifierInstance.modifier instanceof ModifierPotion;
		}
	};

	public static final WeakItemStackCache<Collection<Integer>> potionIds = new WeakItemStackCache<Collection<Integer>>() {
		@Nonnull
		@Override
		protected Collection<Integer> calc(@Nonnull ItemStack key) {
			if (!(key.getItem() instanceof ArmorCore))
				return ImmutableList.of();
			HashSet<Integer> iSet = null;
			List<ModifierInstance> modifiers = Caches.potionModifiers.get(key);
			for (ModifierInstance modifier : modifiers) {
				if (iSet == null) iSet = new HashSet<Integer>(modifiers.size());
				iSet.add(((ModifierPotion) modifier.modifier).potion.id);
			}
			if (iSet == null) return ImmutableList.of();
			return iSet;
		}
	};


	public static final WeakItemStackCache<Integer> visBoost = new WeakItemStackCache<Integer>() {
		@Nonnull
		@Override
		protected Integer calc(@Nonnull ItemStack itemstack) {
			int armorType = slot.get(itemstack);

			if (armorType == -1)
				return 0;

			int modifier = 0;
			if (armorType == 0 && ModifierRegistry.gogglesRevealing.level.get(itemstack) > 0) {
				modifier += 5;
			}
			int level = ModifierRegistry.visBoost.level.get(itemstack);
			if (level > 0) {
				modifier += (level * 5) / ModifierRegistry.visBoost.getMaxLevel();
			}
			return modifier;
		}
	};

	@SuppressWarnings("unchecked")
	public static IntFunction<ItemStack> getMaxEnergy = IntFunction.zeroFunction;
	@SuppressWarnings("unchecked")
	public static IntFunction<ItemStack> getCurEnergy = IntFunction.zeroFunction;

	private abstract static class WeakModifierListCache extends WeakItemStackCache<List<ModifierInstance>> {
		@Nonnull
		@Override
		protected List<ModifierInstance> calc(@Nonnull ItemStack key) {
			List<ModifierInstance> modifierInstances = allModifiers.get(key);
			if (modifierInstances.isEmpty()) return modifierInstances;

			boolean isBroken = broken.get(key);

			ArrayList<ModifierInstance> list = new ArrayList<ModifierInstance>();
			for (ModifierInstance modifierInstance : modifierInstances) {
				if (shouldAdd(key, modifierInstance))
					if (!isBroken || modifierInstance.modifier.worksIfBroken(modifierInstance.level))
						list.add(modifierInstance);
			}

			return ImmutableList.copyOf(list);
		}

		protected abstract boolean shouldAdd(ItemStack key, ModifierInstance modifierInstance);
	}
}
