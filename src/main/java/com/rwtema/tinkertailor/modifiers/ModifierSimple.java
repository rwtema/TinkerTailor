package com.rwtema.tinkertailor.modifiers;

import com.rwtema.tinkertailor.modifiers.itemmodifier.ModArmorModifier;
import com.rwtema.tinkertailor.modifiers.itemmodifier.ModOreModifier;
import com.rwtema.tinkertailor.utils.oremapping.OreIntMap;
import net.minecraft.item.ItemStack;

public class ModifierSimple extends Modifier {
	public final ItemStack[] itemStacks;
	public final OreIntMap map;

	protected ModifierSimple(String name, int maxLevel, ItemStack... itemStacks) {
		super(name, maxLevel, itemStacks);
		this.itemStacks = itemStacks;
		this.map = null;
	}

	protected ModifierSimple(String name, int maxLevel, OreIntMap map) {
		super(name, maxLevel, map.makeItemStackList());
		this.map = map;
		this.itemStacks = map.makeItemStackList();
	}

	@Override
	public ModArmorModifier createItemModifier() {
		if (map != null)
			return new ModOreModifier(this, map);
		else
			return new ModArmorModifier(this, itemStacks);
	}


	public boolean hasEffect(ItemStack itemStack) {
		return level.get(itemStack) > 0;
	}
}
