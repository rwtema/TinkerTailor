package com.rwtema.tinkertailor.items;

import com.rwtema.tinkertailor.TinkersTailor;
import com.rwtema.tinkertailor.nbt.TinkersTailorConstants;
import mantle.blocks.abstracts.MultiItemBlock;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.PatternBuilder;
import tconstruct.library.util.IPattern;

public class ItemArmorCast extends MultiItemBlock implements IPattern {
	public ItemArmorCast(Block b) {
		super(b, "", ItemArmorPattern.names);
		setMaxDamage(0);
		setHasSubtypes(true);
		setCreativeTab(TinkersTailor.creativeTabItems);
	}

	@Override
	public int getPatternCost(ItemStack pattern) {
		return TinkersTailorConstants.slotCost[pattern.getItemDamage()];
	}

	@Override
	public ItemStack getPatternOutput(ItemStack pattern, ItemStack input, PatternBuilder.MaterialSet set) {
		return TConstructRegistry.getPartMapping(this, pattern.getItemDamage(), set.materialID);
	}
}
