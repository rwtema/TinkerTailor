package com.rwtema.tinkertailor.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

public class CollectionHelper {
	public static <T> T getSingleElement(Collection<T> collection) {
		Iterator<T> iterator = collection.iterator();
		return iterator.hasNext() ? iterator.next() : null;
	}

	public static <T> T getRandomElement(Collection<T> collection, Random rand) {
		if (collection.isEmpty()) return null;
		int i = rand.nextInt(collection.size());
		Iterator<T> iterator = collection.iterator();
		T result = null;
		for (int j = 0; j <= i && iterator.hasNext(); j++) {
			result = iterator.next();
		}
		return result;
	}
}
