package com.rwtema.tinkertailor.utils;

import com.rwtema.tinkertailor.nbt.TinkersTailorConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DebugHelper {
	public static Logger logger = LogManager.getLogger(TinkersTailorConstants.MOD_ID);
	static long timer;

	public static void resetTimer() {
		timer = System.nanoTime();
	}

	public static void printTimer(String t) {
		logger.info("time:" + t + " - " + (System.nanoTime() - timer) / 1000000D);
		timer = System.nanoTime();
	}
}
