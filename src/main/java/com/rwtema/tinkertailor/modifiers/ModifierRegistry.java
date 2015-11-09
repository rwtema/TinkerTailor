package com.rwtema.tinkertailor.modifiers;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.rwtema.tinkertailor.crafting.Ingredients;
import com.rwtema.tinkertailor.modifiers.itemmodifier.ModArmorModifier;
import com.rwtema.tinkertailor.modifiers.itemmodifier.ModArmorRepair;
import com.rwtema.tinkertailor.modifiers.itemmodifier.ModCreativeArmorModifier;
import com.rwtema.tinkertailor.modifiers.itemmodifier.ModInducedExtraModifier;
import com.rwtema.tinkertailor.nbt.ProtectionTypes;
import com.rwtema.tinkertailor.nbt.TinkersTailorConstants;
import com.rwtema.tinkertailor.utils.ItemHelper;
import com.rwtema.tinkertailor.utils.oremapping.ItemValueMap;
import com.rwtema.tinkertailor.utils.oremapping.OreIntMap;
import cpw.mods.fml.common.registry.GameRegistry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import tconstruct.armor.TinkerArmor;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.accessory.IHealthAccessory;
import tconstruct.library.crafting.ModifyBuilder;
import tconstruct.library.tools.ToolMaterial;
import tconstruct.tools.TinkerTools;
import tconstruct.world.TinkerWorld;

public class ModifierRegistry {
	public static HashMap<String, Modifier> modifiers = new HashMap<String, Modifier>();
	public static ModifierSimple invisibility;
	public static Modifier gogglesRevealing;
	public static Modifier visBoost;
	public static Modifier energy;
	public static Modifier prickly;
	public static Modifier wither;

	public static void init() {
		ModifyBuilder.registerModifier(new ModArmorRepair());

		for (ProtectionTypes protectionTypes : ProtectionTypes.values()) {
			registerModifier(protectionTypes.createModifier());
		}

		registerModifier(new ModifierPotion("nightvision", 1, Potion.nightVision, ItemHelper.makeOreIntArray(Items.golden_carrot, Ingredients.netherBerry, Ingredients.netherBerryMetaNightvision)).setDuration(205).setAllowedArmorTypes(Modifier.ARMORTYPE_HAT_ONLY));
		registerModifier(new ModifierPotion("jump", 3, Potion.jump, ItemHelper.makeOreIntArray(TinkerWorld.slimePad, Ingredients.netherBerry, Ingredients.netherBerryMetaJump)).setAllowedArmorTypes(Modifier.ARMORTYPE_SHOES_ONLY).setNegativeMaloderous());

		registerModifier(new ModifierAttributes("attack", 40, 4, SharedMonsterAttributes.attackDamage, 0, 0, 8, OreIntMap.newMap(Blocks.piston), OreIntMap.newMap(Blocks.quartz_block, "blockQuartz")).setAllowedArmorTypes(Modifier.ARMORTYPE_SHIRT_ONLY).setNegativeMaloderous());
		registerModifier(new ModifierAttributes("knockback", 20, 4, SharedMonsterAttributes.knockbackResistance, 0, 0, 0.8, OreIntMap.newMap(Blocks.obsidian, "blockObsidian")));
		registerModifier(new ModifierAttributes("haste", 50, 4, SharedMonsterAttributes.movementSpeed, 1, 0, 2, OreIntMap.newMap("dustGlowstone", 1, "glowstone", 4)).setAllowedArmorTypes(Modifier.ARMORTYPE_SHOES_ONLY).setNegativeMaloderous());
		registerModifier(new ModifierAttributes("health", 2, 8, SharedMonsterAttributes.maxHealth, 0, 0, 4, ItemHelper.makeOreIntArray(new ItemValueMap() {
			@Override
			public ItemValueMap put(Object s, int value) {
				throw new UnsupportedOperationException();
			}

			@Override
			public int get(ItemStack itemStack) {
				if (itemStack == null) return 0;
				Item item = itemStack.getItem();
				if (!(item instanceof IHealthAccessory))
					return 0;

				ItemStack copy = itemStack.copy();
				copy.stackSize = 1;
				IHealthAccessory healthAccessory = (IHealthAccessory) item;

				if (!healthAccessory.canEquipAccessory(copy, 4) && !healthAccessory.canEquipAccessory(copy, 5) && !healthAccessory.canEquipAccessory(copy, 6))
					return 0;

				return healthAccessory.getHealthBoost(copy);
			}

			@Override
			public void addItemsToList(List<ItemStack> items) {
				items.add(new ItemStack(TinkerArmor.heartCanister, 1, 2));
				items.add(new ItemStack(TinkerArmor.heartCanister, 1, 4));
				items.add(new ItemStack(TinkerArmor.heartCanister, 1, 6));
			}

			@Override
			public ItemStack makeItemStack() {
				return new ItemStack(TinkerArmor.heartCanister, 1, 2);
			}
		})).setNegativeMaloderous());

		registerModifier(new ModifierPotion("digspeed", 3, Potion.digSpeed, ItemHelper.makeOreIntArray("dustGlowstone", "dustRedstone")).setAllowedArmorTypes(Modifier.ARMORTYPE_SHIRT_ONLY));

		registerModifier(new ModifierPotion("waterbreathing", 50, Potion.waterBreathing, ItemHelper.makeOreIntArray(Items.reeds, Ingredients.cloud)).setAllowedArmorTypes(Modifier.ARMORTYPE_HAT_ONLY).setModifierStep(50));

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
		}.setRequiredMods("Thaumcraft");
		registerModifier(visBoost);


