package com.rwtema.tinkertailor.caches;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.rwtema.tinkertailor.DamageEventHandler;
import com.rwtema.tinkertailor.items.ArmorCore;
import com.rwtema.tinkertailor.modifiers.Modifier;
import com.rwtema.tinkertailor.modifiers.ModifierInstance;
import com.rwtema.tinkertailor.modifiers.ModifierPotion;
import com.rwtema.tinkertailor.modifiers.ModifierRegistry;
import com.rwtema.tinkertailor.nbt.TinkersTailorConstants;
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

	public static WeakCache<ItemStack, Integer> color = new WeakCache<ItemStack, Integer>() {

		@Override
		protected Integer calc(@Nonnull ItemStack stack) {
			NBTTagCompound tags = stack.getTagCompound();

			if (tags != null) {
				tags = stack.getTagCompound().getCompoundTag(TinkersTailorConstants.NBT_MAINTAG);
				tconstruct.library.tools.ToolMaterial material = TConstructRegistry.toolMaterials.get(tags.getInteger(TinkersTailorConstants.NBT_MAINTAG_RENDERID));
				if (material != null)
					return material.primaryColor;
			}
			return 16777215;
		}

		@Override
		protected Integer getNullValue() {
			return 16777215;
		}
	};

	public static WeakCache<ItemStack, Integer> material = new WeakCache<ItemStack, Integer>() {

		@Override
		protected Integer calc(@Nonnull ItemStack stack) {
			NBTTagCompound tags = stack.getTagCompound();

			if (tags != null) {
				tags = tags.getCompoundTag(TinkersTailorConstants.NBT_MAINTAG);
				return tags.getInteger(TinkersTailorConstants.NBT_MAINTAG_MATERIAL);
			}
			return -1;
		}
	};
	public static WeakCache<ItemStack, List<ModifierInstance>> modifiers = new WeakCache<ItemStack, List<ModifierInstance>>() {
		@SuppressWarnings("unchecked")
		@Override
		protected List<ModifierInstance> calc(@Nonnull ItemStack stack) {
			if (!(stack.getItem() instanceof ArmorCore))
				return ImmutableList.of();

			NBTTagCompound tag = stack.getTagCompound();
			if (tag == null)
				return ImmutableList.of();


			NBTTagCompound datatags = stack.getTagCompound().getCompoundTag(TinkersTailorConstants.NBT_MAINTAG);

			Set<String> keys = (Set<String>) datatags.func_150296_c();

			List<ModifierInstance> list = new ArrayList<ModifierInstance>(keys.size());
			for (String key : keys) {
				Modifier modifier = ModifierRegistry.modifiers.get(key);
				if (modifier != null) {
					int level = datatags.getInteger(key);
					if (level != 0)
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
	public static WeakCache<ItemStack, Integer> slot = new WeakCache<ItemStack, Integer>() {
		@Override
		protected Integer calc(@Nonnull ItemStack key) {
			return ((ArmorCore) key.getItem()).armorType;
		}
	};
	public static WeakCache<ItemStack, Integer> maxDurability = new WeakCache<ItemStack, Integer>() {

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

			return durability;
		}
	};

	public static WeakCache<ItemStack, Float> damageResistance = new WeakCache<ItemStack, Float>() {
		@Override
		protected Float calc(@Nonnull ItemStack stack) {
			return (float) (DamageEventHandler.matDRcache.get(Caches.material.get(stack)) * ArmorCore.ratings[slot.get(stack)]) * 4;
		}
	};

	public static WeakCache<ItemStack, Multimap<String, AttributeModifier>> attributes = new WeakCache<ItemStack, Multimap<String, AttributeModifier>>() {
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

	public static WeakCache<ItemStack, List<ModifierInstance>> tickers = new WeakCache<ItemStack, List<ModifierInstance>>() {
		@Override
		protected List<ModifierInstance> calc(@Nonnull ItemStack key) {
			List<ModifierInstance> modifierInstances = modifiers.get(key);
			if (modifierInstances.isEmpty()) return ImmutableList.of();

			List<ModifierInstance> result = new ArrayList<ModifierInstance>(modifierInstances.size());
			for (ModifierInstance modifierInstance : modifierInstances) {
				if (modifierInstance.modifier.doesTick(key, modifierInstance.level)) {
					result.add(modifierInstance);
				}
			}
			if (result.isEmpty()) return ImmutableList.of();
			return result;
		}
	};

	public static WeakCache<ItemStack, Collection<Integer>> potionIds = new WeakCache<ItemStack, Collection<Integer>>() {
		@Override
		protected Collection<Integer> calc(@Nonnull ItemStack key) {
			if (key == null || !(key.getItem() instanceof ArmorCore))
				return ImmutableList.of();
			HashSet<Integer> iSet = null;
			List<ModifierInstance> modifiers = Caches.modifiers.get(key);
			for (ModifierInstance modifier : modifiers) {
				if (modifier.modifier instanceof ModifierPotion) {
					if (iSet == null) iSet = new HashSet<Integer>(modifiers.size());
					iSet.add(((ModifierPotion) modifier.modifier).potion.id);
				}
			}
			if (iSet == null) return ImmutableList.of();
			return iSet;
		}
	};
}
