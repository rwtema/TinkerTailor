package com.rwtema.tinkertailor.modifiers.itemmodifier;

import com.rwtema.tinkertailor.items.ArmorCore;
import com.rwtema.tinkertailor.nbt.TinkersTailorConstants;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.modifier.IModifyable;
import tconstruct.library.modifier.ItemModifier;

public class ModCreativeArmorModifier extends ItemModifier {
	public ModCreativeArmorModifier(ItemStack[] items) {
		super(items, 0, "");
	}

	@Override
	protected boolean canModify(ItemStack tool, ItemStack[] input) {
		return tool.getItem() instanceof ArmorCore;
	}

	@Override
	public void modify(ItemStack[] input, ItemStack tool) {
		NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
		int modifiers = tags.getInteger(TinkersTailorConstants.NBT_MAINTAG_MODIFIERS);
		modifiers += 1;
		tags.setInteger(TinkersTailorConstants.NBT_MAINTAG_MODIFIERS, modifiers);
	}

	public void addMatchingEffect(ItemStack tool) {
	}

	@Override
	public boolean validType(IModifyable input) {
		return input.getModifyType().equals(TinkersTailorConstants.MODIFY_TYPE);
	}

}
