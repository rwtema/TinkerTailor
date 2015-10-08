package com.rwtema.tinkertailor.modifiers;

import com.rwtema.tinkertailor.items.ArmorCore;
import com.rwtema.tinkertailor.nbt.DamageType;
import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;

public class ModifierProtection extends Modifier {
	DamageType damageType;

	public ModifierProtection(DamageType damageType) {
		super(damageType.name, 0);
		this.damageType = damageType;
		this.modifierStep = 16;
		allowedArmorTypes = damageType.allowedArmorTypes();

		maxLevel = damageType.maxLevel * modifierStep;
	}

	@Override
	public float getBonusResistance(EntityLivingBase entity, DamageSource source, float amount, ItemStack item, int slot, int level) {
		if (!damageType.handles(source))
			return 0;

		return getSimpleResistance(level);
	}

	private float getSimpleResistance(int level) {
		float levelFactor = (float) level / maxLevel;
		return 100 - 100 / (1 + levelFactor * 4);
	}

	@Override
	public void addInfo(List<String> list, EntityPlayer player, ItemStack item, int slot, int level) {
		float resistance = (getBonusResistance(player, damageType.damageSource, 100, item, slot, this.level.get(item)));
		int m = (int) (Math.ceil((double) level / modifierStep)) * modifierStep;
		list.add(String.format(damageType.color + "%s%s: %.1f%% (%s/%s)", damageType.getLocalizedName(), getLevelTooltip(level), resistance, level, m) + EnumChatFormatting.GRAY);
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
				r += getBonusResistance(player, damageType.damageSource, 100, armor, i, this.level.get(armor));
			}
		}

		float armor = r;
		if (armor > 80) armor = 80;
		list.add(damageType.color + String.format("%s: %.1f", damageType.getLocalizedName(), armor) + EnumChatFormatting.GRAY);
	}

	@Override
	public ItemStack[] recipe() {
		return new ItemStack[]{damageType.itemStack};
	}

	@Override
	public int getMaxLevel() {
		return maxLevel;
	}
}
