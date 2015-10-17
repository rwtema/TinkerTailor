package com.rwtema.tinkertailor.render;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;

public class RendererHandler implements IResourceManagerReloadListener {
	public final static String ITEMS_TEXTURE = TextureMap.locationItemsTexture.toString();
	public static final RendererHandler instance = new RendererHandler();
	public static List<ModelRendererCustom> rendererCustoms = new ArrayList<ModelRendererCustom>();
	public static HashMap<String, IIcon> iconHashMap = new HashMap<String, IIcon>();
	public static TextureMap itemMap = null;

	public static void init() {
		((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(instance);
		MinecraftForge.EVENT_BUS.register(instance);
		FMLCommonHandler.instance().bus().register(instance);
	}

	public static IIcon registerIcon(String iconName) {
		if (itemMap == null) return null;
		return itemMap.registerIcon(iconName);
	}

	public void clearCache() {
		reloadRenderers();
		rendererCustoms.clear();
	}

	@Override
	public void onResourceManagerReload(IResourceManager p_110549_1_) {
		reloadRenderers();
	}

	public void reloadRenderers() {
		for (ModelRendererCustom rendererCustom : rendererCustoms) {
			for (IRender textureQuadIcon : rendererCustom.staticIconList) {
				textureQuadIcon.onReload();
			}
			rendererCustom.destroyDisplayList();
		}
	}

	@SubscribeEvent
	public void texturePre(TextureStitchEvent.Pre event) {
		if (event.map.getTextureType() != 1) return;
		itemMap = event.map;
		for (Map.Entry<String, IIcon> entry : iconHashMap.entrySet()) {
			entry.setValue(event.map.registerIcon(entry.getKey()));
		}
	}

	@SubscribeEvent
	public void texturePost(TextureStitchEvent.Post event) {
		reloadRenderers();
	}
}
