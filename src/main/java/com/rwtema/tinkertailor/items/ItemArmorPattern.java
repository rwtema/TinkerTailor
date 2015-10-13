package com.rwtema.tinkertailor.items;

import com.rwtema.tinkertailor.TinkersTailor;
import com.rwtema.tinkertailor.nbt.TinkersTailorConstants;
import mantle.items.abstracts.CraftingItem;
import net.minecraft.item.ItemStack;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.PatternBuilder;
import tconstruct.library.util.IPattern;

public class ItemArmorPattern extends CraftingItem implements IPattern {
	static String[] names = new String[]{"helmet", "chestplate", "leggings", "boots"};

	public ItemArmorPattern() {
		super(names, getTex("pattern_"), "", TinkersTailorConstants.RESOURCE_FOLDER, TinkersTailor.creativeTabItems);
		setCreativeTab(TinkersTailor.creativeTabItems);
		setUnlocalizedName("TinkerTailor.ArmorPattern");
	}

	public static String[] getTex(String prefix) {
		String[] tex = new String[4];
		for (int i = 0; i < names.length; i++) {
			tex[i] = prefix + names[i];
		}
		return tex;
	}

	public static final int[] slotCost = new int[]{10, 16, 14, 8};

	@Override
	public int getPatternCost(ItemStack pattern) {
		return slotCost[pattern.getItemDamage()];
	}

	@Override
	public ItemStack getPatternOutput(ItemStack pattern, ItemStack input, PatternBuilder.MaterialSet set) {
//		int meta = input.getItemDamage();
//		if(meta < 0 || meta >= 4) return null;
//
//		if(Arrays.binarySearch(Constants.NON_METALS, set.materialID)<0)
//			return null;
//
//		return ItemArmorCore.armors[meta].makeDefaultStack(set.materialID);
		return TConstructRegistry.getPartMapping(this, pattern.getItemDamage(), set.materialID);
	}
}
