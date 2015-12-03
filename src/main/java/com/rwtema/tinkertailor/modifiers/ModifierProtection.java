package com.rwtema.tinkertailor.modifiers;

import com.rwtema.tinkertailor.caches.Caches;
import com.rwtema.tinkertailor.items.ArmorCore;
import com.rwtema.tinkertailor.nbt.ProtectionTypes;
import com.rwtema.tinkertailor.utils.Lang;
import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;

public class ModifierProtection extends Modifier {
	ProtectionTypes protectionTypes;

	public ModifierProtection(ProtectionTypes protectionTypes) {
		super(protectionTypes.name, 0, protectionTypes.getOreValues());
		this.protectionTypes = protectionTypes;
		this.modifierStep = 16;
		allowedArmorTypes = protectionTypes.allowedArmorTypes();

		maxLevel = protectionTypes.maxLevel * modifierStep;

		protectionTypes.modifier = this;

	}

	@Override
	public boolean givesBonusResistance() {
		return true;
	}

	@Override
	public float getBonusResistance(EntityLivingBase entity, DamageSource source, float amount, ItemStack item, int slot, int level) {
		if (!protectionTypes.handles(source))
			return 0;

		return getSimpleResistance(level);
	}

	private float getSimpleResistance(int level) {
		float levelFactor = (float) level / (protectionTypes.maxLevel * modifierStep);
		return levelFactor * 80;
	}

	@Override
	public void addInfo(List<String> list, EntityPlayer player, ItemStack item, int slot, int level) {
		float resistance = (getBonusResistance(player, protectionTypes.damageSource, 100, item, slot, this.level.get(item)));
		int m = (int) (Math.ceil((double) level / modifierStep)) * modifierStep;
		list.add(String.format(getColorString() + "%s%s: +%.1f%% (%s/%s)", getLocalizedName(), getLevelTooltip(level), resistance, level, m) + EnumChatFormatting.GRAY);
	}


	@Override
	public void addArmorSetInfo(List<String> list, EntityPlayer player) {
		super.addArmorSetInfo(list, player);

		float r = 0;
		for (int i = 0; i < player.inventory.armorInventory.length; i++) {
			ItemStack armor = player.inventory.armorInventory[i];
			if (armor == null) continue;
			Item itm = armor.getItem();

			if (itm instanceof ArmorCore) {
				for (ModifierInstance modifierInstance : Caches.modifiers.get(armor)) {
					r += modifierInstance.modifier.getBonusResistance(player, protectionTypes.damageSource, 100, armor, i, modifierInstance.level);
				}
			}
		}

		float armor = r;
		if (armor > 80) armor = 80;
		list.add(getColorString() + String.format("%s: %.1f", getLocalizedName(), armor) + EnumChatFormatting.GRAY);
	}

	@Override
	public String[] getDocLines() {
		float res = getSimpleResistance(modifierStep);
		String translate = Lang.translate("Protection per Level");
		if (res % 1 == 0)
			return new String[]{String.format(translate + " - %s%%", (int) res)};
		return new String[]{String.format(translate + " - %.2f%%", res)};
	}

	@Override
	public int getMaxLevel() {
		return maxLevel;
	}
}
