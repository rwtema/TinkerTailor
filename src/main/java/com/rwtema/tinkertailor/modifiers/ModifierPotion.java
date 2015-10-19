package com.rwtema.tinkertailor.modifiers;

import com.rwtema.tinkertailor.modifiers.itemmodifier.ModArmorModifier;
import com.rwtema.tinkertailor.modifiers.itemmodifier.ModOreModifier;
import com.rwtema.tinkertailor.utils.oremapping.OreIntMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class ModifierPotion extends Modifier {
	public final Potion potion;
	private final OreIntMap map;
	private final ItemStack[] items;
	public int duration = 5;

	protected ModifierPotion(String name, int maxLevel, Potion potion, ItemStack... items) {
		super(name, maxLevel, items);
		this.potion = potion;
		this.items = items;
		this.map = null;
		modifiers[potion.id] = this;
	}

	protected ModifierPotion(String name, int maxLevel, Potion potion, OreIntMap map) {
		super(name, maxLevel, map.makeItemStackList());
		this.potion = potion;
		this.map = map;
		this.items = null;
		modifiers[potion.id] = this;
	}

	@Override
	public void addInfo(List<String> list, EntityPlayer player, ItemStack item, int slot, int level) {
		super.addInfo(list, player, item, slot, level);
	}

	@Override
	public ModArmorModifier createItemModifier() {
		if (map != null)
			return new ModOreModifier(this, map);
		else
			return new ModArmorModifier(this, items);
	}

	@Override
	public boolean doesTick(ItemStack item, int level) {
		return true;
	}

	@Override
	public void onArmorTick(EntityLivingBase entity, ItemStack item, int slot, int level) {
//		entity.addPotionEffect(makePotionEffect(level));
	}

	public PotionEffect makePotionEffect(int level) {
		return new PotionEffect(potion.getId(), duration, level - 1, true);
	}


	public ModifierPotion setDuration(int duration) {
		this.duration = duration;
		return this;
	}


	public static final ModifierPotion[] modifiers = new ModifierPotion[256];

	private TIntObjectHashMap<PotionEffect> effectCache;

	public PotionEffect getCachedEffect(int level) {
		if (modifierStep != 1)
			level = level / modifierStep + 1;

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
