package com.rwtema.tinkertailor.imc;

import com.google.common.base.Throwables;
import com.rwtema.tinkertailor.modifiers.Modifier;
import com.rwtema.tinkertailor.modifiers.ModifierRegistry;
import com.rwtema.tinkertailor.nbt.TinkersTailorConstants;
import cpw.mods.fml.common.event.FMLInterModComms;
import java.lang.reflect.Field;
import static java.lang.reflect.Modifier.isStatic;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import tconstruct.library.TConstructRegistry;

public class IMCNBTLoader {
	public static HashMap<String, IMCNBTLoader> nbtLoaders = new HashMap<String, IMCNBTLoader>();
	public static HashMap<String, IMCSimple<String>> stringLoaders = new HashMap<String, IMCSimple<String>>();
	public static HashMap<String, IMCSimple<ItemStack>> itemStackLoaders = new HashMap<String, IMCSimple<ItemStack>>();
	public static HashMap<Class, IMCFieldHandler> typeHandlers;
	public static HashMap<String, IMCFieldHandler> nameHandlers;

	static {
		nameHandlers = new HashMap<String, IMCFieldHandler>();
		nameHandlers.put("material_id", new IMCFieldHandler((int) 0, "") {
			@Override
			public void load(Field field, IMC instance, String key, NBTTagCompound compound, boolean required) throws IllegalAccessException, NBTLoadException {
				int i;
				if (compound.hasKey(key, Constants.NBT.TAG_STRING))
					i = ModifierRegistry.reverseMap.get(TConstructRegistry.toolMaterialStrings.get(compound.getString(key)));
				else if (compound.hasKey(key, Constants.NBT.TAG_INT))
					i = compound.getInteger(key);
				else {
					i = -1;
				}
				field.setInt(instance, i);
			}
		});

		typeHandlers = new LinkedHashMap<Class, IMCFieldHandler>();
		typeHandlers.put(boolean.class, new IMCFieldHandler(false, "") {

			@Override
			public void load(Field field, IMC instance, String key, NBTTagCompound compound, boolean required) throws IllegalAccessException {
				field.setBoolean(instance, compound.getBoolean(key));
			}
		});

		typeHandlers.put(byte.class, new IMCFieldHandler((byte) 0, "") {
			@Override
			public void load(Field field, IMC instance, String key, NBTTagCompound compound, boolean required) throws IllegalAccessException {
				field.setByte(instance, compound.getByte(key));
			}
		});

		typeHandlers.put(short.class, new IMCFieldHandler((short) 0, "") {
			@Override
			public void load(Field field, IMC instance, String key, NBTTagCompound compound, boolean required) throws IllegalAccessException {
				field.setShort(instance, compound.getShort(key));
			}
		});

		typeHandlers.put(int.class, new IMCFieldHandler((int) 0, "") {
			@Override
			public void load(Field field, IMC instance, String key, NBTTagCompound compound, boolean required) throws IllegalAccessException {
				field.setInt(instance, compound.getInteger(key));
			}
		});

		typeHandlers.put(long.class, new IMCFieldHandler((long) 0, "") {
			@Override
			public void load(Field field, IMC instance, String key, NBTTagCompound compound, boolean required) throws IllegalAccessException {
				field.setLong(instance, compound.getLong(key));
			}
		});

		typeHandlers.put(float.class, new IMCFieldHandler((float) 0, "") {
			@Override
			public void load(Field field, IMC instance, String key, NBTTagCompound compound, boolean required) throws IllegalAccessException {
				field.setFloat(instance, compound.getFloat(key));
			}
		});

		typeHandlers.put(double.class, new IMCFieldHandler((double) 0, "") {
			@Override
			public void load(Field field, IMC instance, String key, NBTTagCompound compound, boolean required) throws IllegalAccessException {
				field.setDouble(instance, compound.getDouble(key));
			}
		});

		typeHandlers.put(String.class, new IMCFieldHandler("") {
			@Override
			public void load(Field field, IMC instance, String key, NBTTagCompound compound, boolean required) throws IllegalAccessException {
				field.set(instance, compound.getString(key));
			}
		});

		typeHandlers.put(int[].class, new IMCFieldHandler("") {
			@Override
			public void load(Field field, IMC instance, String key, NBTTagCompound compound, boolean required) throws IllegalAccessException {
				field.set(instance, compound.getIntArray(key));
			}
		});

		typeHandlers.put(NBTTagCompound.class, new IMCFieldHandler("") {
			@Override
			public void load(Field field, IMC instance, String key, NBTTagCompound compound, boolean required) throws IllegalAccessException {
				field.set(instance, compound.getCompoundTag(key));
			}
		});

		typeHandlers.put(ItemStack.class, new IMCFieldHandler("") {
			@Override
			public void load(Field field, IMC instance, String key, NBTTagCompound compound, boolean required) throws IllegalAccessException {
				field.set(instance, ItemStack.loadItemStackFromNBT(compound.getCompoundTag(key)));
			}
		});

		typeHandlers.put(Block.class, new IMCFieldHandler("Block ID") {
			@Override
			public void load(Field field, IMC instance, String key, NBTTagCompound compound, boolean required) throws IllegalAccessException {
				field.set(instance, Block.getBlockFromName(compound.getString(key)));
			}
		});

		typeHandlers.put(Item.class, new IMCFieldHandler("Item ID") {
			@Override
			public void load(Field field, IMC instance, String key, NBTTagCompound compound, boolean required) throws IllegalAccessException {
				field.set(instance, Item.itemRegistry.getObject(compound.getString(key)));
			}
		});

		typeHandlers.put(Modifier.class, new IMCFieldHandler("Modifier ID") {
			@Override
			public void load(Field field, IMC instance, String key, NBTTagCompound compound, boolean required) throws IllegalAccessException, NBTLoadException {
				String string = compound.getString(key);
				Modifier value = ModifierRegistry.modifiers.get(string);
				if (value == null && required) throw new NBTLoadException("Modifier " + string + " not found");
				field.set(instance, value);
			}
		});

	}

