package com.rwtema.tinkertailor.modifiers.itemmodifier;

import com.rwtema.tinkertailor.caches.Caches;
import com.rwtema.tinkertailor.items.ArmorCore;
import com.rwtema.tinkertailor.modifiers.Modifier;
import com.rwtema.tinkertailor.nbt.TinkersTailorConstants;
import java.util.ArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.modifier.IModifyable;
import tconstruct.library.modifier.ItemModifier;

public class ModArmorModifier extends ItemModifier {

	public int modifierStep = 1;
	public int maxLevel = -1;
	public int armorAllowMask = 0;
	public boolean useModifiers = true;

	public ModArmorModifier(Modifier modifier, ItemStack[] recipe) {
		super(recipe, modifier.effect, modifier.name);
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
		if (val == 0 || val > modifierStep) return false;
		if (modifierStep != 1) {
			int nextMax = (curValue / modifierStep + 1) * modifierStep;
			if ((curValue + val) > nextMax) return false;
		}

		if (!useModifiers) return true;

		int modifiers = tags.getInteger(TinkersTailorConstants.NBT_MAINTAG_MODIFIERS);
		if (modifiers > 0) return true;

		if (modifierStep == 1 || curValue % modifierStep == 0) return false;
		return curValue == 0 || ((curValue) / modifierStep) == ((curValue + val) / modifierStep);
	}


	@Override
	public boolean validType(IModifyable input) {
		return input.getModifyType().equals(TinkersTailorConstants.MODIFY_TYPE);
	}

	@Override
	public void modify(ItemStack[] recipe, ItemStack tool) {
		NBTTagCompound tags = tool.getTagCompound().getCompoundTag(TinkersTailorConstants.NBT_MAINTAG);

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

		if (curValue % modifierStep == 0 && useModifiers) {
			int modifiers = tags.getInteger(TinkersTailorConstants.NBT_MAINTAG_MODIFIERS);
			modifiers -= 1;
			tags.setInteger(TinkersTailorConstants.NBT_MAINTAG_MODIFIERS, modifiers);
		}

//		addToolTip(tool, color + tooltipName, color + key);
	}

	protected int totalValue(ItemStack[] recipe) {
//		int v = 0;
//		for (ItemStack itemStack : recipe) {
//			if (itemStack != null)
//				v++;
//		}
		return 1;
	}

	@Override
	public boolean matches(ItemStack[] recipe, ItemStack input) {
		if (!canModify(input, recipe))
			return false;

		ArrayList<ItemStack> list = new ArrayList<ItemStack>(this.stacks);

		for (ItemStack craftingStack : recipe) {
			if (craftingStack != null) {
				boolean canCraft = false;

				for (ItemStack removeStack : list) {
					if (craftingStack.getItem() == removeStack.getItem() && (removeStack.getItemDamage() == Short.MAX_VALUE || craftingStack.getItemDamage() == removeStack.getItemDamage())) {
						canCraft = true;
						list.remove(removeStack);
						break;
					}
				}

				if (!canCraft) {
					return false;
				}
			}
		}

		return list.isEmpty();
	}
}
