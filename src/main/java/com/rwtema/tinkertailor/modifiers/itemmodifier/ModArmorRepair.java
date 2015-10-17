package com.rwtema.tinkertailor.modifiers.itemmodifier;

import com.rwtema.tinkertailor.caches.Caches;
import com.rwtema.tinkertailor.nbt.TinkersTailorConstants;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.crafting.PatternBuilder;
import tconstruct.library.modifier.IModifyable;
import tconstruct.library.modifier.ItemModifier;

public class ModArmorRepair extends ItemModifier {
	public ModArmorRepair() {
		super(new ItemStack[0], 0, "");
	}

	@Override
	public boolean matches(ItemStack[] input, ItemStack tool) {
		return canModify(tool, input);
	}

	@Override
	protected boolean canModify(ItemStack tool, ItemStack[] input) {

		if (tool.getItemDamage() <= 0)
			return false;

		int material = Caches.material.get(tool);

		for (ItemStack curInput : input) {
			if (curInput != null && material != PatternBuilder.instance.getPartID(curInput)) {
				return false;
			}
		}
		return calculateIfNecessary(tool, input);
	}

	private boolean calculateIfNecessary(ItemStack tool, ItemStack[] input) {

		int numInputs = 0;
		int materialValue = 0;
		for (ItemStack curInput : input) {
			if (curInput != null) {
				materialValue += PatternBuilder.instance.getPartValue(curInput);
				numInputs++;
			}
		}
		if (numInputs == 0)
			return false;

		if (numInputs == 1) return true;

		int totalRepairValue = calculateIncrease(tool, materialValue, numInputs);
		float averageRepairValue = totalRepairValue / numInputs;

		int damage = tool.getItemDamage();
		return damage >= totalRepairValue - averageRepairValue;
	}

	private int calculateIncrease(ItemStack tool, int materialValue, int itemsUsed) {
		NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");

		int dur = tool.getMaxDamage();

		int increase = (int) (50 * itemsUsed + (dur * 0.4f * materialValue)) / 3;

		int modifiers = tags.getInteger(TinkersTailorConstants.NBT_MAINTAG_MODIFIERS);
		float mods = 1.0f;
		if (modifiers >= 2)
			mods = 0.9f;
		else if (modifiers == 1)
			mods = 0.8f;
		else if (modifiers == 0)
			mods = 0.7f;

		increase *= mods;

		int repair = tags.getInteger(TinkersTailorConstants.NBT_MAINTAG_REPAIRCOUNT);
		float repairCount = (100 - repair) / 100f;
		if (repairCount < 0.5f)
			repairCount = 0.5f;
		increase *= repairCount;
		return increase;
	}

	@Override
	public void modify(ItemStack[] input, ItemStack tool) {
		NBTTagCompound tags = tool.getTagCompound().getCompoundTag(TinkersTailorConstants.NBT_MAINTAG);
		tags.setBoolean(TinkersTailorConstants.NBT_MAINTAG_BROKEN, false);
		int damage = tool.getItemDamage();

		int itemsUsed = 0;

		int materialValue = 0;
		for (ItemStack curInput : input) {
			if (curInput != null) {
				materialValue += PatternBuilder.instance.getPartValue(curInput);
				itemsUsed++;
			}
		}

		int increase = calculateIncrease(tool, materialValue, itemsUsed);

		int repair = tags.getInteger(TinkersTailorConstants.NBT_MAINTAG_REPAIRCOUNT);
		repair += itemsUsed;
		tags.setInteger(TinkersTailorConstants.NBT_MAINTAG_REPAIRCOUNT, repair);

		damage -= increase;
		if (damage < 0)
			damage = 0;
		tool.setItemDamage(damage);
	}

	@Override
	public void addMatchingEffect(ItemStack tool) {
	}

	@Override
	public boolean validType(IModifyable input) {
		return input.getModifyType().equals(TinkersTailorConstants.MODIFY_TYPE);
	}
}
