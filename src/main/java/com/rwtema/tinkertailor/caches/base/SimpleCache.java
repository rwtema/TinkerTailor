package com.rwtema.tinkertailor.caches.base;

import java.util.HashMap;
import javax.annotation.Nonnull;

public abstract class SimpleCache<K, V> {
	private final HashMap<K, V> map = new HashMap<K, V>();

	public synchronized V get(final K key) {
		if (key == null) return getNullValue();
		V v = map.get(key);
		if (v == null) {
			v = calc(key);
			map.put(key, v);
		}
		return v;
	}

	@Nonnull
	protected abstract V calc(K key);

	protected V getNullValue() {
		throw new NullPointerException();
	}

}
