package com.rwtema.tinkertailor.nbt;

import com.rwtema.tinkertailor.TinkersTailor;

public enum ConfigKeys {
	SoftMetal("SoftMetalPartsCrafting", "Enable 'simple' iron/copper tool parts being buildable in the part builder."),

//	StencilIndex("StencilIndex", "Initial index value for TTail stencil range. Requires 4 free indexes."),;
;

	private final String category;
	private final String key;
	private final String comment;


	ConfigKeys(String key, String comment) {
		this(TinkersTailorConstants.CONFIG_MAIN_CATEGORY, key, comment);
	}

	ConfigKeys(String category, String key, String comment) {

		this.category = category;
		this.key = key;
		this.comment = comment;
	}

	public boolean getBool(boolean _default) {
		return TinkersTailor.config.get(category, key, _default, comment).getBoolean(_default);
	}

	public int getInt(int _default) {
		return TinkersTailor.config.get(category, key, _default, comment).getInt(_default);
	}
}
