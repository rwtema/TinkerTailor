package com.rwtema.tinkertailor;

import com.rwtema.tinkertailor.blocks.TileEntityToolModifyStation;
import com.rwtema.tinkertailor.gui.GuiToolModifyStation;
import com.rwtema.tinkertailor.manual.ManualHelper;
import com.rwtema.tinkertailor.render.RendererHandler;
import com.rwtema.tinkertailor.utils.functions.ICallableClient;
import com.rwtema.tinkertailor.utils.functions.ISidedCallable;
import com.rwtema.tinkertailor.utils.functions.ISidedFunction;
import com.rwtema.tinkertailor.utils.ItemHelper;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import java.util.ArrayList;
import java.util.List;
import mantle.lib.client.MantleClientRegistry;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.client.TConstructClientRegistry;

public class ProxyClient extends Proxy {
	@Override
	public void run(ICallableClient callable) {
		callable.run();
	}

	@Override
	public void initSided() {
		RendererHandler.init();
	}

	@Override
	public void run(ISidedCallable callable) {
		callable.runClient();
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == 0)
			return new GuiToolModifyStation(player.inventory, (TileEntityToolModifyStation) world.getTileEntity(x, y, z), world, x, y, z);
		return null;
	}

	public <F, T> T apply(ISidedFunction<T, F> calc, F input) {
		return calc.applyClient(input);
	}

	@Override
	public void addShapedRecipe(String name, ItemStack output, Object... params) {
		ShapedOreRecipe recipe = new ShapedOreRecipe(output, params);
		GameRegistry.addRecipe(recipe);

		int w = ObfuscationReflectionHelper.getPrivateValue(ShapedOreRecipe.class, recipe, "width");
		int h = ObfuscationReflectionHelper.getPrivateValue(ShapedOreRecipe.class, recipe, "height");

		boolean is3x3 = w == 3 || h == 3;

		int k = is3x3 ? 3 : 2;

		ItemStack[] recipeItems = new ItemStack[k * k];
		Object[] input = recipe.getInput();
		fillRecipe(w, k, recipeItems, input);


		if (is3x3)
			MantleClientRegistry.registerManualLargeRecipe(name, output.copy(), recipeItems);
		else
			MantleClientRegistry.registerManualSmallRecipe(name, output.copy(), recipeItems);

	}

	private void fillRecipe(int w, int k, ItemStack[] recipeItems, Object[] input) {
		for (int i = 0; i < input.length; i++) {
			int j = (i % w) + k * (i / w);


			Object target = input[i];
			if (target instanceof ItemStack) {
				recipeItems[j] = (ItemStack) target;
			} else if (target instanceof ArrayList) {
				ArrayList<ItemStack> list = (ArrayList<ItemStack>) target;

				recipeItems[j] = null;

				for (ItemStack itemStack : list) {
					if (itemStack == null || itemStack.getItem() == null) continue;

					String modName = ItemHelper.getModName(itemStack.getItem());

					if ("minecraft".equals(modName)) {
						recipeItems[j] = itemStack.copy();
						break;
					}
					if (recipeItems[j] == null || "tinkers".equals(modName)) {
						recipeItems[j] = itemStack.copy();
					}
				}


			}
		}
	}

	@Override
	public void addShapelessRecipe(String name, ItemStack output, Object... params) {
		ShapelessOreRecipe recipe = new ShapelessOreRecipe(output, params);
		GameRegistry.addRecipe(recipe);
		ItemStack[] recipeItems = new ItemStack[9];
		ArrayList<Object> inputList = recipe.getInput();
		Object[] input = inputList.toArray(new Object[inputList.size()]);
		fillRecipe(3, 3, recipeItems, input);
		MantleClientRegistry.registerManualLargeRecipe(name, output.copy(), recipeItems);
	}

	@Override
	public List<ItemStack> addVariants(Item item, List<ItemStack> list) {
		CreativeTabs creativeTab = item.getCreativeTab();
		if (creativeTab != null) {
			try {
				item.getSubItems(item, creativeTab, list);
			} catch (Throwable throwable) {
				list.add(new ItemStack(item));
			}
		} else
			list.add(new ItemStack(item));
		return list;
	}

	@Override
	public void addCastingRecipe(String s, ItemStack output, FluidStack fluid, ItemStack input, boolean consumeCast, int i) {
		TConstructRegistry.getBasinCasting().addCastingRecipe(output, fluid, input, consumeCast, i);
		ItemStack fluidItemStack = null;
		Block block = fluid.getFluid().getBlock();
		if (block != null)
			fluidItemStack = new ItemStack(block);
		else {
			fluidItemStack = FluidContainerRegistry.fillFluidContainer(fluid, new ItemStack(Items.bucket));
		}

		if (fluidItemStack == null) return;

		TConstructClientRegistry.registerManualSmeltery(s, ItemStack.copyItemStack(output), fluidItemStack, ItemStack.copyItemStack(input));
	}

	@Override
	public void init() {
		ManualHelper.init();
	}

}
