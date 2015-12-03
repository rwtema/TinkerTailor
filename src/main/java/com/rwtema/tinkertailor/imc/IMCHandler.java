package com.rwtema.tinkertailor.imc;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.rwtema.tinkertailor.DamageEventHandler;
import com.rwtema.tinkertailor.TinkersTailor;
import com.rwtema.tinkertailor.modifiers.Modifier;
import com.rwtema.tinkertailor.modifiers.ModifierInstance;
import com.rwtema.tinkertailor.modifiers.ModifierRegistry;
import cpw.mods.fml.common.event.FMLInterModComms;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import tconstruct.library.TConstructRegistry;

public class IMCHandler {
	static List<FMLInterModComms.IMCMessage> messageList = new ArrayList<FMLInterModComms.IMCMessage>();

	static {
		IMCNBTLoader.registerNBT("addInherentModifierToMaterial", new IMC() {
			int material_id;
			String modifier;
			int modifier_level = 1;

			@Override
			public void run() throws NBTLoadException, IMCRunException {
				Modifier modifier = ModifierRegistry.modifiers.get(this.modifier);
				if (modifier == null)
					throw new IMCRunException(this.modifier + " is not a valid Modifier");

				if (material_id == -1 || !TConstructRegistry.toolMaterials.containsKey(material_id))
					throw new IMCRunException("Unable to find given tool material");

				ModifierRegistry.bonusModifiers.put(material_id, new ModifierInstance(modifier, modifier_level));
			}
		}, "Make any armor made out of a given material have a given modifier.");

		IMCNBTLoader.registerNBT("overrideDamageResistance", new IMC() {
			int material_id;
			double dR;

			@Override
			public void run() throws NBTLoadException, IMCRunException {
				if (material_id == -1) throw new IMCRunException();
				DamageEventHandler.matDRcache.put(material_id, dR);
			}
		}, "Override the normal DR calculation for a given material.");
	}

	public static void processIMC(ImmutableList<FMLInterModComms.IMCMessage> imcMessages, boolean finalChance) {
		Iterable<FMLInterModComms.IMCMessage> list = finalChance ? Iterables.concat(imcMessages, messageList) : imcMessages;
		for (FMLInterModComms.IMCMessage imcMessage : list) {
			try {
				if (imcMessage.isNBTMessage()) {
					IMCNBTLoader imcnbtLoader = IMCNBTLoader.nbtLoaders.get(imcMessage.key);

					if (imcnbtLoader == null) {
						TinkersTailor.logger.info(imcMessage.key + " is not recognized");
						continue;
					}

					try {
						imcnbtLoader.load(imcMessage.getNBTValue());
					} catch (NBTLoadException e) {
						TinkersTailor.logger.info("Unable to load " + imcMessage.key + ": " + e.getMessage());
					}
				} else if (imcMessage.isStringMessage()) {
					IMCNBTLoader.IMCSimple<String> stringIMCSimple = IMCNBTLoader.stringLoaders.get(imcMessage.key);
					if (stringIMCSimple != null)
						stringIMCSimple.load(imcMessage.getStringValue());
					else
						TinkersTailor.logger.info(imcMessage.key + " is not recognized");
				} else if (imcMessage.isItemStackMessage()) {
					IMCNBTLoader.IMCSimple<ItemStack> itemStackIMCSimple = IMCNBTLoader.itemStackLoaders.get(imcMessage.key);
					if (itemStackIMCSimple != null)
						itemStackIMCSimple.load(imcMessage.getItemStackValue());
					else
						TinkersTailor.logger.info(imcMessage.key + " is not recognized");
				}
			} catch (IMCRunException e) {
				if (finalChance) {
					TinkersTailor.logger.info("Unable to load " + imcMessage.key + ": " + e.getMessage());
				} else {
					messageList.add(imcMessage); // may rely on some not yet executed IMC messages
				}
			}
		}
	}

	public static void init() {

	}
}
