package com.rwtema.tinkertailor.items;

import com.rwtema.tinkertailor.TinkersTailor;
import com.rwtema.tinkertailor.manual.GuiCustomManual;
import com.rwtema.tinkertailor.manual.ManualHelper;
import com.rwtema.tinkertailor.nbt.TinkersTailorConstants;
import com.rwtema.tinkertailor.utils.Lang;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import mantle.books.BookData;
import mantle.items.abstracts.CraftingItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemTailorsBook extends CraftingItem {
	public ItemTailorsBook() {
		super(new String[]{"Book"}, new String[]{"TailorBook"}, "", TinkersTailorConstants.RESOURCE_FOLDER, TinkersTailor.creativeTabItems);
		setUnlocalizedName("TinkerTailor.Manual");
		setMaxStackSize(1);
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
		FMLClientHandler.instance().displayGuiScreen(player, new GuiCustomManual(stack, getData(stack)));
	}

	@SideOnly(Side.CLIENT)
	private BookData getData(ItemStack stack) {
		return ManualHelper.bookData;
	}

	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean debug) {
		super.addInformation(itemStack, entityPlayer, list, debug);
		list.add(Lang.translate("by RWTema"));
	}
}
