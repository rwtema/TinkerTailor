package com.rwtema.tinkertailor.utils.functions;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface ISidedCallable {
	@SideOnly(Side.SERVER)
	void runServer();

	@SideOnly(Side.CLIENT)
	void runClient();
}
