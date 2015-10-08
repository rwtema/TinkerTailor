package com.rwtema.tinkertailor.modifiers.itemmodifier;

import com.rwtema.tinkertailor.caches.Caches;
import com.rwtema.tinkertailor.items.ArmorCore;
import com.rwtema.tinkertailor.modifiers.Modifier;
import com.rwtema.tinkertailor.nbt.TinkerTailorConstants;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.modifier.IModifyable;
import tconstruct.library.modifier.ItemModifier;

public class ModArmorModifier extends ItemModifier {

	int modifierStep = 1;
	int maxLevel = -1;
	int armorAllowMask = 0;

	public ModArmorModifier(Modifier modifier) {
		super(modifier.recipe(), modifier.effect, modifier.name);
		modifierStep = modifier.getModifierStep();
		maxLevel = modifier.getMaxLevel();
		armorAllowMask = modifier.getAllowedArmorTypes();
	}

	@Override
	protected boolean canModify(ItemStack input, ItemStack[] recipe) {
		if (input == null || !(input.getItem() instanceof ArmorCore)) return false;

		if (armorAllowMask != 0) {
			if (((1 << Caches.slot.get(input)) & armorAllowMask) != 0) {
				return false;
			}
		}

		NBTTagCompound tags = input.getTagCompound().getCompoundTag(getTagName(input));
		int curValue = tags.getInteger(key);

		if (maxLevel > 0 && curValue >= maxLevel)
			return false;

		int val = totalValue(recipe);
		if (val > modifierStep) return false;

		int modifiers = tags.getInteger("Modifiers");
		if (modifiers > 0) return true;
		if (modifierStep == 1 || curValue % modifierStep == 0) return false;
		return (curValue / modifierStep) == ((curValue + val) / modifierStep);

	}


	@Override
	public boolean validType(IModifyable input) {
		return input.getModifyType().equals(TinkerTailorConstants.MODIFY_TYPE);
	}

	@Override
	public void modify(ItemStack[] recipe, ItemStack tool) {
		NBTTagCompound tags = tool.getTagCompound().getCompoundTag(TinkerTailorConstants.NBT_MAINTAG);

		int curValue;
		int val = totalValue(recipe);
		if (tags.hasKey(key)) {
			int increase = tags.getInteger(key);
			curValue = increase;
			increase += val;
			tags.setInteger(key, increase);
		} else {
			curValue = 0;
			tags.setInteger(key, val);
		}

		if ((curValue / modifierStep) != ((curValue + val) / modifierStep)) {
			int modifiers = tags.getInteger("Modifiers");
			modifiers -= 1;
			tags.setInteger("Modifiers", modifiers);
		}

//		addToolTip(tool, color + tooltipName, color + key);
	}

	protected int totalValue(ItemStack[] recipe) {
		int v = 0;
		for (ItemStack itemStack : recipe) {
			if (itemStack != null)
				v++;
		}
		return v;
	}
}
