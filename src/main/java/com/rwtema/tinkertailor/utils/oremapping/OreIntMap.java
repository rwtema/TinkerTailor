package com.rwtema.tinkertailor.utils.oremapping;

import com.rwtema.tinkertailor.TinkersTailor;
import gnu.trove.map.custom_hash.TObjectIntCustomHashMap;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.strategy.IdentityHashingStrategy;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class OreIntMap {

	public TObjectIntCustomHashMap<Item> genericItemStacks = null;
	public TObjectIntCustomHashMap<ItemStack> specificItemStacks = null;

	public static OreIntMap newMap(Object... param) {
		OreIntMap map = new OreIntMap();
		for (int i = 0; i < param.length; i += 2) {
			int k;
			k = (i + 1) < param.length ? (Integer) param[i + 1] : 1;
			map.put(param[i], k);
		}
		return map;
	}

	public static OreIntMap woodValues() {
		OreIntMap oreIntMap = new OreIntMap();

		oreIntMap.put("stickWood", 1);
		oreIntMap.put("plankWood", 2);
		oreIntMap.put("logWood", 4);
		oreIntMap.put("slabWood", 1);

		return oreIntMap;
	}

	public OreIntMap put(Object s, int value) {
		if (s instanceof String)
			return putOres((String) s, value);
		if (s instanceof ItemStack)
			return putItemStack((ItemStack) s, value);
		if (s instanceof Item)
			return putItem((Item) s, value);
		if (s instanceof Block)
			return putBlock((Block) s, value);
		if (s instanceof List) {
			List list = (List) s;
			for (Object o : list) {
				put(o, value);
			}
			return this;
		}

		throw new IllegalArgumentException(String.valueOf(s));
	}

	public OreIntMap putOres(String s, int value) {
		for (ItemStack itemStack : OreDictionary.getOres(s, false)) {
			putItemStack(itemStack, value);
		}
		return this;
	}

	public OreIntMap putItemStack(ItemStack itemStack, int value) {
		if (itemStack.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
			putItem(itemStack.getItem(), value);
		} else {
			if (specificItemStacks == null)
				specificItemStacks = new TObjectIntCustomHashMap<ItemStack>(OreHashStrategy.INSTANCE, 10, 0.5F, 0);
			specificItemStacks.put(itemStack.copy(), value);
		}
		return this;
	}

	public OreIntMap putItem(Item item, int value) {
		if (genericItemStacks == null)
			genericItemStacks = new TObjectIntCustomHashMap<Item>(IdentityHashingStrategy.INSTANCE, 10, 0.5F, 0);

		genericItemStacks.put(item, value);
		return this;
	}

	public OreIntMap putBlock(Block block, int value) {
		putItem(Item.getItemFromBlock(block), value);
		return this;
	}

	public int get(ItemStack itemStack) {
		int i = 0;
		if (genericItemStacks != null) i = genericItemStacks.get(itemStack.getItem());
		if (i == 0 && specificItemStacks != null) i = specificItemStacks.get(itemStack);
		return i;
	}

	public ItemStack[] makeItemStackList() {
		final ArrayList<ItemStack> items = new ArrayList<ItemStack>();

		if (genericItemStacks != null)
			genericItemStacks.forEachKey(new TObjectProcedure<Item>() {
				@Override
				public boolean execute(final Item input) {
					TinkersTailor.proxy.addVariants(input, items);
					return true;
				}
			});

		if (specificItemStacks != null)
			specificItemStacks.forEachKey(new TObjectProcedure<ItemStack>() {
				@Override
				public boolean execute(ItemStack object) {
					return items.add(object.copy());
				}
			});

		return items.toArray(new ItemStack[items.size()]);
	}


}
