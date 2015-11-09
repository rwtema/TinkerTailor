package com.rwtema.tinkertailor.caches.base;

import com.google.common.base.Throwables;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.HashMap;

public abstract class WeakMultiCache<V> {

	final Lookup lookup = new Lookup();
	final HashMap<Object, V> map = new HashMap<Object, V>();
	final ReferenceQueue<Object> refQueue = new ReferenceQueue<Object>();

	@SuppressWarnings("unchecked")
	void expungeStaleEntries() {
		Reference ref;
		while ((ref = refQueue.poll()) != null) {
			WeakRef weakRef = (WeakRef) ref;
			map.remove(weakRef.key);
		}
	}

	public static abstract class Pair<K1, K2, V> extends WeakMultiCache<V> {
		public Pair() {
			super();
			lookup.objects = new Object[2];
		}

		public synchronized V get(K1 k1, K2 k2) {
			expungeStaleEntries();
			V val;

			int hash = System.identityHashCode(k1) * 31 + System.identityHashCode(k2);

			Object[] lookups = lookup.objects;
			try {
				lookups[0] = k1;
				lookups[1] = k2;
				lookup.hash = hash;
				val = map.get(lookup);
				lookups[0] = lookups[1] = 0;
			} catch (Throwable err) {
				lookups[0] = lookups[1] = 0;
				throw Throwables.propagate(err);
			}

			if (val == null) {
				val = calc(k1, k2);
				map.put(new Key(hash, k1, k2), val);
			}
			return val;
		}

		protected abstract V calc(K1 k1, K2 k2);
	}

	public static abstract class Set<K, V> extends WeakMultiCache<V> {
		public Set() {
			super();
			lookup.objects = new Object[2];
		}

		public synchronized V get(K... keys) {
			expungeStaleEntries();
			V val;

			int hash = 1 + keys.length;
			for (K key : keys) {
				hash = hash * 31 + System.identityHashCode(key);
			}

			try {
				lookup.objects = keys;
				lookup.hash = hash;
				val = map.get(lookup);
				lookup.objects = null;
			} catch (Throwable err) {
				lookup.objects = null;
				throw Throwables.propagate(err);
			}

			if (val == null) {
				val = calc(keys);
				map.put(new Key(hash, keys), val);
			}
			return val;
		}

		protected abstract V calc(K... keys);
	}

	final class Key {
		WeakRef[] keys;
		int hash;

		public Key(int hash, Object... keys) {
			this.keys = new WeakRef[keys.length];
			for (int i = 0; i < keys.length; i++) {
				Object key = keys[i];
				this.keys[i] = new WeakRef<Object>(key, this);
			}

			this.hash = hash;
		}

		@Override
		@SuppressWarnings("unchecked")
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Key key = (Key) o;

			if (hash != key.hash) return false;
			if (keys.length != key.keys.length) return false;

			for (int i = 0; i < keys.length; i++) {
				Object k = keys[i].get();
				if (k == null || k != key.keys[i].get())
					return false;

			}
			return true;
		}

		@Override
		public String toString() {
			return "Key{" +
					"keys=" + Arrays.toString(keys) +
					", hash=" + hash +
					'}';
		}

		@Override
		public int hashCode() {
			return hash;
		}
	}

	final class WeakRef<T> extends WeakReference<T> {
		public Key key;

		public WeakRef(T referent, Key key) {
			super(referent, refQueue);
			this.key = key;
		}

		@Override
		public String toString() {
			return "WR[" + get() + "]";
		}
	}

	private final class Lookup {
		public Object[] objects;
		public int hash;

		@Override
		public int hashCode() {
			return hash;
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean equals(Object obj) {
			if (obj.getClass() != Key.class) return false;
			Key key = (Key) obj;
			if (hash != key.hash || objects.length != key.keys.length) return false;

			for (int i = 0; i < objects.length; i++) {
				if (objects[i] != key.keys[i].get())
					return false;
			}
			return true;
		}
	}


}
