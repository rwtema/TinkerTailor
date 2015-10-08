package com.rwtema.tinkertailor;

import com.rwtema.tinkertailor.blocks.TileEntityToolModifyStation;
import com.rwtema.tinkertailor.gui.GuiToolModifyStation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class ProxyClient extends Proxy {
	@Override
	public void run(ICallableClient callable) {
		callable.run();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == 0)
			return new GuiToolModifyStation(player.inventory, (TileEntityToolModifyStation) world.getTileEntity(x, y, z), world, x, y, z);
		return null;
	}
}
