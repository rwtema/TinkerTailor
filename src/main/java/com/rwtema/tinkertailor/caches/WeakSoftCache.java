package com.rwtema.tinkertailor.caches;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class WeakSoftCache<K, V> extends CacheBase<K, V, SoftReference<V>> {
	protected ReferenceQueue<? super V> softQueue = new ReferenceQueue<V>();

	@Nullable
	@Override
	protected V retrieve(@Nullable SoftReference<V> valueStorage) {
		return valueStorage == null ? null : valueStorage.get();
	}

	@Override
	protected V insertCacheEntry(@Nonnull K key, int hashCode) {
		V v = calc(key);
		WeakIdentityRef weakKey = createWeakKey(key, hashCode);
		map.put(weakKey, new SoftValueRef(v, weakKey));
		return v;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void expungeStaleEntries() {
		super.expungeStaleEntries();
		Reference ref;
		while ((ref = softQueue.poll()) != null) {
			map.remove(((SoftValueRef) ref).ref);
		}
	}

	protected final class SoftValueRef extends SoftReference<V> {
		private final WeakIdentityRef ref;

		public SoftValueRef(V referent, WeakIdentityRef ref) {
			super(referent, softQueue);
			this.ref = ref;
		}
	}
}
