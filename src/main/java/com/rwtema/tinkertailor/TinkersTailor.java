package com.rwtema.tinkertailor;

import com.rwtema.tinkertailor.blocks.BlockToolModifyStation;
import com.rwtema.tinkertailor.blocks.TileEntityToolModifyStation;
import com.rwtema.tinkertailor.compat.ModCompatibilityModule;
import com.rwtema.tinkertailor.coremod.CoreTinkerTailor;
import com.rwtema.tinkertailor.crafting.BlockArmorCast;
import com.rwtema.tinkertailor.imc.IMCHandler;
import com.rwtema.tinkertailor.imc.IMCNBTLoader;
import com.rwtema.tinkertailor.items.ArmorCore;
import com.rwtema.tinkertailor.items.ItemArmorCast;
import com.rwtema.tinkertailor.items.ItemArmorPattern;
import com.rwtema.tinkertailor.items.ItemChainmail;
import com.rwtema.tinkertailor.items.ItemTailorsManual;
import com.rwtema.tinkertailor.modifiers.BonusModifiers;
import com.rwtema.tinkertailor.modifiers.ModifierRegistry;
import com.rwtema.tinkertailor.nbt.Config;
import com.rwtema.tinkertailor.nbt.TinkersTailorConstants;
import com.rwtema.tinkertailor.render.font.CustomFontRenderer;
import com.rwtema.tinkertailor.utils.functions.ICallableClient;
import cpw.mods.fml.common.LoaderException;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.net.URL;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tconstruct.TConstruct;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.FluidType;
import tconstruct.library.crafting.LiquidCasting;
import tconstruct.library.crafting.PatternBuilder;
import tconstruct.library.crafting.Smeltery;
import tconstruct.smeltery.TinkerSmeltery;
import tconstruct.tools.TinkerTools;

@Mod(name = TinkersTailorConstants.MOD_ID, modid = TinkersTailorConstants.MOD_ID, dependencies = "required-after:TConstruct;after:Natura")
public class TinkersTailor {

	public static final Logger logger = LogManager.getLogger(TinkersTailorConstants.MOD_ID);
	public static final boolean deobf;
	public static final boolean deobf_folder;
	@Mod.Instance(TinkersTailorConstants.MOD_ID)
	public static TinkersTailor instance;
	public static ArmorCore hat;
	public static final CreativeTabs creativeTabArmor = new CreativeTabs("tinkertailor.armors") {
		@SideOnly(Side.CLIENT)
		private ItemStack icon;
		@SideOnly(Side.CLIENT)
		private long time;
		@SideOnly(Side.CLIENT)
		private Random rand;
		@SideOnly(Side.CLIENT)
		private int materials[];

		@Override
		@SideOnly(Side.CLIENT)
		public Item getTabIconItem() {
			return hat;
		}

		@SideOnly(Side.CLIENT)
		public ItemStack getIconItemStack() {
			if (icon == null || (System.currentTimeMillis() - time) > 2000) {
				if (materials == null) {
					materials = new int[TConstructRegistry.toolMaterials.size()];
					int i = 0;
					for (int m : TConstructRegistry.toolMaterials.keySet()) {
						materials[i++] = m;
					}
					rand = new Random();
				}

				ArmorCore armor = ArmorCore.armors[rand.nextInt(4)];
				int material = materials[rand.nextInt(materials.length)];

				icon = armor.createDefaultStack(material);
				time = System.currentTimeMillis();
			}

			return icon;
		}

		@Override
		public void displayAllReleventItems(List list) {
			for (ArmorCore armor : ArmorCore.armors) {
				armor.getSubItems(armor, this, list);
			}
		}
	};
	public static ArmorCore shirt;
	public static ArmorCore trousers;
	public static ArmorCore shoes;
	public static Block toolModifyStation;
	public static BlockArmorCast armorCast;
	public static final CreativeTabs creativeTabItems = new CreativeTabs("tinkertailor.items") {
		@Override
		public Item getTabIconItem() {
			return Item.getItemFromBlock(armorCast);
		}
	};
	public static ItemArmorPattern armorPattern;
	public static ItemTailorsManual manual;
	@SidedProxy(serverSide = "com.rwtema.tinkertailor.Proxy", clientSide = "com.rwtema.tinkertailor.ProxyClient")
	public static Proxy proxy;

	public static List<ModCompatibilityModule> modCompatabilities;

