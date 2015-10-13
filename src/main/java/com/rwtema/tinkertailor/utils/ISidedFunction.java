package com.rwtema.tinkertailor.utils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface ISidedFunction<T, F> {

	@SideOnly(Side.CLIENT)
	T applyClient(F input);

	@SideOnly(Side.CLIENT)
	T applyServer(F input);
}
