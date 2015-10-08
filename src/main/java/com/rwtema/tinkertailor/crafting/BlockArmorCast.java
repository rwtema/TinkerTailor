package com.rwtema.tinkertailor.crafting;

import com.rwtema.tinkertailor.TinkerTailor;
import com.rwtema.tinkertailor.items.ItemArmorPattern;
import com.rwtema.tinkertailor.nbt.TinkerTailorConstants;
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
	public BlockArmorCast() {
		super(Material.iron);
		setHardness(1);
		this.setCreativeTab(TinkerTailor.creativeTabItems);
		this.setBlockName("TinkerTailor.ArmorCast");
	}

	IIcon[] topIcons = new IIcon[4];

	IIcon sideIcon;
	IIcon edgeIcon;

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		String[] tex = ItemArmorPattern.getTex("cast_");
		for (int i = 0; i < 4; ++i) {
			topIcons[i] = iconRegister.registerIcon(TinkerTailorConstants.RESOURCE_FOLDER + ":" + tex[i]);
		}

		sideIcon = iconRegister.registerIcon(TinkerTailorConstants.RESOURCE_FOLDER + ":" + "cast_base");
		edgeIcon = iconRegister.registerIcon(TinkerTailorConstants.RESOURCE_FOLDER + ":" + "cast_side");
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
