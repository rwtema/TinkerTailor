package com.rwtema.tinkertailor.modifiers;

import com.rwtema.tinkertailor.nbt.TinkersTailorConstants;
import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public final class ModifierInstance implements Comparable<ModifierInstance> {
	public final Modifier modifier;
	public final int level;

	public ModifierInstance(Modifier modifier, int level) {
		this.modifier = modifier;
		this.level = level;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ModifierInstance that = (ModifierInstance) o;

		return modifier.equals(that.modifier);
	}

	@Override
	public int hashCode() {
		return modifier.hashCode();
	}

	@Override
	public int compareTo(@Nonnull ModifierInstance other) {
		int i = this.modifier.compareTo(other.modifier);
		if (i != 0) return i;
		return Double.compare(this.level, other.level);
	}

	public void applyToStack(ItemStack stack) {
		applyToStack(stack.getTagCompound().getCompoundTag(TinkersTailorConstants.NBT_MAINTAG));
	}

	public void applyToStack(NBTTagCompound tag) {
		addModLevel(tag, modifier, level);
	}

	public static void addModLevel(ItemStack stack, Modifier modifier, int level) {
		addModLevel(stack.getTagCompound().getCompoundTag(TinkersTailorConstants.NBT_MAINTAG), modifier, level);
	}

	public static void addModLevel(NBTTagCompound tag, Modifier modifier, int level) {
		int i = Math.min(tag.getInteger(modifier.name) + level, modifier.maxLevel);
		tag.setInteger(modifier.name, i);
	}
}
