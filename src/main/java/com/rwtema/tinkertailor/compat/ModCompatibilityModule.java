package com.rwtema.tinkertailor.compat;

import com.google.common.base.Throwables;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModAPIManager;
import cpw.mods.fml.common.discovery.ASMDataTable;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Set;
import org.apache.logging.log4j.core.helpers.Strings;

public abstract class ModCompatibilityModule {
	@SuppressWarnings("unchecked")
	public static ArrayList<ModCompatibilityModule> loadModCompatibilityModules(FMLPreInitializationEvent event) {
		ASMDataTable asmDataTable = event.getAsmData();
		Set<ASMDataTable.ASMData> set = asmDataTable.getAll(InitialiseMe.class.getName());

		ArrayList<ModCompatibilityModule> modCompatibilityModules = new ArrayList<ModCompatibilityModule>(set.size());

		loopClasses:
		for (ASMDataTable.ASMData asmData : set) {
			String requiredMods = (String) asmData.getAnnotationInfo().get("requiredMods");
			if (Strings.isNotEmpty(requiredMods)) {
				for (String mod : requiredMods.split(";")) {
					if (!isModLoaded(mod))
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

	public static boolean isModLoaded(String mod) {
		return Loader.isModLoaded(mod) || ModAPIManager.INSTANCE.hasAPI(mod);
	}

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
		String requiredMods();
	}
}
