package com.rwtema.tinkertailor.nbt;

import java.util.UUID;

public class TinkerTailorConstants {
	public final static String NBT_MAINTAG = "InfiTool";
	public final static String NBT_MAINTAG_MATERIAL = "Material";
	public final static String NBT_MAINTAG_RENDERID = "RenderID";


	public final static String MODIFY_TYPE = "TH_Armor";
	public final static String RESOURCE_FOLDER = "tinkertailor";

	public final static int[] NON_METALS = {0, 1, 3, 4, 5, 6, 7, 8, 9, 17};
	public final static String MOD_ID = "TinkerTailor";
	public final static String VERSION = "1B";

	public final static UUID UUID_ITEMS = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");

	public static final String CONFIG_MAIN_CATEGORY = "TinkerTailor";

	public static class NBT_IDs {
		public static final int TAG_END = 0;
		public static final int TAG_BYTE = 1;
		public static final int TAG_SHORT = 2;
		public static final int TAG_INT = 3;
		public static final int TAG_LONG = 4;
		public static final int TAG_FLOAT = 5;
		public static final int TAG_DOUBLE = 6;
		public static final int TAG_BYTE_ARRAY = 7;
		public static final int TAG_STRING = 8;
		public static final int TAG_LIST = 9;
		public static final int TAG_COMPOUND = 10;
		public static final int TAG_INT_ARRAY = 11;
		public static final int TAG_ANY_NUMERIC = 99;
	}
}
