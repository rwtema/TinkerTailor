package com.rwtema.tinkertailor.caches.base;

import com.google.common.base.Throwables;
import com.rwtema.tinkertailor.nbt.Config;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class WeakCache<K, V> {
	private static final boolean disableCache = Config.DisableAdvancedCache.get();
	private static final int OVERFULL_THRESHOLD = 10000;
	final HashMap<Object, V> map = createMap();
	final ReferenceQueue<K> refQueue = new ReferenceQueue<K>();
	final LookupReference lookup = new LookupReference();

	HashMap<Object, V> createMap() {
		return new HashMap<Object, V>(16, 0.5F);
	}

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
			v = map.get(lookup);
			lookup.key = null;
		} catch (Throwable err) {
			lookup.key = null;
			throw Throwables.propagate(err);
		}

		if (v == null) {
			v = calc(key);
			map.put(new WeakIdentityRef(key, hashCode), v);
		}

		return v;
	}

	@Nonnull
	protected abstract V calc(@Nonnull K key);

	protected V getNullValue() {
		throw new NullPointerException();
	}

	void expungeStaleEntries() {
		Reference ref;
		while ((ref = refQueue.poll()) != null) {
			map.remove(ref);
		}
	}

	final class WeakIdentityRef extends WeakReference<K> {
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

			if (other.getClass() != WeakIdentityRef.class) return false;

			WeakIdentityRef wr = (WeakIdentityRef) other;

			return hashCode == wr.hashCode && get() == wr.get();
		}


		@Override
		public String toString() {
			return "WeakKey{" + get() + ", " + hashCode + "}";
		}
	}

	final class LookupReference {
		K key;
		int hashCode;

		public void set(@Nonnull K ref, int hashCode) {
			this.key = ref;
			this.hashCode = hashCode;
		}

		@SuppressWarnings({"unchecked", "EqualsWhichDoesntCheckParameterClass"})
		@Override
		public boolean equals(Object obj) {
			return ((WeakIdentityRef) obj).get() == key;
		}

		@Override
		public int hashCode() {
			return hashCode;
		}

		@Override
		public String toString() {
			return "Lookup{" + key + ", " + hashCode + "}";
		}
	}
}
