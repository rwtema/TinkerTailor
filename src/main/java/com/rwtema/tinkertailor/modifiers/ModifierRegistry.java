package com.rwtema.tinkertailor.modifiers;

import com.rwtema.tinkertailor.modifiers.itemmodifier.ModCreativeArmorModifier;
import com.rwtema.tinkertailor.modifiers.itemmodifier.ModInducedExtraModifier;
import com.rwtema.tinkertailor.modifiers.itemmodifier.ModOreModifier;
import com.rwtema.tinkertailor.nbt.DamageType;
import com.rwtema.tinkertailor.utils.oremapping.OreIntMap;
import java.util.HashMap;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import tconstruct.armor.TinkerArmor;
import tconstruct.library.accessory.IHealthAccessory;
import tconstruct.library.crafting.ModifyBuilder;
import tconstruct.library.modifier.ItemModifier;
import tconstruct.tools.TinkerTools;

public class ModifierRegistry {
	public static HashMap<String, Modifier> modifiers = new HashMap<String, Modifier>();

	public static void init() {
		for (DamageType damageType : DamageType.values()) {
			registerModifier(new ModifierProtection(damageType));
		}

		registerModifier(new ModifierPotion("nightvision", 1, Potion.nightVision, new ItemStack(Items.golden_carrot)).setDuration(205).setAllowedArmorTypes(Modifier.ARMORTYPE_HAT_ONLY));
		registerModifier(new ModifierPotion("jump", 3, Potion.jump, new ItemStack(Items.snowball)).setAllowedArmorTypes(Modifier.ARMORTYPE_SHOES_ONLY));

		registerModifier(new ModifierAttributes("haste", 50, 3, OreIntMap.newMap("dustGlowstone", 1, "glowstone", 4), SharedMonsterAttributes.movementSpeed, 1, 1, 4).setAllowedArmorTypes(Modifier.ARMORTYPE_SHOES_ONLY));
		registerModifier(new ModifierAttributes("health", 2, 2, OreIntMap.newMap(TinkerArmor.heartCanister), SharedMonsterAttributes.maxHealth, 0, 0, 2) {
			@Override
			public ItemModifier createItemModifier() {
				return new ModOreModifier(this, map) {
					@Override
					public int getValue(ItemStack itemStack) {
						Item item = itemStack.getItem();
						if (!(item instanceof IHealthAccessory))
							return 0;

						ItemStack copy = itemStack.copy();
						copy.stackSize = 1;
						IHealthAccessory healthAccessory = (IHealthAccessory) item;

						if (!(healthAccessory.canEquipAccessory(copy, 4) || healthAccessory.canEquipAccessory(copy, 5) || healthAccessory.canEquipAccessory(copy, 6)))
							return 0;


						return healthAccessory.getHealthBoost(copy);
					}
				};
			}
		});

		ModifyBuilder.registerModifier(new ModCreativeArmorModifier(new ItemStack[]{new ItemStack(TinkerTools.creativeModifier)}));
		ModifyBuilder.registerModifier(new ModInducedExtraModifier());
	}

	public static void registerModifier(Modifier mod) {
		mod.register();
		mod.itemModifier = mod.createItemModifier();
		ModifyBuilder.registerModifier(mod.itemModifier);
	}
}
