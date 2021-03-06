package com.rwtema.tinkertailor.modifiers;

import com.rwtema.tinkertailor.modifiers.itemmodifier.ModArmorModifier;
import com.rwtema.tinkertailor.nbt.TinkersTailorConstants;
import com.rwtema.tinkertailor.utils.oremapping.ItemValueMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class ModifierPotion extends Modifier {
	public static final ModifierPotion[] modifiers = new ModifierPotion[256];
	public final Potion potion;
	private final ItemValueMap[] items;
	public int duration = 5;
	private TIntObjectHashMap<PotionEffect> effectCache;

	protected ModifierPotion(String name, int maxLevel, Potion potion, ItemValueMap... items) {
		super(name, maxLevel, items);
		this.potion = potion;
		this.items = items;
		modifiers[potion.id] = this;
	}

	@Override
	public void addInfo(List<String> list, EntityPlayer player, ItemStack item, int slot, int level) {
		super.addInfo(list, player, item, slot, level);
	}

	@Override
	public ModArmorModifier createItemModifier() {
		return new ModArmorModifier(this, items);
	}

	@Override
	public boolean doesTick(ItemStack item, int level) {
		return true;
	}

	@Override
	public void onArmorTick(World world, EntityLivingBase entity, ItemStack item, int slot, int level) {
		PotionEffect cachedEffect = getEffect(level);
		if (cachedEffect == null) return;

		int i = 1200 + (int) entity.worldObj.getTotalWorldTime() % 1000;

		if (Potion.potionTypes[potion.id].isReady(i, cachedEffect.getAmplifier())) {
			cachedEffect.performEffect(entity);
		}

	}

	public PotionEffect makePotionEffect(int level) {
		return new PotionEffect(potion.getId(), duration, level - 1, true);
	}

	public ModifierPotion setDuration(int duration) {
		this.duration = duration;
		return this;
	}

	public PotionEffect getEffect(int level) {
		if (modifierStep != 1) {
			int i = level / modifierStep;
			if ((level % modifierStep) < TinkersTailorConstants.RANDOM.nextInt(modifierStep)) i++;
			level = i;
		}

		if (level == 0) return null;

		if (effectCache == null) {
			effectCache = new TIntObjectHashMap<PotionEffect>();
		}

		PotionEffect potionEffect = effectCache.get(level);
		if (potionEffect == null) {
			potionEffect = makePotionEffect(level);
			effectCache.put(level, potionEffect);
		}

		return potionEffect;
	}



}
