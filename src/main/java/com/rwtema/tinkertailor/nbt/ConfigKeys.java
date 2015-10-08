package com.rwtema.tinkertailor.nbt;

import com.rwtema.tinkertailor.TinkerTailor;

public enum ConfigKeys {
	SoftMetal("SoftMetalPartsCrafting", "Enable 'simple' iron/copper tool parts being buildable in the part builder."),
	ModifyStation("EnabledToolModifierStation", "Enable Tool modifier station."),
	StencilIndex("StencilIndex", "Initial index value for TTail stencil range. Requires 4 free indexes."),;


	private final String category;
	private final String key;
	private final String comment;


	ConfigKeys(String key, String comment) {
		this(TinkerTailorConstants.CONFIG_MAIN_CATEGORY, key, comment);
	}

	ConfigKeys(String category, String key, String comment) {

		this.category = category;
		this.key = key;
		this.comment = comment;
	}

	public boolean getBool(boolean _default) {
		return TinkerTailor.config.get(category, key, _default, comment).getBoolean(_default);
	}

	public int getInt(int _default) {
		return TinkerTailor.config.get(category, key, _default, comment).getInt(_default);
	}
}
