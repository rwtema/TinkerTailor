package com.rwtema.tinkertailor.imc;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.rwtema.tinkertailor.TinkersTailor;
import com.rwtema.tinkertailor.modifiers.Modifier;
import com.rwtema.tinkertailor.modifiers.ModifierInstance;
import com.rwtema.tinkertailor.modifiers.ModifierRegistry;
import cpw.mods.fml.common.event.FMLInterModComms;
import java.util.ArrayList;
import java.util.List;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.tools.ToolMaterial;

public class IMCHandler {
	static List<FMLInterModComms.IMCMessage> messageList = new ArrayList<FMLInterModComms.IMCMessage>();

	public static void processIMC(ImmutableList<FMLInterModComms.IMCMessage> imcMessages, boolean finalChance) {
		Iterable<FMLInterModComms.IMCMessage> list = finalChance ? Iterables.concat(imcMessages, messageList) : imcMessages;
		for (FMLInterModComms.IMCMessage imcMessage : list) {
			if (imcMessage.isNBTMessage()) {
				IMCNBTLoader imcnbtLoader = IMCNBTLoader.nbtLoaders.get(imcMessage.key);

				if (imcnbtLoader == null) {
					TinkersTailor.logger.info(imcMessage.key + " is not recognized");
					continue;
				}

				try {
					imcnbtLoader.load(imcMessage.getNBTValue());
				} catch (IMCRunException e) {
					if (finalChance) {
						TinkersTailor.logger.info("Unable to load " + imcMessage.key + "\n" + e.getMessage());
					} else {
						messageList.add(imcMessage); // may rely on some not executed IMC messages
					}
				} catch (NBTLoadException e) {
					TinkersTailor.logger.info("Unable to load " + imcMessage.key + "\n" + e.getMessage());
				}
			}
		}
	}

	static {
		IMCNBTLoader.register("addInherentModifierToMaterial", new IMC() {
			String modifier;
			String material;
			int level = 1;

			@Override
			public void run() throws NBTLoadException {
				Modifier modifier = ModifierRegistry.modifiers.get(this.modifier);
				if (modifier == null)
					throw new IMCRunException(this.modifier + " is not a valid Modifier");

				ToolMaterial toolMaterial = TConstructRegistry.toolMaterialStrings.get(material);
				int id = ModifierRegistry.reverseMap.get(toolMaterial);
				if (id == -1) throw new IMCRunException(this.material + " is not a registered tool material");

				ModifierRegistry.bonusModifiers.put(id, new ModifierInstance(modifier, level));
			}
		});
	}

}
