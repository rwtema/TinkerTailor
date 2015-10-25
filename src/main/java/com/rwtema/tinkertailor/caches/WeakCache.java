package com.rwtema.tinkertailor.caches;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class WeakCache<K, V> extends CacheBase<K, V, V> {
	@Override
	protected final V insertCacheEntry(@Nonnull K key, int hashCode) {
		V v = calc(key);
		map.put(createWeakKey(key, hashCode), v);
		return v;
	}

	@Nullable
	@Override
	protected final V retrieve(@Nullable V valueStorage) {
		return valueStorage;
	}
}
