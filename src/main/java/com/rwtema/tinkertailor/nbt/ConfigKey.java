package com.rwtema.tinkertailor.nbt;

import com.rwtema.tinkertailor.TinkersTailor;
import java.util.ArrayList;
import java.util.List;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class ConfigKey<V> {
	public static List<ConfigKey> configList = new ArrayList<ConfigKey>();
	public final String category;
	public final String key;
	public final String comment;
	public final V defaultValue;
	public final Property.Type type;
	public boolean isReConfiguarable = false;

	public V value;

	ConfigKey(String key, String comment, V defaultValue) {
		this(TinkersTailorConstants.CONFIG_MAIN_CATEGORY, key, comment, defaultValue);
	}

	ConfigKey(String category, String key, String comment, V defaultValue) {
		this(category, key, comment, defaultValue, getType(defaultValue));
	}

	ConfigKey(String category, String key, String comment, V defaultValue, Property.Type type) {
		this.category = category;
		this.key = key;
		this.comment = comment;
		this.defaultValue = defaultValue;
		this.type = type;

		configList.add(this);

		if (Config.configuration != null)
			load(Config.configuration);
	}

	public static Property.Type getType(Object defaultValue) {
		if (defaultValue instanceof Boolean) return Property.Type.BOOLEAN;
		if (defaultValue instanceof Integer) return Property.Type.INTEGER;
		if (defaultValue instanceof Double) return Property.Type.DOUBLE;
		if (defaultValue instanceof String) return Property.Type.STRING;
		throw new RuntimeException("Not Supported: " + defaultValue);
	}

	public V get() {
		if (value == null) {
			throw new RuntimeException("Too Soon");
		}

		return value;
	}

	public void setValue(V v) {
		value = v;
	}

	public void load(Configuration configuration) {
		String defaultValueString = this.defaultValue.toString();
		Property property = configuration.get(category, key, defaultValueString, comment, type);
		property.setDefaultValue(defaultValueString);

		if (Config.version < resetVersionNo && !defaultValueString.equals(property.getString())) {
			property.setValue(defaultValueString);
			TinkersTailor.logger.info("Value for Config:" + key + " is being auto-changed to " + defaultValueString + " for config version " + Config.CUR_VERSION_NO + ".");
		}

		String rawValue = property.getString();
		Object o = null;

		try {
			if (type == Property.Type.BOOLEAN) {
				o = Boolean.parseBoolean(rawValue);
			} else if (type == Property.Type.INTEGER) {
				o = Integer.parseInt(rawValue);
			} else if (type == Property.Type.DOUBLE) {
				o = Double.parseDouble(rawValue);
			} else if (type == Property.Type.STRING) {
				o = rawValue;
			}
		} catch (Exception e) {
			o = defaultValue;
		}

		if (o == null) o = defaultValue;

		value = (V) o;
	}

	public ConfigKey<V> setReConfiguarable() {
		isReConfiguarable = true;
		return this;
	}

	int resetVersionNo;

	public ConfigKey<V> setResetVersion(int resetVersionNo) {
		this.resetVersionNo = resetVersionNo;
		return this;
	}
}
