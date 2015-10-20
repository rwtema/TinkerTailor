package com.rwtema.tinkertailor.utils;

import com.google.common.base.Throwables;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.discovery.ASMDataTable;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Set;

public abstract class ModCompatibilityModule {
	public void onCreated() {

	}

	public void preInit() {

	}

	public void initStart() {

	}

	public void initEnd() {

	}

	public void postInit() {

	}

	public void loadComplete() {

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE})
	public @interface InitialiseMe {
		public String requiredMods();
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<ModCompatibilityModule> loadModCompatibilityModules(FMLPreInitializationEvent event) {
		Set<ASMDataTable.ASMData> set = event.getAsmData().getAll(InitialiseMe.class.getName());

		ArrayList<ModCompatibilityModule> modCompatibilityModules = new ArrayList<ModCompatibilityModule>(set.size());

		loopClasses:
		for (ASMDataTable.ASMData asmData : set) {
			String requiredMods = (String) asmData.getAnnotationInfo().get("requiredMods");
			if (requiredMods != null) {
				for (String mod : requiredMods.split(";")) {
					if (!mod.isEmpty() && !Loader.isModLoaded(mod))
						continue loopClasses;
				}
			}

			try {
				modCompatibilityModules.add((ModCompatibilityModule) Class.forName(asmData.getClassName()).newInstance());
			} catch (Throwable e) {
				throw Throwables.propagate(e);
			}
		}
		return modCompatibilityModules;
	}
}
