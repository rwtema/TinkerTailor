package com.rwtema.tinkertailor.utils;

import cpw.mods.fml.common.registry.GameData;
import net.minecraft.item.Item;

public class ItemHelper {
	public static String getModName(Item item) {
		String s = GameData.getItemRegistry().getNameForObject(item);
		return s.substring(0, s.indexOf(':'));
	}
}
