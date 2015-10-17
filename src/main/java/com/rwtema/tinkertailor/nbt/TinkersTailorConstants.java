package com.rwtema.tinkertailor.nbt;

import java.text.DecimalFormat;
import java.util.Random;
import java.util.UUID;

public class TinkersTailorConstants {
	public final static String NBT_MAINTAG = "InfiTool";
	public final static String NBT_MAINTAG_MATERIAL = "Material";
	public final static String NBT_MAINTAG_RENDERID = "RenderID";
	public final static String NBT_MAINTAG_BROKEN = "Broken";

	public final static String NBT_MAINTAG_MODIFIERS = "Modifiers";
	public final static String NBT_MAINTAG_REPAIRCOUNT = "RepairCount";


	public final static String MODIFY_TYPE = "TH_Armor";
	public final static String RESOURCE_FOLDER = "tinkertailor";

	public final static int[] NON_METALS = {0, 1, 3, 4, 5, 6, 7, 8, 9, 17};
	public final static String MOD_ID = "TinkersTailor";
	public final static String VERSION = "1B";

	public final static UUID UUID_ITEMS = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");

	public static final String CONFIG_MAIN_CATEGORY = "TinkersTailor";

	public static final Random RANDOM = new Random();

	public static final String[] NAMES = {"Helmet", "Chestplate", "Leggings", "Boots"};
	public static final String[] UNLOCAL_NAMES = {
			"item.TinkerTailor.Hat.name",
			"item.TinkerTailor.Shirt.name",
			"item.TinkerTailor.Trousers.name",
			"item.TinkerTailor.Shoes.name"
	};

	public static final String[] EMPTY_STRING_ARRAY = {};

	public static final DecimalFormat formatTwoDigits = new DecimalFormat("#.##");
	public static final DecimalFormat formatTenDigits = new DecimalFormat("#.##########");


}