	public Class<? extends IMC> base;
	public HashMap<Field, IMCFieldHandler> fields = new LinkedHashMap<Field, IMCFieldHandler>();
	public HashSet<String> required = new LinkedHashSet<String>();
	public HashMap<Field, Object> defaults = new HashMap<Field, Object>();
	public final String desc;

	public IMCNBTLoader(Class<? extends IMC> base, String desc) {
		this.base = base;
		this.desc = desc;

		IMC test = newInstance();

		for (Field field : base.getDeclaredFields()) {
			if (isStatic(field.getModifiers()))
				continue;

			IMCFieldHandler imcFieldHandler = nameHandlers.get(field.getName());
			if (imcFieldHandler == null) imcFieldHandler = typeHandlers.get(field.getType());
			if (imcFieldHandler == null) continue;

			Object defaultTypeValue = imcFieldHandler.defaultValue;

			field.setAccessible(true);

			try {
				Object o = field.get(test);
				Object defaultValue = imcFieldHandler.defaultValue;
				if (o == null || defaultValue.equals(o)) {
					required.add(field.getName());
				}

				defaults.put(field, o);

				fields.put(field, imcFieldHandler);
			} catch (IllegalAccessException e) {
				throw Throwables.propagate(e);
			}
		}
	}

	public static void registerString(String key, IMCSimple<String> handler) {
		stringLoaders.put(key, handler);
	}

	public static void registerItemStack(String key, IMCSimple<ItemStack> handler) {
		itemStackLoaders.put(key, handler);
	}

	public static void registerNBT(Class<? extends IMC> clazz, String desc) {
		nbtLoaders.put(clazz.getName(), new IMCNBTLoader(clazz, desc));
	}

	public static void registerNBT(String name, IMC imc, String desc) {
		nbtLoaders.put(name, new IMCNBTLoader(imc.getClass(), desc));
	}

	public static void sendTest() {
		NBTTagCompound tag = new NBTTagCompound();

		FMLInterModComms.sendMessage(TinkersTailorConstants.MOD_ID, "addDefaultModifierToMaterial", (NBTTagCompound) tag.copy());
		tag.setString("modifier", "prickly");
		FMLInterModComms.sendMessage(TinkersTailorConstants.MOD_ID, "addDefaultModifierToMaterial", (NBTTagCompound) tag.copy());
		tag.setString("modifier", "prickly");
		tag.setString("material", "Iron");
		FMLInterModComms.sendMessage(TinkersTailorConstants.MOD_ID, "addDefaultModifierToMaterial", (NBTTagCompound) tag.copy());
		tag.setString("modifier", "prickly");
		tag.setString("material", "Iron");
		tag.setInteger("level", 2);
		FMLInterModComms.sendMessage(TinkersTailorConstants.MOD_ID, "addDefaultModifierToMaterial", (NBTTagCompound) tag.copy());
	}

	public void load(NBTTagCompound tag) throws NBTLoadException, IMCRunException {
		for (String s : required) {
			if (!tag.hasKey(s)) throw new NBTLoadException(s + " is missing");
		}

		IMC imc = newInstance();

		for (Map.Entry<Field, IMCFieldHandler> entry : fields.entrySet()) {
			Field key = entry.getKey();
			try {
				String name = key.getName();
				if (tag.hasKey(name))
					entry.getValue().load(key, imc, name, tag, required.contains(name));
			} catch (IllegalAccessException e) {
				throw Throwables.propagate(e);
			}
		}

		imc.run();

	}

	private IMC newInstance() {
		IMC check;
		try {
			check = base.newInstance();
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
		return check;
	}

	public static abstract class IMCSimple<T> {
		abstract void load(T t) throws IMCRunException;
	}

	public static abstract class IMCFieldHandler {
		@Nonnull
		Object defaultValue;
		public final String expectedType;

		public IMCFieldHandler(String expectedType) {
			this(new Object(), expectedType);
		}

		public IMCFieldHandler(@Nonnull Object defaultValue, String expectedType) {
			this.defaultValue = defaultValue;
			this.expectedType = expectedType;
		}

		public abstract void load(Field field, IMC instance, String key, NBTTagCompound compound, boolean required) throws IllegalAccessException, NBTLoadException;
	}
}
