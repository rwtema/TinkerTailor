package com.rwtema.tinkertailor.crafting;

import com.rwtema.tinkertailor.TinkersTailor;
import com.rwtema.tinkertailor.items.ItemArmorPattern;
import com.rwtema.tinkertailor.nbt.TinkersTailorConstants;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import mantle.blocks.MantleBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class BlockArmorCast extends MantleBlock {
	IIcon[] topIcons = new IIcon[4];
	IIcon sideIcon;
	IIcon edgeIcon;

	public BlockArmorCast() {
		super(Material.iron);
		setHardness(1);
		this.setCreativeTab(TinkersTailor.creativeTabItems);
		this.setBlockName("TinkerTailor.ArmorCast");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		String[] tex = ItemArmorPattern.getTex("cast_");
		for (int i = 0; i < 4; ++i) {
			topIcons[i] = iconRegister.registerIcon(TinkersTailorConstants.RESOURCE_FOLDER + ":" + tex[i]);
		}

		sideIcon = iconRegister.registerIcon(TinkersTailorConstants.RESOURCE_FOLDER + ":" + "cast_base");
		edgeIcon = iconRegister.registerIcon(TinkersTailorConstants.RESOURCE_FOLDER + ":" + "cast_side");
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		if (side == 1 && (meta >= 0 && meta < 4))
			return topIcons[meta];

		if ((side & 6) == 4)
			return sideIcon;
		return edgeIcon;
	}

	@Override
	public void getSubBlocks(Item block, CreativeTabs tab, List list) {
		for (int i = 0; i < 4; i++) {
			list.add(new ItemStack(block, 1, i));
		}
	}


	@Override
	public int damageDropped(int meta) {
		return meta;
	}

}
