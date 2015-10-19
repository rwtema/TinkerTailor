package com.rwtema.tinkertailor.utils;

import com.google.common.base.Throwables;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.discovery.ASMDataTable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Set;

public abstract class ModCompat {
	public void preInit() {

	}

	public void init() {

	}

	public void postInit() {

	}

	public void loadComplete(){

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE})
	public @interface Initializer {
		public String requiredMods();
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<ModCompat> loadMods(ASMDataTable data) {
		ArrayList<ModCompat> list = new ArrayList<ModCompat>();
		Set<ASMDataTable.ASMData> set = data.getAll(Initializer.class.getName());
		loop:
		for (ASMDataTable.ASMData asmData : set) {
			String requiredMods = (String) asmData.getAnnotationInfo().get("requiredMods");
			for (String mod : requiredMods.split(";")) {
				if (!mod.isEmpty() && !Loader.isModLoaded(mod))
					continue loop;
			}

			try {
				Class<? extends ModCompat> clazz = (Class<? extends ModCompat>) Class.forName(asmData.getClassName());
				ModCompat modCompat = clazz.newInstance();
				list.add(modCompat);

			} catch (ClassNotFoundException e) {
				throw Throwables.propagate(e);
			} catch (InstantiationException e) {
				throw Throwables.propagate(e);
			} catch (IllegalAccessException e) {
				throw Throwables.propagate(e);
			}
		}
		return list;
	}
}
