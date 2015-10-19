package com.rwtema.tinkertailor.utils;

import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraftforge.oredict.OreDictionary;
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

	public static ItemStack[] makeStackArray(Object... items) {
		ArrayList<ItemStack> list = makeStackList(items);

		return list.toArray(new ItemStack[list.size()]);
	}

	public static ArrayList<ItemStack> makeStackList(Object... items) {
		ArrayList<ItemStack> list = new ArrayList<ItemStack>(items.length);
		for (int i = 0; i < items.length; i++) {
			Object o = items[i];
			if(o == null) continue;

			if ((i + 1) < items.length && items[i + 1] instanceof Integer) {
				if (items[i] instanceof Item)
					list.add(new ItemStack((Item) o, 1, (Integer) items[i + 1]));
				else if (items[i] instanceof Block)
					list.add(new ItemStack((Block) o, 1, (Integer) items[i + 1]));
			} else {
				ItemStack item = makeStack(o);
				if (item != null) {
					list.add(item);
				}
			}
		}
		return list;
	}

	public static ItemStack makeStack(Object obj) {
		if (obj == null) return null;
		if (obj instanceof ItemStack)
			return checkStack((ItemStack) obj);
		else if (obj instanceof Item)
			return new ItemStack((Item) obj);
		else if (obj instanceof Block)
			return new ItemStack((Block) obj);
		else if (obj instanceof String)
			return getSingleStack(OreDictionary.getOres((String) obj, false));
		else if (obj instanceof List)
			return getSingleStack((List<ItemStack>) obj);
		else if (obj instanceof ItemStack[])
			return getSingleStack(Arrays.asList((ItemStack[]) obj));

		return null;
	}

	public static ItemStack checkStack(ItemStack obj) {
		return obj == null || obj.getItem() == null ? null : obj;
	}

	public static ItemStack getSingleStack(Iterable<ItemStack> list) {
		ItemStack a = null;
		for (ItemStack itemStack : list) {
			if (itemStack == null || itemStack.getItem() == null) continue;

			String modName = ItemHelper.getModName(itemStack.getItem());

			if ("minecraft".equals(modName)) {
				return itemStack.copy();

			}
			if (a == null || "tinkers".equals(modName)) {
				a = itemStack.copy();
			}
		}
		return a;
	}
}
