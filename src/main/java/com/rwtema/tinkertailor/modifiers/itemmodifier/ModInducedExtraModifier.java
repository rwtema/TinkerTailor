package com.rwtema.tinkertailor.modifiers.itemmodifier;


import com.rwtema.tinkertailor.nbt.TinkersTailorConstants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.crafting.ModifyBuilder;
import tconstruct.library.modifier.IModifyable;
import tconstruct.library.modifier.ItemModifier;
import tconstruct.modifiers.tools.ModExtraModifier;

public class ModInducedExtraModifier extends ItemModifier {
	public ModInducedExtraModifier() {
		super(new ItemStack[]{}, 0, "Modification");
	}

	@Override
	protected boolean canModify(ItemStack tool, ItemStack[] recipe) {
		if (tool != null && tool.getItem() instanceof IModifyable) {
			NBTTagCompound tags = this.getModifierTag(tool);
			return !tags.getBoolean(key);
		}
		return false;
	}

	@Override
	public void modify(ItemStack[] recipe, ItemStack input) {
		NBTTagCompound tags = this.getModifierTag(input);
		String key = getKey(recipe);
		if (key == null) return;
		tags.setBoolean(key, true);
		int modifiers = tags.getInteger("Modifiers");
		modifiers += 1;
		tags.setInteger("Modifiers", modifiers);
	}

	public void addMatchingEffect(ItemStack tool) {
	}

	@Override
	public boolean matches(ItemStack[] recipe, ItemStack tool) {
		String key = getKey(recipe);
		if (key == null) return false;

		if (tool != null && tool.getItem() instanceof IModifyable) {
			NBTTagCompound tags = this.getModifierTag(tool);
			return !tags.getBoolean(key);
		}
		return false;

	}

	private HashMap<String, List<ItemStack>> items = null;
	private int k = -1;

	public HashMap<String, List<ItemStack>> getItemKeyMap() {
		List<ItemModifier> modifiers = ModifyBuilder.instance.itemModifiers;
		if (items != null && k == modifiers.size()) return items;

		items = new HashMap<String, List<ItemStack>>();

		for (ItemModifier itemModifier : modifiers) {
			if (itemModifier.getClass() == ModExtraModifier.class) {
				items.put(itemModifier.key, itemModifier.stacks);
			}
		}

		k = modifiers.size();
		return items;
	}

	public String getKey(ItemStack[] input) {
		HashMap<String, List<ItemStack>> keyMap = getItemKeyMap();
		for (Map.Entry<String, List<ItemStack>> entry : keyMap.entrySet()) {
			if (matchItems(input, entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}

	public boolean matchItems(ItemStack[] recipe, List items) {
		ArrayList tempList = new ArrayList(items);

		mainLoop:
		for (ItemStack craftingStack : recipe) {
			if (craftingStack != null) {
				for (Object obj : tempList) {
					ItemStack removeStack = (ItemStack) obj;

					if (craftingStack.getItem() == removeStack.getItem() && (removeStack.getItemDamage() == Short.MAX_VALUE || craftingStack.getItemDamage() == removeStack.getItemDamage())) {
						tempList.remove(removeStack);
						continue mainLoop;
					}
				}

				return false;
			}
		}

		return tempList.isEmpty();
	}

	@Override
	public boolean validType(IModifyable input) {
		return input.getModifyType().equals(TinkersTailorConstants.MODIFY_TYPE);
	}

}
