package com.rwtema.tinkertailor.coremod;

import com.google.common.eventbus.EventBus;
import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import java.util.Map;

@IFMLLoadingPlugin.TransformerExclusions(value = {"com.rwtema.tinkershats.coremod.", "com.rwtema.tinkershats.coremod.CoreTinkerer"})
@IFMLLoadingPlugin.SortingIndex(Integer.MAX_VALUE)
public class CoreTinkerer extends DummyModContainer implements IFMLLoadingPlugin {
	protected static final ModMetadata md;

	static {
		md = new ModMetadata();
		md.autogenerated = false;
		md.authorList.add("RWTema");
		md.credits = "RWTema";
		md.modId = "CoreTinkerTailor";
		md.version = "Whatever";
		md.name = "CoreTinkerTailor";
		md.description = "Core mod for tinker tailor";


	}

	public static boolean runtimeDeobfuscationEnabled;


	public CoreTinkerer() {
		super(md);
	}

	@Override
	public String[] getASMTransformerClass() {
		return new String[]{ClassTransformerHandler.class.getName()};
	}

	@Override
	public String getModContainerClass() {
		return CoreTinkerer.class.getName();
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
		runtimeDeobfuscationEnabled = (Boolean) data.get("runtimeDeobfuscationEnabled");
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}

	@Override
	public boolean registerBus(EventBus bus, LoadController controller) {
		return true;
	}
}