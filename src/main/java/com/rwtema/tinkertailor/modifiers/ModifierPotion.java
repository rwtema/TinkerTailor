package com.rwtema.tinkertailor.modifiers;

import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class ModifierPotion extends Modifier {
	public final Potion potion;
	private final ItemStack[] items;
	public int duration = 5;

	protected ModifierPotion(String name, int maxLevel, Potion potion, ItemStack... items) {
		super(name, maxLevel);
		this.potion = potion;
		this.items = items;
	}

	@Override
	public void addInfo(List<String> list, EntityPlayer player, ItemStack item, int slot, int level) {
		super.addInfo(list, player, item, slot, level);
	}

	@Override
	public ItemStack[] recipe() {
		return items;
	}


	@Override
	public boolean doesTick(ItemStack item, int level) {
		return true;
	}

	@Override
	public void onArmorTick(EntityLivingBase entity, ItemStack item, int slot, int level) {
		entity.addPotionEffect(makePotionEffect(level));
	}

	private PotionEffect makePotionEffect(int level) {
		return new PotionEffect(potion.getId(), duration, level - 1);
	}

	public ModifierPotion setDuration(int duration) {
		this.duration = duration;
		return this;
	}
}
