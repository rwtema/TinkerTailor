package com.rwtema.tinkertailor.caches.base;

import java.util.ArrayList;
import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;

public abstract class WeakItemStackCache<V> extends WeakCache<ItemStack, V> {
	static ArrayList<WeakCache<ItemStack, ?>> dependencies = new ArrayList<WeakCache<ItemStack, ?>>();

	public WeakItemStackCache() {
		dependencies.add(this);
	}

	public static void clearAllCaches(ItemStack key) {
		for (WeakCache<ItemStack, ?> dependency : dependencies) {
			dependency.clear(key);
		}
	}

	@Nonnull
	@Override
	protected abstract V calc(ItemStack stack);

}
