package com.rwtema.tinkertailor.coremod;

import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClassTransformerHandler implements IClassTransformer {
	public static Logger logger = LogManager.getLogger("TinkerTailorCoreMod");
	IClassTransformer[] transformers = new IClassTransformer[]{
			new AltPotionTransformer()
	};

	@Override
	public byte[] transform(String s, String s2, byte[] bytes) {

		StringBuilder builder = null;
		for (IClassTransformer transformer : transformers) {
			byte[] b = bytes;
			bytes = transformer.transform(s, s2, bytes);
			if (b != bytes) {
				if (builder == null) builder = new StringBuilder("Transformed: ").append(s);
				builder.append(transformer.toString());
			}
		}
		if (builder != null) {
			logger.info(builder.toString());
		}
		return bytes;
	}
}
