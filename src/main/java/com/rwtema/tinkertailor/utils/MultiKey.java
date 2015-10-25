package com.rwtema.tinkertailor.utils;

import java.util.Arrays;

public class MultiKey {
	private final Object[] objects;
	private int hash;

	public MultiKey(Object... objects) {
		this.objects = objects;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || !(o instanceof MultiKey)) return false;

		MultiKey that = (MultiKey) o;
		return Arrays.equals(objects, that.objects);

	}

	@Override
	public int hashCode() {
		int h = hash;
		if (h == 0 && this.objects != null) {
			Object objects[] = this.objects;

			h = 1 + objects.length;
			for (Object o : objects) {
				h = 31 * h + (o != null ? o.hashCode() : 0);
			}
			hash = h;
		}
		return h;
	}

	public static class Duo<A, B> extends MultiKey {
		public Duo(A a, B b) {
			super(a, b);
		}
	}

	public static class Tri<A, B, C> extends MultiKey {
		public Tri(A a, B b, C c) {
			super(a, b, c);
		}
	}

	public static class Quad<A, B, C, D> extends MultiKey {
		public Quad(A a, B b, C c, D d) {
			super(a, b, c, d);
		}
	}
}
