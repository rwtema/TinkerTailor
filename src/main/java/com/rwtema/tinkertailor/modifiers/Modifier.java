package com.rwtema.tinkertailor.modifiers;

import com.google.common.collect.Multimap;
import com.rwtema.tinkertailor.nbt.StringHelper;
import com.rwtema.tinkertailor.nbt.TinkerTailorConstants;
import com.rwtema.tinkertailor.caches.WeakCache;
import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;

public abstract class Modifier {
public static final int ARMORTYPE_HAT_ONLY = 14;
	public static final int ARMORTYPE_SHIRT_ONLY = 13;
	public static final int ARMORTYPE_TROUSERS_ONLY = 11;
	public static final int ARMORTYPE_SHOES_ONLY = 7;

	public int effect = 0;
	protected int maxLevel;
	public ItemStack[] itemStacks;
	protected int modifierStep = 1;
	public int allowedArmorTypes;

	protected Modifier(String name, int maxLevel, ItemStack... itemStacks) {
		this.name = name;
		this.maxLevel = maxLevel;
		this.itemStacks = itemStacks;
		register();
	}

	public String name;

	public WeakCache<ItemStack, Integer> level = new WeakCache<ItemStack, Integer>() {
		@Override
		protected Integer calc(ItemStack stack) {
			NBTTagCompound tag = stack.getTagCompound();
			if (tag == null)
				return 0;

			return tag.getCompoundTag(TinkerTailorConstants.NBT_MAINTAG).getInteger(Modifier.this.name);
		}
	};

	public float getBonusResistance(EntityLivingBase entity, DamageSource source, float amount, ItemStack item, int slot, int level) {
		return 0;
	}

	public void addAttributes(ItemStack item, int slot, int level, Multimap<String, AttributeModifier> map) {

	}

	public void addAttributeModifier(Multimap<String, AttributeModifier> map, IAttribute attribute, double amount, int priority) {
		map.put(attribute.getAttributeUnlocalizedName(), new AttributeModifier(TinkerTailorConstants.UUID_ITEMS, "Armor modifier", amount, priority));
	}

	public int reduceArmorDamage(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int originalDamage, int slot, int level) {
		return damage;
	}

	public void addInfo(List<String> list, EntityPlayer player, ItemStack item, int slot, int level) {
		list.add(getLocalizedName() + getLevelTooltip(level));
	}

	public String getLocalizedName() {
		String transKey = "modifier." + name;
		if (!StatCollector.canTranslate(transKey)) return StringHelper.capFirst(name);
		return StatCollector.translateToLocal(transKey);
	}

	public void addArmorSetInfo(List<String> list, EntityPlayer player) {

	}

	public void register() {
		ModifierRegistry.modifiers.put(name, this);
	}

	public abstract ItemStack[] recipe();

	public boolean doesTick(ItemStack item, int level) {
		return false;
	}

	public void onArmorTick(EntityLivingBase entity, ItemStack item, int slot, int level) {

	}

	public int getMaxLevel() {
		return maxLevel;
	}

	public int getModifierStep() {
		return modifierStep;
	}

	public String getLevelTooltip(int level) {
		if(getMaxLevel() == 1) return "";
		int l = 1 + level / modifierStep;
		return " " +  StringHelper.toRomanNumeral(l);
	}

	public int getAllowedArmorTypes() {
		return allowedArmorTypes;
	}

	public Modifier setAllowedArmorTypes(int allowedArmorTypes) {
		this.allowedArmorTypes = allowedArmorTypes;
		return this;
	}
}
