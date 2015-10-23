package com.rwtema.tinkertailor.render.textures;

import com.rwtema.tinkertailor.TinkersTailor;
import com.rwtema.tinkertailor.render.RendererHandler;
import java.util.ArrayList;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.oredict.OreDictionary;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.PatternBuilder;
import tconstruct.library.tools.ToolMaterial;
import tconstruct.tools.items.ToolShard;

public class ArmorTextures {
	public static String[] getTextures(int material) {
		return getIcon(getItemKeyFromMat(material));
	}

	private static PatternBuilder.ItemKey getItemKeyFromMat(int matid) {
		ToolMaterial toolMaterial = TConstructRegistry.toolMaterials.get(matid);
		if (toolMaterial == null) return null;
		return getItemKey(matid);
	}

	private static String[] getIcon(PatternBuilder.ItemKey itemKey) {

		if (itemKey.item instanceof ItemBlock) {
			Block field_150939_a = ((ItemBlock) itemKey.item).field_150939_a;
			String[] icons = getIcons(field_150939_a, itemKey.damage);
			if (icons != null) return icons;
		}

		return getIcon(itemKey.item, itemKey.damage);
	}

	private static String[] getIcon(Item item, int meta) {
		String iconName = null;

		if (meta != OreDictionary.WILDCARD_VALUE) {
			try {
				iconName = getIconName(item.getIcon(new ItemStack(item, 1, meta), 0));
			} catch (Throwable ignore) {

			}
		}

		if (iconName == null) {
			for (ItemStack itemStack : TinkersTailor.proxy.addVariants(item, new ArrayList<ItemStack>())) {
				try {
					iconName = getIconName(item.getIcon(itemStack, 0));
				} catch (Throwable ignore) {

				}
				if (iconName != null) break;
			}
		}

		if (iconName == null) {
			try {
				iconName = getIconName(item.getIcon(new ItemStack(item), 0));
			} catch (Throwable ignore) {

			}
		}

		if (iconName == null) return null;

		return new String[]{iconName};
	}

	private static String[] getIcons(Block block, int meta) {
		String any = null;
		String top = null;
		String side = null;

		String[] icons = new String[6];

		for (int s = 0; s < 6; s++) {
			String iconName = null;
			if (meta == OreDictionary.WILDCARD_VALUE) {
				for (int i = 0; i < 16; i++) {
					try {
						iconName = getIconName(block.getIcon(s, meta));
					} catch (Throwable err) {
						continue;
					}
					if (iconName != null) break;
				}
			} else
				try {
					iconName = getIconName(block.getIcon(s, meta));
				} catch (Throwable err) {
					continue;
				}

			if (iconName == null) continue;
			icons[s] = iconName;
			if (any == null) any = iconName;
			if (s < 2) {
				if (top == null) top = iconName;
			} else {
				if (side == null) side = iconName;
			}
		}

		if (any == null) return null;

		for (int i = 0; i < icons.length; i++) {
			if (icons[i] != null) continue;
			if (i < 2 && top != null) icons[i] = top;
			else if (i >= 2 && side != null) icons[i] = side;
			else icons[i] = any;
		}

		return icons;

	}

	private static String getIconName(IIcon icon) {
		if (icon == null) return null;

		String iconName = icon.getIconName();

		if (RendererHandler.items.getTextureExtry(iconName) != null || RendererHandler.blocks.getTextureExtry(iconName) != null)
			return iconName;
		else
			return null;
	}

	private static PatternBuilder.ItemKey getItemKey(int material) {
		PatternBuilder.ItemKey r = null;
		for (Map.Entry<String, PatternBuilder.MaterialSet> entry : PatternBuilder.instance.materialSets.entrySet()) {
			if (entry.getValue().materialID == material) {
				String key = entry.getKey();
				for (PatternBuilder.ItemKey itemKey : PatternBuilder.instance.materials) {

					if (key.equals(itemKey.key)) {
						if (itemKey.item instanceof ItemBlock)
							return itemKey;


						if (r == null || !(itemKey.item instanceof ToolShard)) {
							r = itemKey;
						}


					}
				}
			}
		}

		return r;
	}

	private static String getKey(int matid) {
		for (Map.Entry<String, PatternBuilder.MaterialSet> entry : PatternBuilder.instance.materialSets.entrySet()) {
			if (entry.getValue().materialID == matid)
				return entry.getKey();
		}
		return null;
	}
}
