package com.rwtema.tinkertailor.utils.oremapping;

import com.rwtema.tinkertailor.TinkersTailor;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.custom_hash.TObjectIntCustomHashMap;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.strategy.IdentityHashingStrategy;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class OreIntMap implements ItemValueMap {

	public TObjectIntCustomHashMap<Item> genericItemStacks = null;
	public TObjectIntCustomHashMap<ItemStack> specificItemStacks = null;

	public static OreIntMap newMap(Object... param) {
		OreIntMap map = new OreIntMap();
		for (int i = 0; i < param.length; i += 1) {
			Object obj = param[i];

			if (obj == null || obj instanceof Integer) continue;

			int k;
			if ((i + 1) < param.length && param[i + 1] instanceof Integer) {
				k = (Integer) param[i + 1];
				i++;
			} else k = 1;

			map.put(obj, k);
		}
		return map;
	}

	public static ItemValueMap woodValues() {
		ItemValueMap ItemValueMap = new OreIntMap();

		ItemValueMap.put("stickWood", 1);
		ItemValueMap.put("plankWood", 2);
		ItemValueMap.put("logWood", 4);
		ItemValueMap.put("slabWood", 1);

		return ItemValueMap;
	}

	@Override
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

	private OreIntMap putOres(String s, int value) {
		for (ItemStack itemStack : OreDictionary.getOres(s, false)) {
			putItemStack(itemStack, value);
		}
		return this;
	}

	private OreIntMap putItemStack(ItemStack itemStack, int value) {
		if (itemStack.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
			putItem(itemStack.getItem(), value);
		} else {
			if (genericItemStacks != null && genericItemStacks.get(itemStack.getItem()) == value)
				return this;

			if (specificItemStacks == null)
				specificItemStacks = new TObjectIntCustomHashMap<ItemStack>(OreHashStrategy.INSTANCE, 10, 0.5F, 0);
			specificItemStacks.put(itemStack.copy(), value);
		}
		return this;
	}

	private OreIntMap putItem(Item item, int value) {
		if (genericItemStacks == null)
			genericItemStacks = new TObjectIntCustomHashMap<Item>(IdentityHashingStrategy.INSTANCE, 10, 0.5F, 0);

		genericItemStacks.put(item, value);

		if (specificItemStacks != null) {
			TObjectIntIterator<ItemStack> iterator = specificItemStacks.iterator();
			while (iterator.hasNext()) {
				iterator.advance();
				if (iterator.key().getItem() == item && iterator.value() == value)
					iterator.remove();
			}
			if (specificItemStacks.isEmpty()) specificItemStacks = null;
		}
		return this;
	}

	private OreIntMap putBlock(Block block, int value) {
		putItem(Item.getItemFromBlock(block), value);
		return this;
	}

	@Override
	public int get(ItemStack itemStack) {
		int i;
		if (specificItemStacks != null && (i = specificItemStacks.get(itemStack)) > 0) return i;
		if (genericItemStacks != null) return genericItemStacks.get(itemStack.getItem());
		return 0;
	}

	@Override
	public void addItemsToList(final List<ItemStack> items) {
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
	}

	@Override
	public ItemStack makeItemStack() {
		if (genericItemStacks != null && !genericItemStacks.isEmpty()) {
			for (Item item : genericItemStacks.keySet()) {
				List<ItemStack> itemStacks = TinkersTailor.proxy.addVariants(item, new ArrayList<ItemStack>());
				if (!itemStacks.isEmpty())
					return itemStacks.get(0);
			}
		}

		if (specificItemStacks != null) {
			for (ItemStack itemStack : specificItemStacks.keySet()) {
				if (itemStack != null)
					return itemStack.copy();
			}
		}

		return null;
	}
}
