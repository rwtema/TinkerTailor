package com.rwtema.tinkertailor.blocks;

import com.rwtema.tinkertailor.gui.ContainerToolModifyStation;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import tconstruct.library.crafting.ModifyBuilder;
import tconstruct.library.modifier.IModifyable;
import tconstruct.tools.logic.ToolStationLogic;

public class TileEntityToolModifyStation extends ToolStationLogic {

	public int recipeAmount = 1;

	public TileEntityToolModifyStation() {
		super(4);
	}

	@Override
	public Container getGuiContainer(InventoryPlayer inventoryplayer, World world, int x, int y, int z) {
		return new ContainerToolModifyStation(inventoryplayer, this);
	}

	@Override
	public void buildTool(String name) {
		recipeAmount = 1;
		ItemStack output = null;
		ItemStack input = inventory[1];
		if (input != null) {
			if (input.getItem() instanceof IModifyable) //Modify item
			{
				ItemStack a = sanityCheckCopy(inventory[2]);
				ItemStack b = sanityCheckCopy(inventory[3]);
				if (a == null && b == null)
					output = null;
				else {

					ItemStack posOut = input.copy();
					recipeAmount = 0;
					while (a != null || b != null) {

						output = ModifyBuilder.instance.modifyItem(posOut, new ItemStack[]{a, b});
						if (output == null || ItemStack.areItemStacksEqual(posOut, output)) {
							if (recipeAmount > 0) {
								output = posOut;
							} else {
								output = null;
								recipeAmount = 1;
							}
							break;
						} else {
							if (a != null) {
								a.stackSize--;
								if (a.stackSize == 0)
									a = null;
							}
							if (b != null) {
								b.stackSize--;
								if (b.stackSize == 0)
									b = null;
							}
							posOut = output;
						}

						recipeAmount++;
					}
				}
			}

		}


		inventory[0] = output;
	}

	private ItemStack sanityCheckCopy(ItemStack itemStack) {
		if (itemStack == null || itemStack.stackSize <= 0 || itemStack.stackSize > 64)
			return null;

		return ItemStack.copyItemStack(itemStack);
	}

	@Override
	public String getDefaultName() {
		return "tile.TinkerTailor.ToolModifyStation.name";
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
		if (slot == 0) {
			return itemstack != null && itemstack.getItem() instanceof IModifyable && itemstack.getMaxStackSize() == 1;
		}
		return super.isItemValidForSlot(slot, itemstack);
	}
}
