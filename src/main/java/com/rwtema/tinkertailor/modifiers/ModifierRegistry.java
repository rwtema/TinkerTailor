package com.rwtema.tinkertailor.modifiers;

import com.rwtema.tinkertailor.crafting.Ingredients;
import com.rwtema.tinkertailor.modifiers.itemmodifier.ModArmorModifier;
import com.rwtema.tinkertailor.modifiers.itemmodifier.ModArmorRepair;
import com.rwtema.tinkertailor.modifiers.itemmodifier.ModCreativeArmorModifier;
import com.rwtema.tinkertailor.modifiers.itemmodifier.ModInducedExtraModifier;
import com.rwtema.tinkertailor.nbt.ProtectionTypes;
import com.rwtema.tinkertailor.utils.ItemHelper;
import com.rwtema.tinkertailor.utils.oremapping.OreIntMap;
import cpw.mods.fml.common.registry.GameRegistry;
import java.util.HashMap;
import java.util.List;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import tconstruct.armor.TinkerArmor;
import tconstruct.library.accessory.IHealthAccessory;
import tconstruct.library.crafting.ModifyBuilder;
import tconstruct.tools.TinkerTools;
import tconstruct.world.TinkerWorld;

public class ModifierRegistry {
	public static HashMap<String, Modifier> modifiers = new HashMap<String, Modifier>();
	public static ModifierSimple invisibility;
	public static Modifier gogglesRevealing;
	public static Modifier visBoost;

	public static void init() {
		ModifyBuilder.registerModifier(new ModArmorRepair());

		for (ProtectionTypes protectionTypes : ProtectionTypes.values()) {
			registerModifier(new ModifierProtection(protectionTypes));
		}

		registerModifier(new ModifierPotion("nightvision", 1, Potion.nightVision, OreIntMap.newMap(Items.golden_carrot)).setDuration(205).setAllowedArmorTypes(Modifier.ARMORTYPE_HAT_ONLY));
		registerModifier(new ModifierPotion("jump", 3, Potion.jump, OreIntMap.newMap(TinkerWorld.slimePad)).setAllowedArmorTypes(Modifier.ARMORTYPE_SHOES_ONLY));

		registerModifier(new ModifierAttributes("attack", 40, 4, SharedMonsterAttributes.attackDamage, 0, 0, 8, OreIntMap.newMap(Blocks.piston), OreIntMap.newMap(Blocks.quartz_block, "blockQuartz")).setAllowedArmorTypes(Modifier.ARMORTYPE_SHIRT_ONLY));
		registerModifier(new ModifierAttributes("knockback", 20, 4, SharedMonsterAttributes.knockbackResistance, 0, 0, 0.8, OreIntMap.newMap(Blocks.obsidian, "blockObsidian")));
		registerModifier(new ModifierAttributes("haste", 50, 4, SharedMonsterAttributes.movementSpeed, 1, 0, 2, OreIntMap.newMap("dustGlowstone", 1, "glowstone", 4)).setAllowedArmorTypes(Modifier.ARMORTYPE_SHOES_ONLY));
		registerModifier(new ModifierAttributes("health", 2, 8, SharedMonsterAttributes.maxHealth, 0, 0, 4, OreIntMap.newMap(TinkerArmor.heartCanister)) {
			@Override
			public ModArmorModifier createItemModifier() {
				return new ModArmorModifier(this, map) {
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

		registerModifier(new ModifierPotion("digspeed", 3, Potion.digSpeed, ItemHelper.makeOreIntArray(OreIntMap.newMap("dustGlowstone"), Ingredients.netherBerry)).setAllowedArmorTypes(Modifier.ARMORTYPE_SHIRT_ONLY));

		registerModifier(new ModifierPotion("waterbreathing", 1, Potion.waterBreathing, ItemHelper.makeOreIntArray(Items.reeds, Ingredients.cloud)).setAllowedArmorTypes(Modifier.ARMORTYPE_HAT_ONLY));

		registerModifier(invisibility = new ModifierSimple("invisibility", 1,
				OreIntMap.newMap(
						new ItemStack(Items.potionitem, 1, 8206), 1,
						new ItemStack(Items.potionitem, 1, 8270), 1,
						new ItemStack(Items.potionitem, 1, 16389), 1,
						new ItemStack(Items.potionitem, 1, 16462), 1
				)) {
			@Override
			public ModArmorModifier createItemModifier() {
				return new ModArmorModifier(this, recipe) {
					{
						this.useModifiers = false;
					}

					@Override
					public int getValue(ItemStack itemStack) {
						if (itemStack.getItem() != Items.potionitem)
							return 0;
						List effects = Items.potionitem.getEffects(itemStack);
						if (effects == null || effects.isEmpty()) return 0;

						for (Object o : effects) {
							PotionEffect effect = (PotionEffect) o;
							if (effect.getPotionID() == Potion.invisibility.getId()) {
								return 1;
							}
						}
						return 0;
					}
				};
			}
		});

		Item goggles = GameRegistry.findItem("Thaumcraft", "ItemGoggles");
		gogglesRevealing = new ModifierSimple("gogglesRevealing", 1, ItemHelper.makeOreIntArray(goggles)) {
			@Override
			public ModArmorModifier createItemModifier() {
				return new ModArmorModifier(this, recipe) {
					{
						this.useModifiers = false;
					}
				};
			}
		}.setRequiredMods("Thaumcraft");
		registerModifier(gogglesRevealing);


		Item thaumcraftResource = GameRegistry.findItem("Thaumcraft", "ItemResource");
		visBoost = new ModifierSimple("visBoost", 150, ItemHelper.makeOreIntArray(thaumcraftResource, 7 /*Enchanted Fabric*/)) {
			{
				modifierStep = maxLevel;
			}

			@Override
			public void addInfo(List<String> list, EntityPlayer player, ItemStack item, int slot, int level) {
				super.addInfo(list, player, item, slot, level);
			}

			@Override
			public void addArmorSetInfo(List<String> list, EntityPlayer player) {
				super.addArmorSetInfo(list, player);
			}
		}.setRequiredMods("Thaumcraft");
		registerModifier(visBoost);

		ModifyBuilder.registerModifier(new ModCreativeArmorModifier(new ItemStack[]{new ItemStack(TinkerTools.creativeModifier)}));
		ModifyBuilder.registerModifier(new ModInducedExtraModifier());
	}

	public static void registerModifier(Modifier mod) {
		ModifierRegistry.modifiers.put(mod.name, mod);
		mod.itemModifier = mod.createItemModifier();
		ModifyBuilder.registerModifier(mod.itemModifier);
	}
}
