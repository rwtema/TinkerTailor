package com.rwtema.tinkertailor.crafting;

import com.rwtema.tinkertailor.utils.ModCompatibilityModule;
import mods.natura.common.NContent;

@ModCompatibilityModule.InitialiseMe(requiredMods = "Natura")
public class NaturaCompatibilityModule extends ModCompatibilityModule {

	@Override
	public void initStart() {
		Ingredients.cloud = NContent.cloud;
		Ingredients.netherBerry = NContent.netherBerryItem;
	}
}
