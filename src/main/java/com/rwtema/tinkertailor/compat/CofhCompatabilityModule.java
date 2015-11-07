package com.rwtema.tinkertailor.compat;

import cofh.api.energy.IEnergyContainerItem;
import com.rwtema.tinkertailor.caches.Caches;
import com.rwtema.tinkertailor.items.ArmorCore;
import com.rwtema.tinkertailor.modifiers.Modifier;
import com.rwtema.tinkertailor.modifiers.ModifierRegistry;
import com.rwtema.tinkertailor.modifiers.ModifierSimple;
import com.rwtema.tinkertailor.modifiers.itemmodifier.ModArmorModifier;
import com.rwtema.tinkertailor.nbt.TinkersTailorConstants;
import com.rwtema.tinkertailor.utils.functions.IntFunction;
import com.rwtema.tinkertailor.utils.oremapping.OreIntMap;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import tconstruct.library.modifier.IModifyable;
import tconstruct.tools.TinkerTools;
import tconstruct.util.config.PHConstruct;

@ModCompatibilityModule.InitialiseMe(requiredMods = "CoFHAPI|energy")
public class CofhCompatabilityModule extends ModCompatibilityModule {
	@Override
	public void onCreated() {
		Caches.getMaxEnergy = new IntFunction<ItemStack>() {
			@Override
			public int calc(@Nonnull ItemStack input) {
				Item item = input.getItem();
				return item instanceof IEnergyContainerItem ? ((IEnergyContainerItem) item).getMaxEnergyStored(input) : 0;
			}
		};

		Caches.getCurEnergy = new IntFunction<ItemStack>() {
			@Override
			public int calc(@Nonnull ItemStack input) {
				Item item = input.getItem();
				return item instanceof IEnergyContainerItem ? ((IEnergyContainerItem) item).getEnergyStored(input) : 0;
			}
		};
	}

	@Override
	public void initEnd() {
		ModifierRegistry.energy = new ModifierEnergy();
		ModifierRegistry.registerModifier(ModifierRegistry.energy);
	}

	public static class ModifierEnergy extends ModifierSimple {
		{
			priority = 100;
		}

		public ModifierEnergy() {
			super("flux", 1, OreIntMap.newMap(TinkerTools.modFlux.batteries));
		}

		@Override
		public void addInfo(List<String> list, EntityPlayer player, ItemStack item, int slot, int level) {
			ArmorCore armor = ArmorCore.armors[slot];
			list.add(getColorString() + getLocalizedName() + ": (" + armor.getEnergyStored(item) + " / " + armor.getMaxEnergyStored(item) + ")" + resetColorString());
		}

		@Override
		public ModArmorModifier createItemModifier() {
			return new ModEnergy(this);
		}

		@Override
		public boolean allowRandom() {
			return false;
		}

		@Override
		public int reduceArmorDamage(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int originalDamage, int slot, int level) {
			damage -= ArmorCore.armors[slot].extractEnergy(stack, damage * 100, true) / 100;
			return damage;
		}
	}

	public static class ModEnergy extends ModArmorModifier {

		public ModEnergy(Modifier modifier) {
			super(modifier);
		}

		@Override
		public boolean matches(ItemStack[] recipe, ItemStack input) {
			NBTTagCompound tags = input.getTagCompound().getCompoundTag(TinkersTailorConstants.NBT_MAINTAG);

			ItemStack foundBattery = getBattery(recipe);

			// no battery present
			if (foundBattery == null)
				return false;

			int maxEnergyStored = ((IEnergyContainerItem) foundBattery.getItem()).getMaxEnergyStored(foundBattery);

			if (maxEnergyStored <= 0) return false;

			if (PHConstruct.balancedFluxModifier && Caches.maxDurability.get(input.copy()) < maxEnergyStored / 1000)
				return false;

			int curEnergyMax = tags.getInteger("EnergyMax");

			if (curEnergyMax != 0)
				return maxEnergyStored > curEnergyMax;

			return (tags.getInteger("Modifiers") - TinkerTools.modFlux.modifiersRequired) >= 0;
		}

		private ItemStack getBattery(ItemStack[] recipe) {
			ItemStack foundBattery = null;

			for (ItemStack stack : recipe)
				for (ItemStack battery : TinkerTools.modFlux.batteries) {
					if (stack == null || stack.getItem() != battery.getItem() || !(stack.getItem() instanceof IEnergyContainerItem))
						continue;

					if (foundBattery != null) {
						return null;
					}

					foundBattery = stack;
				}
			return foundBattery;
		}

		@Override
		public void modify(ItemStack[] recipe, ItemStack input) {
			NBTTagCompound mainTags = input.getTagCompound();
			NBTTagCompound tags = mainTags.getCompoundTag(TinkersTailorConstants.NBT_MAINTAG);

			if (tags.getInteger("EnergyMax") == 0) {
				int modifiers = tags.getInteger(TinkersTailorConstants.NBT_MAINTAG_MODIFIERS);
				modifiers -= TinkerTools.modFlux.modifiersRequired;
				tags.setInteger(TinkersTailorConstants.NBT_MAINTAG_MODIFIERS, modifiers);
			}

			ItemStack inputBattery = getBattery(recipe);

			if (inputBattery == null) throw new IllegalStateException("Something has gone horribly wrong.");


			IEnergyContainerItem energyContainer = (IEnergyContainerItem) inputBattery.getItem();

			int charge = energyContainer.getEnergyStored(inputBattery);

			// add already present charge in the tool
			if (tags.hasKey("Energy"))
				charge += tags.getInteger("Energy");
			int maxCharge = energyContainer.getMaxEnergyStored(inputBattery);


			ItemStack subject42 = inputBattery.copy();
			int progress = 0, change = 1; // prevent endless loops with creative battery, blah
			// fill the battery full
			while (progress < maxCharge && change > 0) {
				change = energyContainer.receiveEnergy(subject42, 100000, false);
				progress += change;
			}
			// get the maximum extraction rate
			int maxExtract = energyContainer.extractEnergy(subject42, Integer.MAX_VALUE, true);

			subject42 = inputBattery.copy();

			// completely empty the battery
			progress = 0;
			change = 1;
			while (progress < maxCharge && change > 0) {
				change = energyContainer.extractEnergy(subject42, 100000, false);
				progress += change;
			}
			int maxReceive = energyContainer.receiveEnergy(subject42, Integer.MAX_VALUE, true);

			charge = Math.min(charge, maxCharge);
			tags.setInteger(key, 1);

			mainTags.setInteger("Energy", charge);
			mainTags.setInteger("EnergyMax", maxCharge);
			mainTags.setInteger("EnergyExtractionRate", maxExtract);
			mainTags.setInteger("EnergyReceiveRate", maxReceive);

		}

		public void addMatchingEffect(ItemStack tool) {
		}

		@Override
		public boolean validType(IModifyable input) {
			return input.getModifyType().equals(TinkersTailorConstants.MODIFY_TYPE);
		}
	}


}
