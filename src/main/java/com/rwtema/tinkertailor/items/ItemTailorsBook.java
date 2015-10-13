package com.rwtema.tinkertailor.items;

import com.rwtema.tinkertailor.TinkersTailor;
import com.rwtema.tinkertailor.manual.ManualHelper;
import com.rwtema.tinkertailor.nbt.TinkersTailorConstants;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mantle.books.BookData;
import mantle.client.gui.GuiManual;
import mantle.items.abstracts.CraftingItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemTailorsBook extends CraftingItem {
	public ItemTailorsBook() {
		super(new String[]{"Book"}, new String[]{"TailorBook"}, "", TinkersTailorConstants.RESOURCE_FOLDER, TinkersTailor.creativeTabItems);
		setUnlocalizedName("tinkersTailor.manual");
	}


	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (world.isRemote) {
			openBook(stack, world, player);
		}
		return stack;
	}

	@SideOnly(Side.CLIENT)
	public void openBook(ItemStack stack, World world, EntityPlayer player) {
//		player.openGui(TConstruct.instance, mantle.client.MProxyClient.manualGuiID, world, 0, 0, 0);
		FMLClientHandler.instance().displayGuiScreen(player, new GuiManual(stack, getData(stack)));
	}

	@SideOnly(Side.CLIENT)
	private BookData getData(ItemStack stack) {
		return ManualHelper.bookData;
	}


}
