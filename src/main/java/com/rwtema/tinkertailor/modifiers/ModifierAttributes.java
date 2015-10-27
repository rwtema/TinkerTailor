package com.rwtema.tinkertailor.modifiers;

import com.google.common.collect.Multimap;
import com.rwtema.tinkertailor.utils.oremapping.ItemValueMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.item.ItemStack;

public class ModifierAttributes extends Modifier {

	public final ItemValueMap[] map;
	IAttribute attribute;
	int priority;
	double defaultVal = 0;
	double maxVal;

	public ModifierAttributes(String name, int modifierStep, int maxLevel, IAttribute attribute, int priority, double defaultVal, double maxVal, ItemValueMap... map) {
		super(name, maxLevel * modifierStep, map);
		this.map = map;
		this.attribute = attribute;
		this.priority = priority;
		this.defaultVal = defaultVal;
		this.maxVal = maxVal;
		this.modifierStep = modifierStep;
	}

	@Override
	public void addAttributes(ItemStack item, int slot, int level, Multimap<String, AttributeModifier> map) {
		addAttributeModifier(map, attribute, getModifier(level), priority);
	}

	private double getModifier(int level) {
		return defaultVal + (double) level * (maxVal - defaultVal) / maxLevel;
	}

}
