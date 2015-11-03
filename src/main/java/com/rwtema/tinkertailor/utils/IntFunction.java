package com.rwtema.tinkertailor.utils;

import javax.annotation.Nonnull;

public interface IntFunction<T> {
	int calc(@Nonnull T input);

	IntFunction zeroFunction = new IntFunction() {
		@Override
		public int calc(@Nonnull Object input) {
			return 0;
		}
	};

	IntFunction oneFunction = new IntFunction() {
		@Override
		public int calc(@Nonnull Object input) {
			return 0;
		}
	};
}
