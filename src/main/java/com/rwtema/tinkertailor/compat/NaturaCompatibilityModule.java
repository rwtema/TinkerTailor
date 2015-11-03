package com.rwtema.tinkertailor.compat;

import com.rwtema.tinkertailor.crafting.Ingredients;
import mods.natura.common.NContent;

@ModCompatibilityModule.InitialiseMe(requiredMods = "Natura")
public class NaturaCompatibilityModule extends ModCompatibilityModule {

	@Override
	public void initStart() {
		Ingredients.cloud = NContent.cloud;
		Ingredients.netherBerry = NContent.netherBerryItem;
		Ingredients.cotton = NContent.plantItem;
		Ingredients.berry = NContent.berryItem;
		Ingredients.glowShroom = NContent.glowshroom;
		Ingredients.potashApple = NContent.potashApple;
	}
}