		registerModifier(new ModifierPotion("poison", 4, Potion.poison).setMaloderous());
		registerModifier(wither = new ModifierPotion("witherDamage", 4, Potion.wither).setMaloderous());
		registerModifier(new ModifierPotion("blindness", 1, Potion.blindness).setAllowedArmorTypes(Modifier.ARMORTYPE_HAT_ONLY).setMaloderous());
		registerModifier(new ModifierPotion("confusion", 1, Potion.confusion).setDuration(120).setMaloderous());
		registerModifier(new ModifierPotion("slowdig", 4, Potion.digSlowdown).setMaloderous());

		prickly = new ModifierSimple("prickly", 2) {
			@Override
			public boolean doesTick(ItemStack item, int level) {
				return true;
			}

			@Override
			public void onArmorTick(World world, EntityLivingBase entity, ItemStack item, int slot, int level) {
				if (!world.isRemote)
					if (entity.onGround && (entity.motionX != 0 || entity.motionZ != 0)) {
						if (TinkersTailorConstants.RANDOM.nextInt(5) == 0) {
							entity.attackEntityFrom(DamageSource.cactus, 1.0F);
						}
					}
			}
		}.setMaloderous();

		registerModifier(prickly);
		bonusModifiers.put(TinkerTools.MaterialID.Cactus, new ModifierInstance(prickly, prickly.modifierStep));

		registerModifier(new ModifierDurability("durability", 4, 25, 2000, OreIntMap.newMap(
				"gemEmerald", 1,
				"blockEmerald", 9
		)));


		ModifyBuilder.registerModifier(new ModCreativeArmorModifier(new ItemStack[]{new ItemStack(TinkerTools.creativeModifier)}));
		ModifyBuilder.registerModifier(new ModInducedExtraModifier());
	}

	public static void registerModifier(Modifier mod) {
		if (ModifierRegistry.modifiers.containsKey(mod.name)) throw new RuntimeException("Duplicate keys: " + mod.name);
		ModifierRegistry.modifiers.put(mod.name, mod);
		mod.itemModifier = mod.createItemModifier();
		ModifyBuilder.registerModifier(mod.itemModifier);

		if (mod.allowRandom()) {
			if (mod.maloderous == Modifier.MALODEROUS_NONE || mod.maloderous == Modifier.MALODEROUS_WHENNEGATIVE)
				plusModifiers.add(mod);
			if (mod.maloderous == Modifier.MALODEROUS_ALWAYS || mod.maloderous == Modifier.MALODEROUS_WHENNEGATIVE)
				negModifiers.add(mod);
		}
	}

	public static List<Modifier> plusModifiers = new ArrayList<Modifier>();
	public static List<Modifier> negModifiers = new ArrayList<Modifier>();

	public static Multimap<Integer, ModifierInstance> bonusModifiers = HashMultimap.create();

	public static Map<ToolMaterial, Integer> reverseMap = new HashMap<ToolMaterial, Integer>() {

		@Override
		public Integer get(Object key) {
			if(key == null) return -1;
			Integer integer = super.get(key);
			if (integer == null) {
				for (Map.Entry<Integer, ToolMaterial> entry : TConstructRegistry.toolMaterials.entrySet()) {
					put(entry.getValue(), entry.getKey());
				}

				integer = super.get(key);

				if (integer == null) return -1;
			}

			return integer;
		}
	};
}
