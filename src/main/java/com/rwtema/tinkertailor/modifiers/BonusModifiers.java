package com.rwtema.tinkertailor.modifiers;

import com.rwtema.tinkertailor.utils.LinearRegression2D;
import gnu.trove.map.hash.TIntFloatHashMap;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.ToolBuilder;
import tconstruct.tools.TinkerTools;

public class BonusModifiers {
	static TIntFloatHashMap cache;
	public static final int[] numMods = new int[]{2, 4, 3, 2};

	public static int getBonusModifiers(int material, int slot) {
		return baseLine + (int) Math.floor(numMods[slot] * getBonusModifiers(material) + 0.75F);
	}

	public static float getBonusModifiers(int material) {
		if (cache == null) return 0;
		if (cache.containsKey(material))
			return cache.get(material);

		float value = getBonusMultiplier(material);
		cache.put(material, value);
		return value;
	}


	public static float getBonusMultiplier(int mat) {
		LinearRegression2D regression = new LinearRegression2D();
		for (Item[] items : itemRecipes) {
			ItemStack[] stack = new ItemStack[4];
			int k = items.length;
			for (int i = 0; i < (1 << k); i++) {
				int t = 0;
				for (int j = 0; j < k; j++) {
					int meta;
					if ((i & (1 << j)) == 0) {
						meta = TinkerTools.MaterialID.Wood;
					} else {
						meta = mat;
						t++;

						if (items[j] == TinkerTools.shovelHead)
							t++;
					}

					stack[j] = new ItemStack(items[j], 1, meta);
				}

				ItemStack itemStack = buildStack(stack);

				if (itemStack != null) {
					regression.addXY(t, getModifiers(itemStack));
				}
			}
		}

		float m = regression.m();
		return Math.round(m * 8F) / 8F;
	}

	private static ItemStack buildStack(ItemStack[] stack) {
		return ToolBuilder.instance.buildTool(stack[0], stack[1], stack[2], stack[3], "Test");
	}

	public static List<Item[]> itemRecipes = new ArrayList<Item[]>();

	public static void init() {
		itemRecipes.add(new Item[]{TinkerTools.pickaxeHead, TinkerTools.toolRod, TinkerTools.binding});
		itemRecipes.add(new Item[]{TinkerTools.swordBlade, TinkerTools.toolRod, TinkerTools.wideGuard});
		itemRecipes.add(new Item[]{TinkerTools.hatchetHead, TinkerTools.toolRod});
		itemRecipes.add(new Item[]{TinkerTools.shovelHead, TinkerTools.toolRod});
		itemRecipes.add(new Item[]{TinkerTools.swordBlade, TinkerTools.toolRod, TinkerTools.handGuard});
		itemRecipes.add(new Item[]{TinkerTools.swordBlade, TinkerTools.toolRod, TinkerTools.crossbar});
		itemRecipes.add(new Item[]{TinkerTools.frypanHead, TinkerTools.toolRod});
		itemRecipes.add(new Item[]{TinkerTools.hatchetHead, TinkerTools.toolRod, TinkerTools.shovelHead});
		itemRecipes.add(new Item[]{TinkerTools.knifeBlade, TinkerTools.toolRod, TinkerTools.crossbar});
		itemRecipes.add(new Item[]{TinkerTools.swordBlade, TinkerTools.toolRod, TinkerTools.fullGuard});
		itemRecipes.add(new Item[]{TinkerTools.chiselHead, TinkerTools.toolRod});
		itemRecipes.add(new Item[]{TinkerTools.scytheBlade, TinkerTools.toughRod, TinkerTools.toughBinding, TinkerTools.toughRod});
		itemRecipes.add(new Item[]{TinkerTools.broadAxeHead, TinkerTools.toughRod, TinkerTools.largePlate, TinkerTools.toughBinding});
		itemRecipes.add(new Item[]{TinkerTools.largeSwordBlade, TinkerTools.toughRod, TinkerTools.largePlate, TinkerTools.toughRod});
		itemRecipes.add(new Item[]{TinkerTools.excavatorHead, TinkerTools.toughRod, TinkerTools.largePlate, TinkerTools.toughBinding});
		itemRecipes.add(new Item[]{TinkerTools.hammerHead, TinkerTools.toughRod, TinkerTools.largePlate, TinkerTools.largePlate});
		itemRecipes.add(new Item[]{TinkerTools.broadAxeHead, TinkerTools.toughRod, TinkerTools.broadAxeHead, TinkerTools.toughBinding});

		buildBaseLine();

		cache = new TIntFloatHashMap();
		for (Integer integer : TConstructRegistry.toolMaterials.keySet()) {
			getBonusModifiers(integer);
		}
	}

	static int baseLine = 3;

	public static void buildBaseLine() {

		int meta = TinkerTools.MaterialID.Wood;

		int n = 0;
		float Sx = 0;

		for (Item[] itemRecipe : itemRecipes) {
			ItemStack[] stacks = new ItemStack[4];
			for (int i = 0; i < itemRecipe.length; i++) {
				stacks[i] = new ItemStack(itemRecipe[i], 1, meta);
			}
			ItemStack itemStack = buildStack(stacks);
			if (itemStack != null) {
				n++;
				int modifiers = getModifiers(itemStack);
				Sx += modifiers;
			}
		}

		baseLine = Math.round(Sx / n);
	}

	public static int getModifiers(ItemStack stack) {
		if (stack == null || !stack.hasTagCompound()) return 0;
		return stack.getTagCompound().getCompoundTag("InfiTool").getInteger("Modifiers");
	}
}
