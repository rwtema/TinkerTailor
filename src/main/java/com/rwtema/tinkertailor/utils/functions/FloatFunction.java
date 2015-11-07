package com.rwtema.tinkertailor.utils.functions;

import javax.annotation.Nonnull;

public interface FloatFunction<T> {
	float calc(@Nonnull T input);

	FloatFunction zeroFunction = new FloatFunction() {
		@Override
		public float calc(@Nonnull Object input) {
			return 0;
		}
	};
}
