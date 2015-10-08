package com.rwtema.tinkertailor.items;

import com.rwtema.tinkertailor.TinkerTailor;
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
		setCreativeTab(TinkerTailor.creativeTabItems);
	}

	@Override
	public int getPatternCost(ItemStack pattern) {
		return ItemArmorPattern.slotCost[pattern.getItemDamage()];
	}

	@Override
	public ItemStack getPatternOutput(ItemStack pattern, ItemStack input, PatternBuilder.MaterialSet set) {
		return TConstructRegistry.getPartMapping(this, pattern.getItemDamage(), set.materialID);
	}
}
