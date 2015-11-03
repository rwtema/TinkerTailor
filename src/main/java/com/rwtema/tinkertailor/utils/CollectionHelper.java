package com.rwtema.tinkertailor.utils;

import com.rwtema.tinkertailor.nbt.TinkersTailorConstants;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.RandomAccess;

public class CollectionHelper {
	public static <T> T getSingleElement(Collection<T> collection) {
		Iterator<T> iterator = collection.iterator();
		return iterator.hasNext() ? iterator.next() : null;
	}

	public static <T> T getRandomElement(Collection<T> collection) {
		return getRandomElement(collection, TinkersTailorConstants.RANDOM);
	}

	public static <T> T getRandomElement(Collection<T> collection, Random rand) {
		if (collection.isEmpty()) throw new IllegalArgumentException("Collections " + collection + " is Empty");

		if (collection instanceof RandomAccess) {
			return ((List<T>) collection).get(rand.nextInt(collection.size()));
		}

		int i = rand.nextInt(collection.size());
		Iterator<T> iterator = collection.iterator();
		T result = null;
		for (int j = 0; j <= i && iterator.hasNext(); j++) {
			result = iterator.next();
		}

		return result;
	}
}
