package com.rwtema.tinkertailor.modifiers;

import com.google.common.collect.Multimap;
import com.rwtema.tinkertailor.modifiers.itemmodifier.ModArmorModifier;
import com.rwtema.tinkertailor.modifiers.itemmodifier.ModOreModifier;
import com.rwtema.tinkertailor.utils.oremapping.OreIntMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.item.ItemStack;

public class ModifierAttributes extends Modifier {

	public final OreIntMap map;
	IAttribute attribute;
	int priority;
	double defaultVal = 0;
	double maxVal;
	public ModifierAttributes(String name, int modifierStep, int maxLevel, OreIntMap map, IAttribute attribute, int priority, double defaultVal, double maxVal) {
		super(name, maxLevel * modifierStep, map.makeItemStackList());
		this.map = map;
		this.attribute = attribute;
		this.priority = priority;
		this.defaultVal = defaultVal;
		this.maxVal = maxVal;
		this.modifierStep = modifierStep;
	}
	public ModifierAttributes(String name, int modifierStep, int maxLevel, ItemStack[] recipe, IAttribute attribute, int priority, double defaultVal, double maxVal) {
		super(name, maxLevel * modifierStep, recipe);
		this.map = null;
		this.attribute = attribute;
		this.priority = priority;
		this.defaultVal = defaultVal;
		this.maxVal = maxVal;
		this.modifierStep = modifierStep;
	}
	protected ModifierAttributes(String name, int maxLevel, OreIntMap map) {
		super(name, maxLevel, map.makeItemStackList());
		this.map = map;
	}

	@Override
	public void addAttributes(ItemStack item, int slot, int level, Multimap<String, AttributeModifier> map) {
		addAttributeModifier(map, attribute, getModifier(level), priority);
	}

	private double getModifier(int level) {
		return defaultVal + (double) level * (maxVal - defaultVal) / maxLevel;
	}

	@Override
	public ModArmorModifier createItemModifier() {
		if (map != null)
			return new ModOreModifier(this, map);
		else
			return new ModArmorModifier(this, itemStacks);
	}
}
