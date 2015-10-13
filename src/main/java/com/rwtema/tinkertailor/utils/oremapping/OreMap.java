package com.rwtema.tinkertailor.utils.oremapping;

import gnu.trove.map.hash.TCustomHashMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.procedure.TObjectProcedure;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class OreMap<K> {
	public THashMap<Item, K> genericItemStacks = null;
	public TCustomHashMap<ItemStack, K> specificItemStacks = null;

	public OreMap<K> put(Object s, K value) {
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
		}

		throw new IllegalArgumentException();
	}

	public OreMap<K> putOres(String s, K value) {
		for (ItemStack itemStack : OreDictionary.getOres(s, false)) {
			putItemStack(itemStack, value);
		}
		return this;
	}

	public OreMap<K> putItemStack(ItemStack itemStack, K value) {
		if (itemStack.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
			putItem(itemStack.getItem(), value);
		} else {
			if (specificItemStacks == null)
				specificItemStacks = new TCustomHashMap<ItemStack, K>(OreHashStrategy.INSTANCE, 10, 0.5F);
			specificItemStacks.put(itemStack.copy(), value);
		}
		return this;
	}

	public OreMap<K> putItem(Item item, K value) {
		if (genericItemStacks == null)
			genericItemStacks = new THashMap<Item, K>();

		genericItemStacks.put(item, value);
		return this;
	}

	public OreMap<K> putBlock(Block block, K value) {
		putItem(Item.getItemFromBlock(block), value);
		return this;
	}

	public K get(ItemStack itemStack) {
		K value = null;
		if (genericItemStacks != null) value = genericItemStacks.get(itemStack.getItem());
		if (value == null && specificItemStacks != null) value = specificItemStacks.get(itemStack);
		return value;
	}

	public ItemStack[] makeItemStackList() {
		final ArrayList<ItemStack> items = new ArrayList<ItemStack>();

		if (genericItemStacks != null)
			genericItemStacks.forEachKey(new TObjectProcedure<Item>() {
				@Override
				public boolean execute(Item object) {
					return items.add(new ItemStack(object));
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
