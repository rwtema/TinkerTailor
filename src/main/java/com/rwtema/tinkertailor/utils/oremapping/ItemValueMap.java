package com.rwtema.tinkertailor.utils.oremapping;

import java.util.List;
import net.minecraft.item.ItemStack;

public interface ItemValueMap {
	ItemValueMap put(Object s, int value);

	int get(ItemStack itemStack);

	void addItemsToList(final List<ItemStack> items);

	ItemStack makeItemStack();

}
