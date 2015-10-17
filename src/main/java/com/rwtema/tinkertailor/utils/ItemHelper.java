package com.rwtema.tinkertailor.utils;

import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import tconstruct.library.tools.Weapon;
import tconstruct.library.weaponry.ProjectileWeapon;

public class ItemHelper {
	public static String getModName(Item item) {
		String s = GameData.getItemRegistry().getNameForObject(item);
		return s.substring(0, s.indexOf(':'));
	}

	@SideOnly(Side.CLIENT)
	public static boolean isPlayerHoldingWeapon() {
		EntityClientPlayerMP thePlayer = Minecraft.getMinecraft().thePlayer;
		if (thePlayer == null) return false;
		ItemStack heldItem = thePlayer.getHeldItem();
		return isWeapon(heldItem);
	}

	public static boolean isWeapon(ItemStack heldItem) {
		Item item;
		if (heldItem == null || (item = heldItem.getItem()) == null) return false;
		return item instanceof ItemSword || item instanceof ItemBow || item instanceof Weapon || item instanceof ProjectileWeapon;


	}
}
