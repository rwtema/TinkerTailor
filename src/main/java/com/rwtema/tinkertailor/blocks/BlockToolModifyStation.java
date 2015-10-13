package com.rwtema.tinkertailor.blocks;

import com.rwtema.tinkertailor.TinkersTailor;
import com.rwtema.tinkertailor.nbt.TinkersTailorConstants;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mantle.blocks.abstracts.InventoryBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import tconstruct.tools.model.TableRender;

public class BlockToolModifyStation extends InventoryBlock {
	public BlockToolModifyStation(Material material) {
		super(material);
		this.setCreativeTab(TinkersTailor.creativeTabItems);
		this.setHardness(2f);
		this.setStepSound(Block.soundTypeWood);
	}

	//Block.hasComparatorInputOverride and Block.getComparatorInputOverride

	/* Rendering */
	@Override
	public String[] getTextureNames() {
		return new String[]{"toolmodify_top", "toolmodify_side", "toolmodify_bottom"};
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return icons[(side == 0 ? 2 : side == 1 ? 0 : 1)];
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
		return side == ForgeDirection.UP;
	}

	@Override
	public int getRenderType() {
		return TableRender.model;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
		return true;
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		return AxisAlignedBB.getBoundingBox((double) x + this.minX, (double) y + this.minY, (double) z + this.minZ, (double) x + this.maxX, (double) y + this.maxY, (double) z + this.maxZ);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		switch (metadata) {
			case 0:
				return new TileEntityToolModifyStation();

			default:
				return null;
		}
	}

	@Override
	public Integer getGui(World world, int x, int y, int z, EntityPlayer entityplayer) {
		int md = world.getBlockMetadata(x, y, z);
		if (md == 0)
			return 0;

		return null;

	}

	@Override
	public Object getModInstance() {
		return TinkersTailor.instance;
	}

	@Override
	public String getTextureDomain(int textureNameIndex) {
		return TinkersTailorConstants.RESOURCE_FOLDER;
	}

}
