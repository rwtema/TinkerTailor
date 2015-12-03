package com.rwtema.tinkertailor.caches.base;

import com.google.common.base.Throwables;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.HashMap;

public abstract class WeakMultiCache<V> {

	final Lookup lookup = new Lookup();
	final HashMap<Object, V> map = createMap();

	protected HashMap<Object, V> createMap() {
		return new HashMap<Object, V>(16, 0.25F);
	}

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

	public static abstract class Array<K, V> extends WeakMultiCache<V> {
		public Array() {
			super();
			this.lookup.objects = new Object[0];
		}

		public synchronized V get(K... keys) {
			expungeStaleEntries();
			V val;

			int hash = keys.length;
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
				if (key != null)
					this.keys[i] = new WeakRef<Object>(key, this);
			}

			this.hash = hash;
		}

		@Override
		@SuppressWarnings("unchecked")
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || o.getClass() != Key.class) return false;

			Key key = (Key) o;

			if (hash != key.hash) return false;
			WeakRef[] otherKeys = key.keys;
			if (this.keys.length != otherKeys.length) return false;

			for (int i = 0; i < this.keys.length; i++) {
				WeakRef ref = this.keys[i];
				WeakRef otherRef = otherKeys[i];

				if (ref == null) {
					if (otherRef != null) return false;
				} else {
					if (otherRef == null) return false;

					Object k = ref.get();

					if (k == null || k != otherRef.get())
						return false;
				}
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
			if (obj == null || obj.getClass() != Key.class) return false;

			Key key = (Key) obj;
			WeakRef[] keys = key.keys;
			if (hash != key.hash || objects.length != keys.length) return false;

			for (int i = 0; i < objects.length; i++) {
				WeakRef ref = keys[i];
				if (ref == null) {
					if (objects[i] != null) return false;
				} else {
					Object o = ref.get();
					if (o == null || objects[i] != o)
						return false;
				}
			}
			return true;
		}
	}


}
