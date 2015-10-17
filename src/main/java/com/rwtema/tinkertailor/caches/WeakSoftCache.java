package com.rwtema.tinkertailor.caches;

import com.google.common.base.Throwables;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.HashMap;

public abstract class WeakSoftCache<K, V> {
	private static final int OVERFULL_THRESHOLD = 32767;
	private final HashMap<WeakReference<K>, SoftReference<V>> map = new HashMap<WeakReference<K>, SoftReference<V>>();
	private final ReferenceQueue<K> refQueueWeak = new ReferenceQueue<K>();
	private final ReferenceQueue<V> refQueueSoft = new ReferenceQueue<V>();
	private final Lookup lookup = new Lookup();

	@SuppressWarnings("SuspiciousMethodCalls")
	public synchronized V get(final K key) {
		expungeStaleEntries();
		if (map.size() > OVERFULL_THRESHOLD) {
			map.clear();
		}

		if (key == null) return getNullValue();

		SoftReference<V> ref;
		int hashCode = System.identityHashCode(key);

		try {
			lookup.set(key, hashCode);
			ref = map.get(lookup);
			lookup.ref = null;
		} catch (Throwable err) {
			lookup.ref = null;
			throw Throwables.propagate(err);
		}

		V v = ref != null ? ref.get() : null;

		if (v == null) {
			v = calc(key);
			if (v == null)
				throw new NullPointerException("Calculated null value for [" + key + "]");

			WeakIdentityRef<K> newKey = new WeakIdentityRef<K>(key, hashCode);
			SoftValueRef<V> newVal = new SoftValueRef<V>(v, newKey);
			newKey.softRef = newVal;
			map.put(newKey, newVal);
		}
		return v;
	}

	protected abstract V calc(K key);

	protected V getNullValue() {
		throw new NullPointerException();
	}

	@SuppressWarnings("SuspiciousMethodCalls")
	private void expungeStaleEntries() {
		Reference<? extends K> ref;
		while ((ref = refQueueWeak.poll()) != null) {
			ref.clear();
			map.remove(ref);
		}

		Reference<? extends V> softRef;
		while ((softRef = refQueueSoft.poll()) != null) {
			map.remove(((SoftValueRef<V>) softRef).weakRef);
		}
	}

	private final class WeakIdentityRef<T extends K> extends WeakReference<K> {
		private final int hashCode;
		public SoftValueRef<V> softRef;

		public WeakIdentityRef(K referent, int hashCode) {
			super(referent, refQueueWeak);
			this.hashCode = hashCode;
		}

		@Override
		public int hashCode() {
			return hashCode;
		}

		@Override
		public void clear() {
			super.clear();
			softRef.clear();
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

	private final class SoftValueRef<T extends V> extends SoftReference<V> {

		final WeakIdentityRef<K> weakRef;

		public SoftValueRef(V value, WeakIdentityRef<K> weakRef) {
			super(value, refQueueSoft);
			this.weakRef = weakRef;
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
