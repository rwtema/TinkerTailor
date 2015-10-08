package com.rwtema.tinkertailor.modifiers;

import com.rwtema.tinkertailor.modifiers.itemmodifier.ModArmorModifier;
import com.rwtema.tinkertailor.modifiers.itemmodifier.ModCreativeArmorModifier;
import com.rwtema.tinkertailor.modifiers.itemmodifier.ModInducedExtraModifier;
import com.rwtema.tinkertailor.nbt.DamageType;
import java.util.HashMap;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import tconstruct.library.crafting.ModifyBuilder;
import tconstruct.tools.TinkerTools;

public class ModifierRegistry {
	public static HashMap<String, Modifier> modifiers = new HashMap<String, Modifier>();

	public final static class ArmorModifierInstance {
		public final Modifier modifier;
		public final int level;

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			ArmorModifierInstance that = (ArmorModifierInstance) o;

			return modifier.equals(that.modifier);
		}

		@Override
		public int hashCode() {
			return modifier.hashCode();
		}

		public ArmorModifierInstance(Modifier modifier, int level) {
			this.modifier = modifier;
			this.level = level;
		}
	}

	public static void init() {
		for (DamageType damageType : DamageType.values()) {
			ModifierProtection modifier = (new ModifierProtection(damageType));
			ModArmorModifier modArmorModifier = new ModArmorModifier(modifier);
			ModifyBuilder.registerModifier(modArmorModifier);
		}


		registerModifier(new ModifierPotion("nightvision", 1, Potion.nightVision, new ItemStack(Items.golden_carrot)).setDuration(205).setAllowedArmorTypes(Modifier.ARMORTYPE_HAT_ONLY));
		registerModifier(new ModifierPotion("jump", 3, Potion.jump, new ItemStack(Items.snowball)).setAllowedArmorTypes(Modifier.ARMORTYPE_SHOES_ONLY));

		ModifyBuilder.registerModifier(new ModCreativeArmorModifier(new ItemStack[]{new ItemStack(TinkerTools.creativeModifier)}));

		ModifyBuilder.registerModifier(new ModInducedExtraModifier());
	}

	public static void registerModifier(Modifier mod) {
		ModifyBuilder.registerModifier(new ModArmorModifier(mod));
	}
}