	static {
		boolean _deObf;
		try {
			World.class.getDeclaredMethod("getBlock", int.class, int.class, int.class);
			_deObf = true;
		} catch (NoSuchMethodException e) {
			_deObf = false;
		}
		deobf = _deObf;


		if (deobf) {
			URL resource = TinkersTailor.class.getClassLoader().getResource(TinkersTailor.class.getName().replace('.', '/').concat(".class"));
			deobf_folder = resource != null && "file".equals(resource.getProtocol());
		} else
			deobf_folder = false;

		if (!CoreTinkerTailor.loaded) {
			String message = "TinkersTailor CoreMod Failed To Load";
			if (deobf) {
				message = message + ": Add to VM Options:  \"-Dfml.coreMods.load=" + CoreTinkerTailor.class.getName() + "\"";
			}
			throw new LoaderException(message);
		}
	}

	@Mod.EventHandler
	public void preinit(FMLPreInitializationEvent event) {
		Config.load(new Configuration(event.getSuggestedConfigurationFile()));

		modCompatabilities = ModCompatibilityModule.loadModCompatibilityModules(event);
		for (ModCompatibilityModule modCompatibilityModule : modCompatabilities) {
			modCompatibilityModule.onCreated();
		}

		proxy.initSided();

		DamageEventHandler.instance.register();
//		MinecraftForge.EVENT_BUS.register(new SpawnHandler());

		NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);

		hat = (ArmorCore) new ArmorCore(0).setUnlocalizedName("TinkerTailor.Hat");
		GameRegistry.registerItem(hat, "helmet");
		shirt = (ArmorCore) new ArmorCore(1).setUnlocalizedName("TinkerTailor.Shirt");
		GameRegistry.registerItem(shirt, "chestplate");
		trousers = (ArmorCore) new ArmorCore(2).setUnlocalizedName("TinkerTailor.Trousers");
		GameRegistry.registerItem(trousers, "leggings");
		shoes = (ArmorCore) new ArmorCore(3).setUnlocalizedName("TinkerTailor.Shoes");
		GameRegistry.registerItem(shoes, "boots");

		GameRegistry.registerItem(new ItemChainmail(), "chainmetal");

		armorCast = new BlockArmorCast();
		GameRegistry.registerBlock(armorCast, ItemArmorCast.class, "ArmorCast");
		armorPattern = new ItemArmorPattern();
		GameRegistry.registerItem(armorPattern, "ArmorPattern");

		toolModifyStation = new BlockToolModifyStation(Material.wood).setBlockName("TinkerTailor.ToolModifyStation");
		GameRegistry.registerBlock(toolModifyStation, "ToolModifyStation");
		GameRegistry.registerTileEntity(TileEntityToolModifyStation.class, "ToolModifyStation");

		manual = new ItemTailorsManual();
		GameRegistry.registerItem(manual, "Book");

		proxy.preInit();

