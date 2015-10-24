package com.rwtema.tinkertailor.utils;

public class RandomHelper {
	private static final long multiplier = 0x5DEECE66DL;
	private static final long addend = 0xBL;
	private static final long mask = (1L << 48) - 1;

	public static long nextRand(int seed) {
		long oldseed = (seed ^ multiplier) & mask;
		return (oldseed * multiplier + addend) & mask;
	}


	public static int nextInt(int seed, int n) {
		return (int) (Math.abs((int)nextRand(seed)) % n);
	}
}
