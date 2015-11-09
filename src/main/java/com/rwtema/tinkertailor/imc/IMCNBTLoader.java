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
import java.util.Map;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.tools.ToolMaterial;

public class IMCNBTLoader {
	static HashMap<String, IMCNBTLoader> nbtLoaders = new HashMap<String, IMCNBTLoader>();
	Class<? extends IMC> base;
	HashMap<Field, IMCFieldHandler> fields = new HashMap<Field, IMCFieldHandler>();

	static HashMap<Class, IMCFieldHandler> typeHandlers;
	static HashMap<String, IMCFieldHandler> nameHandlers;
	HashSet<String> required = new HashSet<String>();

	static {
		nameHandlers = new HashMap<String, IMCFieldHandler>();
		nameHandlers.put("material_id", new IMCFieldHandler((int) 0) {
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

		typeHandlers = new HashMap<Class, IMCFieldHandler>();
		typeHandlers.put(boolean.class, new IMCFieldHandler(false) {

			@Override
			public void load(Field field, IMC instance, String key, NBTTagCompound compound, boolean required) throws IllegalAccessException {
				field.setBoolean(instance, compound.getBoolean(key));
			}
		});

		typeHandlers.put(byte.class, new IMCFieldHandler((byte) 0) {
			@Override
			public void load(Field field, IMC instance, String key, NBTTagCompound compound, boolean required) throws IllegalAccessException {
				field.setByte(instance, compound.getByte(key));
			}
		});

		typeHandlers.put(short.class, new IMCFieldHandler((short) 0) {
			@Override
			public void load(Field field, IMC instance, String key, NBTTagCompound compound, boolean required) throws IllegalAccessException {
				field.setShort(instance, compound.getShort(key));
			}
		});

		typeHandlers.put(int.class, new IMCFieldHandler((int) 0) {
			@Override
			public void load(Field field, IMC instance, String key, NBTTagCompound compound, boolean required) throws IllegalAccessException {
				field.setInt(instance, compound.getInteger(key));
			}
		});

		typeHandlers.put(long.class, new IMCFieldHandler((long) 0) {
			@Override
			public void load(Field field, IMC instance, String key, NBTTagCompound compound, boolean required) throws IllegalAccessException {
				field.setLong(instance, compound.getLong(key));
			}
		});

		typeHandlers.put(float.class, new IMCFieldHandler((float) 0) {
			@Override
			public void load(Field field, IMC instance, String key, NBTTagCompound compound, boolean required) throws IllegalAccessException {
				field.setFloat(instance, compound.getFloat(key));
			}
		});

		typeHandlers.put(double.class, new IMCFieldHandler((double) 0) {
			@Override
			public void load(Field field, IMC instance, String key, NBTTagCompound compound, boolean required) throws IllegalAccessException {
				field.setDouble(instance, compound.getDouble(key));
			}
		});

		typeHandlers.put(String.class, new IMCFieldHandler() {
			@Override
			public void load(Field field, IMC instance, String key, NBTTagCompound compound, boolean required) throws IllegalAccessException {
				field.set(instance, compound.getString(key));
			}
		});

		typeHandlers.put(int[].class, new IMCFieldHandler() {
			@Override
			public void load(Field field, IMC instance, String key, NBTTagCompound compound, boolean required) throws IllegalAccessException {
				field.set(instance, compound.getIntArray(key));
			}
		});

		typeHandlers.put(NBTTagCompound.class, new IMCFieldHandler() {
			@Override
			public void load(Field field, IMC instance, String key, NBTTagCompound compound, boolean required) throws IllegalAccessException {
				field.set(instance, compound.getCompoundTag(key));
			}
		});

		typeHandlers.put(ItemStack.class, new IMCFieldHandler() {
			@Override
			public void load(Field field, IMC instance, String key, NBTTagCompound compound, boolean required) throws IllegalAccessException {
				field.set(instance, ItemStack.loadItemStackFromNBT(compound.getCompoundTag(key)));
			}
		});

		typeHandlers.put(Block.class, new IMCFieldHandler() {
			@Override
			public void load(Field field, IMC instance, String key, NBTTagCompound compound, boolean required) throws IllegalAccessException {
				field.set(instance, Block.getBlockFromName(compound.getString(key)));
			}
		});

		typeHandlers.put(Item.class, new IMCFieldHandler() {
			@Override
			public void load(Field field, IMC instance, String key, NBTTagCompound compound, boolean required) throws IllegalAccessException {
				field.set(instance, Item.itemRegistry.getObject(compound.getString(key)));
			}
		});

		typeHandlers.put(ToolMaterial.class, new IMCFieldHandler() {
			@Override
			public void load(Field field, IMC instance, String key, NBTTagCompound compound, boolean required) throws IllegalAccessException, NBTLoadException {
				String string = compound.getString(key);
				ToolMaterial value = TConstructRegistry.toolMaterialStrings.get(string);
				if (value == null && required) throw new NBTLoadException("Material " + string + " not found");
				field.set(instance, value);
			}
		});

		typeHandlers.put(Modifier.class, new IMCFieldHandler() {
			@Override
			public void load(Field field, IMC instance, String key, NBTTagCompound compound, boolean required) throws IllegalAccessException, NBTLoadException {
				String string = compound.getString(key);
				Modifier value = ModifierRegistry.modifiers.get(string);
				if (value == null && required) throw new NBTLoadException("Modifier " + string + " not found");
				field.set(instance, value);
			}
		});

	}

	public IMCNBTLoader(Class<? extends IMC> base) {
		this.base = base;

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
				Object defaultValue;
				if (o == null || (defaultValue = defaultTypeValue) == null || defaultValue.equals(o)) {
					required.add(field.getName());
				}

				fields.put(field, imcFieldHandler);
			} catch (IllegalAccessException e) {
				throw Throwables.propagate(e);
			}
		}
	}

	public static void register(Class<? extends IMC> clazz) {
		nbtLoaders.put(clazz.getName(), new IMCNBTLoader(clazz));
	}

	public static void register(String name, IMC imc) {
		nbtLoaders.put(name, new IMCNBTLoader(imc.getClass()));
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

	public void load(NBTTagCompound tag) throws NBTLoadException {
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

	public static abstract class IMCFieldHandler {
		@Nonnull
		Object defaultValue;

		public IMCFieldHandler() {
			this(new Object());
		}

		public IMCFieldHandler(@Nonnull Object defaultValue) {
			this.defaultValue = defaultValue;
		}


		public abstract void load(Field field, IMC instance, String key, NBTTagCompound compound, boolean required) throws IllegalAccessException, NBTLoadException;

		public String name(){
			return null;
		}
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

}
