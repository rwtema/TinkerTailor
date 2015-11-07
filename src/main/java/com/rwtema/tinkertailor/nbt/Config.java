package com.rwtema.tinkertailor.nbt;

import net.minecraftforge.common.config.Configuration;

public class Config {

	public static Configuration configuration;

	public static int version;

	public static final int CUR_VERSION_NO = 0;

	public static void load(Configuration configuration) {
		Config.configuration = configuration;
		configuration.load();
		version = configuration.getInt("VersionNo", TinkersTailorConstants.CONFIG_ADVANCED_CATEGORY, CUR_VERSION_NO, 0, CUR_VERSION_NO, "Keeps track of current configuration version number");

		for (ConfigKey configKey : ConfigKey.configList) {
			configKey.load(configuration);
		}

		if(version != CUR_VERSION_NO)
			configuration.get("VersionNo", TinkersTailorConstants.CONFIG_ADVANCED_CATEGORY, CUR_VERSION_NO).setValue(CUR_VERSION_NO);

		if (configuration.hasChanged())
			configuration.save();
	}

	public static void save() {
		if (configuration.hasChanged())
			configuration.save();
	}

	public final static ConfigKey<Boolean> SoftMetal = new ConfigKey<Boolean>("SoftMetalPartsCrafting", "Enable 'simple' iron/copper tool parts being buildable in the part builder.", true);
	public final static ConfigKey<Boolean> WeaponInvis = new ConfigKey<Boolean>("WeaponOverridesInvis", "Holding a weapon or bow cancels armor invisibility on other players.", false).setReConfiguarable();
	public final static ConfigKey<Boolean> DisableAdvancedCache = new ConfigKey<Boolean>(TinkersTailorConstants.CONFIG_ADVANCED_CATEGORY, "DisableAdvancedCaching", "Disables advanced caching of item properties (decreases performance)", false);
//	public static ConfigKey<Double> DungeonProbability = new ConfigKey<Double>("DungeonChestProbability", "Probability of an armor appearing in a dungeon chest", 0.05D);
}
