package com.rwtema.tinkertailor.gui;

import com.rwtema.tinkertailor.TinkersTailor;
import com.rwtema.tinkertailor.blocks.TileEntityToolModifyStation;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import tconstruct.library.event.ToolCraftedEvent;
import tconstruct.library.modifier.IModifyable;
import tconstruct.tools.inventory.SlotTool;
import tconstruct.tools.inventory.ToolStationContainer;
import tconstruct.tools.logic.ToolStationLogic;

public class ContainerToolModifyStation extends ToolStationContainer {
	private final TileEntityToolModifyStation builderlogic;

	public ContainerToolModifyStation(InventoryPlayer inventoryplayer, TileEntityToolModifyStation builderlogic) {
		super(inventoryplayer, builderlogic);
		this.builderlogic = builderlogic;
	}

	public void initializeContainer(InventoryPlayer inventoryplayer, final ToolStationLogic builderlogic) {
		invPlayer = inventoryplayer;
		logic = builderlogic;

		toolSlot = new SlotTool(inventoryplayer.player, builderlogic, 0, 225, 38) {
			protected void onCrafting(ItemStack stack) {
				if (stack.getItem() instanceof IModifyable) {
					ContainerToolModifyStation.this.craftToolMulti(stack, inventory, player);
				} else
					super.onCrafting(stack);
			}
		};

		this.addSlotToContainer(toolSlot);
		slots = new Slot[]{new Slot(builderlogic, 1, 167, 29), new Slot(builderlogic, 2, 149, 38), new Slot(builderlogic, 3, 167, 47)};

		for (int iter = 0; iter < 3; iter++)
			this.addSlotToContainer(slots[iter]);

        /* Player inventory */
		for (int column = 0; column < 3; column++) {
			for (int row = 0; row < 9; row++) {
				this.addSlotToContainer(new Slot(inventoryplayer, row + column * 9 + 9, 118 + row * 18, 84 + column * 18));
			}
		}

		for (int column = 0; column < 9; column++) {
			this.addSlotToContainer(new Slot(inventoryplayer, column, 118 + column * 18, 142));
		}
	}

	protected void craftTool(ItemStack stack) {
		if (stack.getItem() instanceof IModifyable) {
			craftToolMulti(stack, logic, invPlayer.player);
		} else
			super.craftTool(stack);
	}

	private void craftToolMulti(ItemStack stack, IInventory logic, EntityPlayer player) {
		NBTTagCompound tags = stack.getTagCompound().getCompoundTag(((IModifyable) stack.getItem()).getBaseTagName());
		Boolean full = (logic.getStackInSlot(2) != null || logic.getStackInSlot(3) != null);
		for (int i = 2; i <= 3; i++)
			logic.decrStackSize(i, builderlogic.recipeAmount);
		ItemStack compare = logic.getStackInSlot(1);
		int amount = compare.getItem() instanceof IModifyable ? compare.stackSize : 1;
		logic.decrStackSize(1, amount);
		if (!player.worldObj.isRemote && full)
			player.worldObj.playSoundEffect(player.posX, player.posY, player.posZ, "tinker:little_saw", 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
		MinecraftForge.EVENT_BUS.post(new ToolCraftedEvent(logic, player, stack));
	}

	@Override
	public boolean canInteractWith (EntityPlayer entityplayer)
	{
		Block block = logic.getWorldObj().getBlock(logic.xCoord, logic.yCoord, logic.zCoord);
		return block == TinkersTailor.toolModifyStation && logic.isUseableByPlayer(entityplayer);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotID) {
		ItemStack stack = null;
		Slot slot = (Slot) this.inventorySlots.get(slotID);

		if (slot != null && slot.getHasStack())
		{
			ItemStack slotStack = slot.getStack();
			stack = slotStack.copy();
			if (slotID < logic.getSizeInventory())
			{
				if (slotID == 0)
				{
					if (!this.mergeCraftedStack(slotStack, logic.getSizeInventory(), this.inventorySlots.size(), true, player))
					{
						return null;
					}
				}
				else if (!this.mergeItemStack(slotStack, logic.getSizeInventory(), this.inventorySlots.size(), true))
				{
					return null;
				}
			}
			else if (!this.mergeItemStack(slotStack, 1, logic.getSizeInventory(), false))
			{
				return null;
			}

			if (slotStack.stackSize == 0)
			{
				slot.putStack(null);
			}
			else
			{
				slot.onSlotChanged();
			}
		}

		return stack;
	}
}
