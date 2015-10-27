package com.rwtema.tinkertailor.modifiers.itemmodifier;

import com.rwtema.tinkertailor.caches.Caches;
import com.rwtema.tinkertailor.items.ArmorCore;
import com.rwtema.tinkertailor.modifiers.Modifier;
import com.rwtema.tinkertailor.nbt.TinkersTailorConstants;
import com.rwtema.tinkertailor.utils.oremapping.ItemValueMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.modifier.IModifyable;
import tconstruct.library.modifier.ItemModifier;

public class ModArmorModifier extends ItemModifier {

	public final ItemValueMap[] recipe;
	private final Modifier modifier;
	public int modifierStep = 1;
	public int maxLevel = -1;
	public int armorAllowMask = 0;
	public boolean useModifiers = true;

	public ModArmorModifier(Modifier modifier, ItemValueMap... recipe) {
		super(convertRecipe(recipe), modifier.effect, modifier.name);
		this.modifier = modifier;
		this.recipe = recipe;
		modifierStep = modifier.getModifierStep();
		maxLevel = modifier.getMaxLevel();
		armorAllowMask = modifier.getAllowedArmorTypes();
	}

	public static ItemStack[] convertRecipe(ItemValueMap[] recipe) {
		List<ItemStack> list = new ArrayList<ItemStack>();
		for (ItemValueMap ItemValueMap : recipe) {
			ItemStack stack = ItemValueMap.makeItemStack();
			if (stack != null)
				list.add(stack);
		}
		return list.toArray(new ItemStack[list.size()]);
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
	}

	protected int totalValue(ItemStack[] recipe) {
		if (this.recipe.length == 0) {
			return 0;
		}
		if (this.recipe.length == 1) {
			ItemValueMap ItemValueMap = this.recipe[0];
			int val = 0;
			for (ItemStack itemStack : recipe) {
				if (itemStack != null)
					val += ItemValueMap.get(itemStack);
			}
			return val;
		}

		return 1;
	}

	public int getValue(ItemStack itemStack) {
		if (itemStack == null) return 0;
		for (ItemValueMap ItemValueMap : recipe) {
			int i = ItemValueMap.get(itemStack);
			if (i > 0)
				return i;
		}
		return 0;
	}

	@Override
	public boolean matches(ItemStack[] recipe, ItemStack input) {
		if (!canModify(input, recipe))
			return false;

		if (this.recipe.length == 0)
			return false;

		if (this.recipe.length == 1) {
			boolean canCraft = false;
			ItemValueMap map = this.recipe[0];
			for (ItemStack craftingStack : recipe) {
				if (craftingStack != null) {
					canCraft = true;

					if (getValue(craftingStack) <= 0)
						return false;
				}
			}

			return canCraft;
		}

		ArrayList<ItemValueMap> list = new ArrayList<ItemValueMap>();
		Collections.addAll(list, this.recipe);

		loop:
		for (ItemStack craftingStack : recipe) {
			if (craftingStack != null) {
				for (ItemValueMap ItemValueMap : list) {
					if (ItemValueMap.get(craftingStack) > 0) {
						list.remove(ItemValueMap);
						continue loop;
					}
				}

				return false;
			}
		}

		return list.isEmpty();
	}
}
