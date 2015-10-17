package com.rwtema.tinkertailor.caches;

import com.google.common.base.Throwables;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import javax.annotation.Nonnull;

public abstract class WeakCache<K, V> {
	private static final int OVERFULL_THRESHOLD = 32767;
	private final HashMap<WeakReference<K>, V> map = new HashMap<WeakReference<K>, V>();
	private final ReferenceQueue<K> refQueue = new ReferenceQueue<K>();
	private final Lookup lookup = new Lookup();

	@SuppressWarnings("SuspiciousMethodCalls")
	public synchronized V get(final K key) {
		expungeStaleEntries();
		if (map.size() > OVERFULL_THRESHOLD) {
			map.clear();
		}

		if (key == null) return getNullValue();

		V v;
		int hashCode = System.identityHashCode(key);

		try {
			lookup.set(key, hashCode);
			v = map.get(lookup);
			lookup.ref = null;
		} catch (Throwable err) {
			lookup.ref = null;
			throw Throwables.propagate(err);
		}

		if (v == null) {
			v = calc(key);
			if (v == null)
				throw new NullPointerException("Calculated null value for [" + key + "]");

			map.put(new WeakIdentityRef<K>(key, hashCode), v);
		}
		return v;
	}

	protected abstract V calc(@Nonnull K key);

	protected V getNullValue() {
		throw new NullPointerException();
	}

	@SuppressWarnings("SuspiciousMethodCalls")
	private void expungeStaleEntries() {
		Reference<? extends K> ref;
		while ((ref = refQueue.poll()) != null) {
			map.remove(ref);
		}
	}

	private final class WeakIdentityRef<T extends K> extends WeakReference<K> {
		private final int hashCode;

		public WeakIdentityRef(K referent, int hashCode) {
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

			if (!(other instanceof WeakIdentityRef)) {
				return false;
			}
			WeakIdentityRef wr = (WeakIdentityRef) other;

			return value == wr.get();
		}
	}

	private final class Lookup {
		K ref;
		int hash;

		public void set(K ref, int hashCode) {
			this.ref = ref;
			this.hash = hashCode;
		}

		@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
		@Override
		public boolean equals(Object obj) {
			return obj.equals(this);
		}

		@Override
		public int hashCode() {
			return hash;
		}
	}

}
