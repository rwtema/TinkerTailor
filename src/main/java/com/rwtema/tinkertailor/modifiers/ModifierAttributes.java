package com.rwtema.tinkertailor.modifiers;

import com.google.common.collect.Multimap;
import com.rwtema.tinkertailor.modifiers.itemmodifier.ModOreModifier;
import com.rwtema.tinkertailor.utils.oremapping.OreIntMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.item.ItemStack;
import tconstruct.library.modifier.ItemModifier;

public class ModifierAttributes extends Modifier {

	public ModifierAttributes(String name, int modifierStep, int maxLevel, OreIntMap map, IAttribute attribute, int priority, double defaultVal, double maxVal) {
		super(name, maxLevel * modifierStep, map.makeItemStackList());
		this.map = map;
		this.attribute = attribute;
		this.priority = priority;
		this.defaultVal = defaultVal;
		this.maxVal = maxVal;
		this.modifierStep = modifierStep;
	}

	public final OreIntMap map;

	protected ModifierAttributes(String name, int maxLevel, OreIntMap map) {
		super(name, maxLevel, map.makeItemStackList());
		this.map = map;
	}

	IAttribute attribute;
	int priority;
	double defaultVal = 0;
	double maxVal;

	@Override
	public void addAttributes(ItemStack item, int slot, int level, Multimap<String, AttributeModifier> map) {
		addAttributeModifier(map, attribute, getModifier(level), priority);
	}

	private double getModifier(int level) {
		return defaultVal + (double) level * (maxVal - defaultVal) / maxLevel;
	}

	@Override
	public ItemModifier createItemModifier() {
		return new ModOreModifier(this, map);
	}
}
