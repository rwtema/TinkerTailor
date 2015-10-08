package com.rwtema.tinkertailor.caches;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.rwtema.tinkertailor.DamageEventHandler;
import com.rwtema.tinkertailor.items.ArmorCore;
import com.rwtema.tinkertailor.modifiers.ModifierRegistry;
import static com.rwtema.tinkertailor.modifiers.ModifierRegistry.ArmorModifierInstance;
import com.rwtema.tinkertailor.modifiers.Modifier;
import com.rwtema.tinkertailor.modifiers.ModifierPotion;
import com.rwtema.tinkertailor.nbt.TinkerTailorConstants;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.tools.ToolMaterial;

public class Caches {

	public static WeakCache<ItemStack, Integer> color = new WeakCache<ItemStack, Integer>() {

		@Override
		protected Integer calc(ItemStack stack) {
			NBTTagCompound tags = stack.getTagCompound();

			if (tags != null) {
				tags = stack.getTagCompound().getCompoundTag(TinkerTailorConstants.NBT_MAINTAG);
				tconstruct.library.tools.ToolMaterial material = TConstructRegistry.toolMaterials.get(tags.getInteger(TinkerTailorConstants.NBT_MAINTAG_RENDERID));
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
		protected Integer calc(ItemStack stack) {
			NBTTagCompound tags = stack.getTagCompound();

			if (tags != null) {
				tags = stack.getTagCompound().getCompoundTag(TinkerTailorConstants.NBT_MAINTAG);
				return tags.getInteger(TinkerTailorConstants.NBT_MAINTAG_MATERIAL);
			}
			return 0;
		}
	};

	public static WeakCache<ItemStack, List<ArmorModifierInstance>> modifiers = new WeakCache<ItemStack, List<ArmorModifierInstance>>() {
		@SuppressWarnings("unchecked")
		@Override
		protected List<ArmorModifierInstance> calc(ItemStack stack) {
			NBTTagCompound tag = stack.getTagCompound();
			if (tag == null)
				return ImmutableList.of();

			NBTTagCompound datatags = stack.getTagCompound().getCompoundTag(TinkerTailorConstants.NBT_MAINTAG);

			Set<String> keys = (Set<String>) datatags.func_150296_c();

			List<ArmorModifierInstance> list = new ArrayList<ArmorModifierInstance>(keys.size());
			for (String key : keys) {
				Modifier modifier = ModifierRegistry.modifiers.get(key);
				if (modifier != null) {
					int level = datatags.getInteger(key);
					if (level != 0)
						list.add(new ArmorModifierInstance(modifier, level));
				}
			}
			return ImmutableList.copyOf(list);
		}

		@Override
		protected List<ArmorModifierInstance> getNullValue() {
			return ImmutableList.of();
		}
	};

	public static WeakCache<ItemStack, Integer> slot = new WeakCache<ItemStack, Integer>() {
		@Override
		protected Integer calc(ItemStack key) {
			return ((ArmorCore) key.getItem()).armorType;
		}
	};

	public static WeakCache<ItemStack, Float> damageResistance = new WeakCache<ItemStack, Float>() {
		@Override
		protected Float calc(ItemStack stack) {
			return (float) (DamageEventHandler.matDRcache.get(Caches.material.get(stack)) * ArmorCore.ratings[slot.get(stack)]);
		}
	};


	public static WeakCache<ItemStack, Float> damageThreshold = new WeakCache<ItemStack, Float>() {
		@Override
		protected Float calc(ItemStack stack) {
			ToolMaterial toolMaterial = TConstructRegistry.toolMaterials.get(Caches.material.get(stack));
			if (toolMaterial == null) return 0F;
			return (toolMaterial.attack() * ArmorCore.ratings[slot.get(stack)]);
		}
	};

	public static WeakCache<ItemStack, Multimap<String, AttributeModifier>> attributes = new WeakCache<ItemStack, Multimap<String, AttributeModifier>>() {
		@Override
		protected Multimap<String, AttributeModifier> calc(ItemStack key) {
			Multimap<String, AttributeModifier> map = HashMultimap.create();
			List<ArmorModifierInstance> armorModifierInstances = modifiers.get(key);
			for (ArmorModifierInstance modifier : armorModifierInstances) {
				modifier.modifier.addAttributes(key, slot.get(key), modifier.level, map);
			}

			return map;
		}
	};

	public static WeakCache<ItemStack, List<ArmorModifierInstance>> tickers = new WeakCache<ItemStack, List<ArmorModifierInstance>>() {
		@Override
		protected List<ArmorModifierInstance> calc(ItemStack key) {
			List<ArmorModifierInstance> armorModifierInstances = modifiers.get(key);
			if (armorModifierInstances.isEmpty()) return ImmutableList.of();

			List<ArmorModifierInstance> result = new ArrayList<ArmorModifierInstance>(armorModifierInstances.size());
			for (ArmorModifierInstance armorModifierInstance : armorModifierInstances) {
				if (armorModifierInstance.modifier.doesTick(key, armorModifierInstance.level)) {
					result.add(armorModifierInstance);
				}
			}
			if (result.isEmpty()) return ImmutableList.of();
			return result;
		}
	};

	public static WeakCache<ItemStack, Collection<Integer>> potionIds = new WeakCache<ItemStack, Collection<Integer>>() {
		@Override
		protected Collection<Integer> calc(ItemStack key) {
			if (key == null || !(key.getItem() instanceof ArmorCore))
				return ImmutableList.of();
			HashSet<Integer> iSet = null;
			List<ArmorModifierInstance> modifiers = Caches.modifiers.get(key);
			for (ArmorModifierInstance modifier : modifiers) {
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
