package com.rwtema.tinkertailor.utils.functions;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface ICallableClient {
	@SideOnly(Side.CLIENT)
	void run();
}
