package com.rwtema.tinkertailor;

import com.rwtema.tinkertailor.utils.ICallableClient;
import com.rwtema.tinkertailor.utils.ISidedCallable;
import com.rwtema.tinkertailor.utils.ISidedFunction;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import mantle.blocks.abstracts.InventoryLogic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import tconstruct.library.TConstructRegistry;

public class Proxy implements IGuiHandler {
	public void run(ICallableClient ICallableClient) {

	}

	public void run(ISidedCallable callable){
		callable.runServer();
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile != null && tile instanceof InventoryLogic) {
			return ((InventoryLogic) tile).getGuiContainer(player.inventory, world, x, y, z);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}


	public <F, T> T apply(ISidedFunction<T, F> calc, F input) {
		return calc.applyServer(input);
	}

	public void preInit() {

	}

	public void init() {

	}

	public void addShapedRecipe(String name, ItemStack output, Object... params) {
		GameRegistry.addRecipe(new ShapedOreRecipe(output, params));
	}

	public void addCastingRecipe(String s, ItemStack output, FluidStack fluid, ItemStack input, boolean consumeCast, int i) {
		TConstructRegistry.getBasinCasting().addCastingRecipe(output, fluid, input, consumeCast, i);
	}
}
