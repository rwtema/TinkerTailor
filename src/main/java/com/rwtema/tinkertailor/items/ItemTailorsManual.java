package com.rwtema.tinkertailor.items;

import com.rwtema.tinkertailor.TinkersTailor;
import com.rwtema.tinkertailor.manual.GuiCustomManual;
import com.rwtema.tinkertailor.manual.ManualHelper;
import com.rwtema.tinkertailor.nbt.TinkersTailorConstants;
import com.rwtema.tinkertailor.utils.Lang;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import mantle.books.BookData;
import mantle.items.abstracts.CraftingItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import tconstruct.library.tools.AbilityHelper;
import tconstruct.tools.TinkerTools;
import tconstruct.util.config.PHConstruct;

public class ItemTailorsManual extends CraftingItem {

	public ItemTailorsManual() {
		super(new String[]{"Manual"}, new String[]{"TailorBook"}, "", TinkersTailorConstants.RESOURCE_FOLDER, TinkersTailor.creativeTabItems);
		setUnlocalizedName("TinkerTailor");
		setMaxStackSize(1);
		FMLCommonHandler.instance().bus().register(this);
	}


	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (world.isRemote) {
			openBook(stack, world, player);
		}
		return stack;
	}

	@SideOnly(Side.CLIENT)
	public void openBook(ItemStack stack, World world, EntityPlayer player) {
		FMLClientHandler.instance().displayGuiScreen(player, new GuiCustomManual(stack, getData(stack)));
	}

	@SideOnly(Side.CLIENT)
	private BookData getData(ItemStack stack) {
		return ManualHelper.bookData;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean debug) {
		super.addInformation(itemStack, entityPlayer, list, debug);
		list.add(Lang.translate("by RWTema"));
	}

	@SubscribeEvent
	public void onCrafting (PlayerEvent.ItemCraftedEvent event)
	{
		Item item = event.crafting.getItem();
		if (!event.player.worldObj.isRemote)
		{
			if (item == Item.getItemFromBlock(TinkerTools.toolStationWood))
			{
				if (!PHConstruct.beginnerBook)
				{
					return;
				}

				NBTTagCompound tag = event.player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
				if (!tag.getBoolean("TinkersTailorManual"))
				{
					tag.setBoolean("TinkersTailorManual", true);
					AbilityHelper.spawnItemAtPlayer(event.player, new ItemStack(TinkersTailor.manual));
				}
			}

		}
	}

}
