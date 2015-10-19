package com.rwtema.tinkertailor.crafting;

import com.rwtema.tinkertailor.utils.ModCompat;
import mods.natura.common.NContent;

@ModCompat.Initializer(requiredMods = "Natura")
public class NaturaCompat extends ModCompat {

	@Override
	public void init() {
		Ingredients.cloud = NContent.cloud;
		Ingredients.netherBerry = NContent.netherBerryItem;
	}
}
