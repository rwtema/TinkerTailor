package com.rwtema.tinkertailor.modifiers;

import com.google.common.collect.Multimap;
import com.rwtema.tinkertailor.caches.WeakCache;
import com.rwtema.tinkertailor.items.ArmorCore;
import com.rwtema.tinkertailor.modifiers.itemmodifier.ModArmorModifier;
import com.rwtema.tinkertailor.nbt.StringHelper;
import com.rwtema.tinkertailor.nbt.TinkersTailorConstants;
import com.rwtema.tinkertailor.render.font.RenderCustomColor;
import com.rwtema.tinkertailor.utils.Lang;
import com.rwtema.tinkertailor.utils.oremapping.ItemValueMap;
import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;

public abstract class Modifier implements Comparable<Modifier> {
	public static final int ARMORTYPE_HAT_ONLY = 14;
	public static final int ARMORTYPE_SHIRT_ONLY = 13;
	public static final int ARMORTYPE_TROUSERS_ONLY = 11;
	public static final int ARMORTYPE_SHOES_ONLY = 7;

	public int effect = 0;
	public ItemValueMap[] recipe;
	public int allowedArmorTypes;
	public int color = 0;
	public ModArmorModifier itemModifier;
	public String name;
	public final WeakCache<ItemStack, Integer> level = new WeakCache<ItemStack, Integer>() {
		@Nonnull
		@Override
		protected Integer calc(@Nonnull ItemStack stack) {
			if (!(stack.getItem() instanceof ArmorCore))
				return 0;

			NBTTagCompound tag = stack.getTagCompound();
			if (tag == null)
				return 0;

			return tag.getCompoundTag(TinkersTailorConstants.NBT_MAINTAG).getInteger(Modifier.this.name);
		}
	};
	public String[] requiredMods = null;
	protected int maxLevel;
	protected int modifierStep = 1;
	String colorString = null;

	protected Modifier(String name, int maxLevel, ItemValueMap... recipe) {
		this.name = name;
		this.maxLevel = maxLevel;
		this.recipe = recipe;
	}

	public float getBonusResistance(EntityLivingBase entity, DamageSource source, float amount, ItemStack item, int slot, int level) {
		return 0;
	}

	public void addAttributes(ItemStack item, int slot, int level, Multimap<String, AttributeModifier> map) {

	}

	public void addAttributeModifier(Multimap<String, AttributeModifier> map, IAttribute attribute, double amount, int priority) {
		map.put(attribute.getAttributeUnlocalizedName(), new AttributeModifier(TinkersTailorConstants.UUID_ITEMS, "Armor modifier", amount, priority));
	}

	public int reduceArmorDamage(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int originalDamage, int slot, int level) {
		return damage;
	}

	public void addInfo(List<String> list, EntityPlayer player, ItemStack item, int slot, int level) {

		if (modifierStep > 1) {
			int m = (int) (Math.ceil((double) level / modifierStep)) * modifierStep;
			list.add(getColorString() + getLocalizedName() + getLevelTooltip(level - 1) + String.format(" (%s/%s)", level, m) + resetColorString());
		} else
			list.add(getColorString() + getLocalizedName() + getLevelTooltip(level - 1) + resetColorString());
	}

	public String resetColorString() {
		return EnumChatFormatting.RESET.toString();
	}

	public String getLocalizedName() {
		return Lang.findTranslateKey(StringHelper.capFirst(name), "modifier");
	}

	public void addArmorSetInfo(List<String> list, EntityPlayer player) {

	}

	public ModArmorModifier createItemModifier() {
		return new ModArmorModifier(this, recipe);
	}

	public boolean doesTick(ItemStack item, int level) {
		return false;
	}

	public void onArmorTick(EntityLivingBase entity, ItemStack item, int slot, int level) {

	}

	public int getMaxLevel() {
		return maxLevel;
	}

	public Modifier setModifierStep(int modifierStep) {
		this.modifierStep = modifierStep;
		return this;
	}

	public int getModifierStep() {
		return modifierStep;
	}

	public String getLevelTooltip(int level) {
		if (getMaxLevel() == 1) return "";
		int l = 1 + level / modifierStep;
		return " " + StringHelper.toRomanNumeral(l);
	}

	public int getAllowedArmorTypes() {
		return allowedArmorTypes;
	}

	public Modifier setAllowedArmorTypes(int allowedArmorTypes) {
		this.allowedArmorTypes = allowedArmorTypes;
		return this;
	}

	public String getColorString() {
		if (colorString == null) {
			if (color == 0) {
				Random rand = TinkersTailorConstants.RANDOM;
				int r, g, b;
				do {
					r = rand.nextInt(256);
					g = rand.nextInt(256);
					b = rand.nextInt(256);
				} while (r + g + b < 256);

				color = 0xff000000 | (r << 16) | (g << 8) | b;
			}

			colorString = RenderCustomColor.getCol(color);

		}
		return colorString;
	}

	public int compareTo(Modifier o) {
		return getLocalizedName().compareTo(o.getLocalizedName());
	}

	public String[] getDocLines() {
		return TinkersTailorConstants.EMPTY_STRING_ARRAY;
	}

	public boolean shouldHaveDocEntry() {
		return true;
	}


	public int durabilityBoost(ItemStack stack, int level) {
		return 0;
	}

	public Modifier setRequiredMods(String... requiredMods) {
		this.requiredMods = requiredMods;
		return this;
	}
}
