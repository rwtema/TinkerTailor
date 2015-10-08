package com.rwtema.tinkertailor.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import tconstruct.library.tools.DynamicToolPart;

public class TextureHeavyPlate extends TextureQuadIcon {

	private final ModelArmor armor;

	private final DynamicToolPart part;

	public TextureHeavyPlate(Vec3[] vectors, ModelArmor armor, DynamicToolPart part) {
		super(vectors, null, null);
		this.armor = armor;
		this.part = part;
	}

	@Override
	public void render(float partialTicks) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationItemsTexture);
		ItemStack item = new ItemStack(part, 1, armor.material);
		color = part.getColorFromItemStack(item,0);
		icon = part.getIconFromDamage(armor.material);
		super.render(partialTicks);
		Minecraft.getMinecraft().getTextureManager().bindTexture(armor.location);
	}
}
