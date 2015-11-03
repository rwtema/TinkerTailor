package com.rwtema.tinkertailor.caches;

import com.google.common.base.Throwables;
import com.rwtema.tinkertailor.nbt.Config;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class CacheBase<K, V, S> {
	private static final boolean disableCache = Config.DisableAdvancedCache.get();
	private static final int OVERFULL_THRESHOLD = 10000;
	protected final HashMap<WeakReference<K>, S> map = createMap();
	private final ReferenceQueue<K> refQueue = new ReferenceQueue<K>();
	private final Lookup lookup = new Lookup();

	protected HashMap<WeakReference<K>, S> createMap() {
		return new HashMap<WeakReference<K>, S>(16, 0.5F);
	}

	@SuppressWarnings("SuspiciousMethodCalls")
	public synchronized V get(@Nullable final K key) {
		if (disableCache) return key == null ? getNullValue() : calc(key);

		expungeStaleEntries();
		if (map.size() >= OVERFULL_THRESHOLD) {
			map.clear();
		}

		if (key == null) return getNullValue();

		V v;
		int hashCode = System.identityHashCode(key);

		try {
			lookup.set(key, hashCode);
			v = retrieve(map.get(lookup));
			lookup.ref = null;
		} catch (Throwable err) {
			lookup.ref = null;
			throw Throwables.propagate(err);
		}

		if (v == null) {
			v = insertCacheEntry(key, hashCode);
		}

		return v;
	}

	protected abstract V insertCacheEntry(@Nonnull K key, int hashCode);

	protected WeakIdentityRef createWeakKey(@Nonnull K key, int hashCode) {
		return new WeakIdentityRef(key, hashCode);
	}

	@Nullable
	protected abstract V retrieve(@Nullable S valueStorage);

	@Nonnull
	protected abstract V calc(@Nonnull K key);

	protected V getNullValue() {
		throw new NullPointerException();
	}

	@SuppressWarnings("SuspiciousMethodCalls")
	protected void expungeStaleEntries() {
		Reference ref;
		while ((ref = refQueue.poll()) != null) {
			map.remove(ref);
		}
	}

	protected final class WeakIdentityRef extends WeakReference<K> {
		private final int hashCode;

		public WeakIdentityRef(@Nonnull K referent, int hashCode) {
			super(referent, refQueue);
			this.hashCode = hashCode;
		}

		@Override
		public int hashCode() {
			return hashCode;
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean equals(Object other) {
			if (this == other) return true;

			Object value = get();
			if (value == null) return false;

			if (other == lookup) {
				return lookup.ref == value;
			}

			if (other.getClass() != WeakIdentityRef.class) {
				return false;
			}
			WeakIdentityRef wr = (WeakIdentityRef) other;

			return value == wr.get();
		}
	}

	private final class Lookup {
		K ref;
		int hash;

		public void set(@Nonnull K ref, int hashCode) {
			this.ref = ref;
			this.hash = hashCode;
		}

		@Override
		public boolean equals(Object obj) {
			return obj.getClass() == WeakIdentityRef.class && obj.equals(this);
		}

		@Override
		public int hashCode() {
			return hash;
		}
	}

}
