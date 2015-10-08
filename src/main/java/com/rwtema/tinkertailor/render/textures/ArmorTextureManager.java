package com.rwtema.tinkertailor.render.textures;

import com.rwtema.tinkertailor.items.ArmorCore;
import com.rwtema.tinkertailor.nbt.TinkerTailorConstants;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.tools.ToolMaterial;

public class ArmorTextureManager {

	ResourceLocation base;

	TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();

	TIntObjectHashMap<String> textureStringMap = new TIntObjectHashMap<String>();
	TIntObjectHashMap<ResourceLocation> textureLocationMap = new TIntObjectHashMap<ResourceLocation>();
	String armorString;

	public static ArmorTextureManager getManager(int i) {
		if (i == 0)
			return helmet;
		else if (i == 1)
			return chestplate;
		else if (i == 2)
			return leggings;
		else
			return boots;
	}

	public static final String prefix = TinkerTailorConstants.RESOURCE_FOLDER + ":textures/items/armor/";

	static final ArmorTextureManager helmet = new ArmorTextureManager(
			"tinkers:textures/items/armor/helmet_%s.png",
			prefix + "helmet.png"
	);

	static final ArmorTextureManager chestplate = new ArmorTextureManager(
			prefix + "chestplate_%s.png",
			prefix + "chestplate.png"
	);

	static final ArmorTextureManager leggings = new ArmorTextureManager(
			prefix + "leggings_%s.png",
			prefix + "leggings.png"
	);

	static final ArmorTextureManager boots = new ArmorTextureManager(
			prefix + "boots_%s.png",
			prefix + "boots.png"
	);

	public ArmorTextureManager(String armorString, String defaultString) {
		base = new ResourceLocation(defaultString);
		this.armorString = armorString;
	}

	public String getArmorName(int matid) {
		String string = textureStringMap.get(matid);

		//noinspection PointlessBooleanExpression
		if (string == null || ArmorCore.DEBUG_ALWAYS_RELOAD) {
			ToolMaterial material = TConstructRegistry.getMaterial(matid);
			string = String.format(armorString, material.name());
			ResourceLocation resourceLocation = new ResourceLocation(string);
			textureManager.loadTexture(resourceLocation, new ColoredTexture(resourceLocation, base, material.primaryColor));
			textureStringMap.put(matid, string);

		}
		return string;
	}

	public ResourceLocation getArmorResource(int matid) {
		ResourceLocation resourceLocation = textureLocationMap.get(matid);

		if (resourceLocation == null || ArmorCore.DEBUG_ALWAYS_RELOAD) {
			resourceLocation = new ResourceLocation(getArmorName(matid));
			textureLocationMap.put(matid, resourceLocation);
		}
		return resourceLocation;
	}

}