		for (ModCompatibilityModule modCompatibilityModule : modCompatabilities) {
			modCompatibilityModule.preInit();
		}
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {

		for (ModCompatibilityModule modCompatibilityModule : modCompatabilities) {
			modCompatibilityModule.initStart();
		}

		proxy.run(new ICallableClient() {
			@Override
			@SideOnly(Side.CLIENT)
			public void run() {
				CustomFontRenderer.instance = new CustomFontRenderer();
			}
		});

		ModifierRegistry.init();
		PatternBuilder.instance.addToolPattern(armorPattern);

		proxy.addShapedRecipe("armorPatternHelmet", new ItemStack(armorPattern, 1, 0), "SSS", "S S", 'S', TinkerTools.blankPattern);
		proxy.addShapedRecipe("armorPatternChestplate", new ItemStack(armorPattern, 1, 1), "S S", "SSS", "SSS", 'S', TinkerTools.blankPattern);
		proxy.addShapedRecipe("armorPatternLeggings", new ItemStack(armorPattern, 1, 2), "SSS", "S S", "S S", 'S', TinkerTools.blankPattern);
		proxy.addShapedRecipe("armorPatternBoots", new ItemStack(armorPattern, 1, 3), "S S", "S S", 'S', TinkerTools.blankPattern);


		proxy.addShapelessRecipe("manual", new ItemStack(manual), TinkerTools.manualBook, TinkerTools.blankPattern, Items.iron_ingot);


		LiquidCasting basinCasting = TConstructRegistry.getBasinCasting();
		for (int meta = 0; meta < 4; meta++) {
			ItemStack metalCast = new ItemStack(TinkersTailor.armorCast, 1, meta);

			int[] liquidDamage = new int[]{2, 13, 10, 11, 12, 14, 15, 6, 16, 18}; // ItemStack

			for (int i = 0; i < TinkerSmeltery.liquids.length; i++) {
				Fluid fs = TinkerSmeltery.liquids[i].getFluid();
				int fluidAmount = (TinkersTailorConstants.slotCost[meta] * TConstruct.ingotLiquidValue / 2);
				ItemStack output = ArmorCore.armors[meta].createDefaultStack(liquidDamage[i]);
				basinCasting.addCastingRecipe(output, new FluidStack(fs, fluidAmount), metalCast, 100);
//				Smeltery.addMelting(FluidType.getFluidType(fs), output, 0, fluidAmount);
			}

			for (int mat = 0; mat < TinkersTailorConstants.NON_METALS.length; mat++) {
				TConstructRegistry.addPartMapping(TinkersTailor.armorPattern, meta, mat, ArmorCore.armors[meta].createDefaultStack(mat));
			}

		}

		for (int meta = 0; meta < 4; meta++) {
			FluidStack alBrass = new FluidStack(TinkerSmeltery.moltenAlubrassFluid, TConstruct.blockLiquidValue);
			ItemStack output = new ItemStack(armorCast, 1, meta);

			ItemStack input = ArmorCore.armors[meta].createDefaultStack(TinkerTools.MaterialID.Stone);
			input.setItemDamage(Short.MAX_VALUE);
			proxy.addCastingRecipe("cast" + TinkersTailorConstants.NAMES[meta], output, alBrass, input, true, 100);

			Smeltery.addMelting(FluidType.getFluidType(TinkerSmeltery.moltenAlubrassFluid), output, 0, TConstruct.blockLiquidValue);
		}

		if (Config.SoftMetal.get()) {
			for (int mat : new int[]{TinkerTools.MaterialID.Copper, TinkerTools.MaterialID.Iron}) {
				for (int meta = 0; meta <= 12; meta++) {
					TConstructRegistry.addPartMapping(TinkerTools.woodPattern, meta + 1, mat, new ItemStack(TinkerTools.patternOutputs[meta], 1, mat));
				}

				for (int meta = 0; meta < 4; meta++) {
					TConstructRegistry.addPartMapping(TinkersTailor.armorPattern, meta, mat, ArmorCore.armors[meta].createDefaultStack(mat));
				}
			}
		}


		ItemStack smelteryStack = TinkerSmeltery.smeltery != null ? new ItemStack(TinkerSmeltery.smeltery, 1, 2) : new ItemStack(Blocks.obsidian, 1, 0);
		ItemStack searedbrick = TinkerSmeltery.smeltery != null ? new ItemStack(TinkerTools.materials, 1, 2) : new ItemStack(Items.brick);
		proxy.addShapedRecipe("modifyStation", new ItemStack(toolModifyStation, 1), "bbb", "msm", "m m", 'b', smelteryStack, 's', new ItemStack(TinkerTools.toolStationWood, 1, 0), 'm', searedbrick);

		proxy.init();

		for (ModCompatibilityModule modCompatibilityModule : modCompatabilities) {
			modCompatibilityModule.initEnd();
		}

		if (deobf_folder) {
			IMCNBTLoader.sendTest();
		}
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		BonusModifiers.init();
		for (ModCompatibilityModule modCompatibilityModule : modCompatabilities) {
			modCompatibilityModule.postInit();
		}
	}

	@Mod.EventHandler
	public void handleIMC(FMLInterModComms.IMCEvent e) {
		IMCHandler.processIMC(e.getMessages(), false);
	}

	@Mod.EventHandler
	public void loadComplete(FMLLoadCompleteEvent event) {
//		double dungeonProbability = Config.DungeonProbability.get();
//		if (dungeonProbability > 1e-5) {
//			ItemHelper.addDungeonItem(new ItemStack(hat), 1, 1, ChestGenHooks.DUNGEON_CHEST, dungeonProbability / 4);
//			ItemHelper.addDungeonItem(new ItemStack(shirt), 1, 1, ChestGenHooks.DUNGEON_CHEST, dungeonProbability / 4);
//			ItemHelper.addDungeonItem(new ItemStack(trousers), 1, 1, ChestGenHooks.DUNGEON_CHEST, dungeonProbability / 4);
//			ItemHelper.addDungeonItem(new ItemStack(shoes), 1, 1, ChestGenHooks.DUNGEON_CHEST, dungeonProbability / 4);
//		}

		IMCHandler.processIMC(FMLInterModComms.fetchRuntimeMessages(this), true);

		for (ModCompatibilityModule modCompatibilityModule : modCompatabilities) {
			modCompatibilityModule.loadComplete();
		}

		Config.save();
	}
}
