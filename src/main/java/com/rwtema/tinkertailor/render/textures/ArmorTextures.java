package com.rwtema.tinkertailor.render.textures;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.rwtema.tinkertailor.TinkersTailor;
import com.rwtema.tinkertailor.render.RendererHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
	@Nonnull
	public static List<String> getTextures(int material) {
		List<String> icons = new ArrayList<String>();
		for (PatternBuilder.ItemKey itemKey : getItemKeyFromMat(material)) {
			icons.addAll(getIcon(itemKey));
		}
		return icons;
	}

	@Nonnull
	private static List<PatternBuilder.ItemKey> getItemKeyFromMat(int matId) {
		ToolMaterial toolMaterial = TConstructRegistry.toolMaterials.get(matId);
		if (toolMaterial == null) return ImmutableList.of();
		return getItemKey(matId);
	}

	@Nonnull
	private static List<String> getIcon(PatternBuilder.ItemKey itemKey) {
		List<String> icons = new ArrayList<String>();
		if (itemKey.item instanceof ItemBlock) {
			Block field_150939_a = ((ItemBlock) itemKey.item).field_150939_a;
			icons.addAll(getIcons(field_150939_a, itemKey.damage));
		}

		if (icons.isEmpty())
			icons.addAll(getIcon(itemKey.item, itemKey.damage));
		return icons;
	}

	@Nonnull
	private static List<String> getIcon(Item item, int meta) {
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

		if (iconName == null) return ImmutableList.of();

		return Lists.newArrayList(iconName);
	}

	@Nonnull
	private static List<String> getIcons(Block block, int meta) {
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

		if (any == null) return ImmutableList.of();

		for (int i = 0; i < icons.length; i++) {
			if (icons[i] != null) continue;
			if (i < 2 && top != null) icons[i] = top;
			else if (i >= 2 && side != null) icons[i] = side;
			else icons[i] = any;
		}

		return Lists.newArrayList(icons);

	}

	@Nullable
	private static String getIconName(IIcon icon) {
		if (icon == null) return null;

		String iconName = icon.getIconName();

		if (RendererHandler.items.getTextureExtry(iconName) != null || RendererHandler.blocks.getTextureExtry(iconName) != null)
			return iconName;
		else
			return null;
	}

	@Nonnull
	private static List<PatternBuilder.ItemKey> getItemKey(int material) {
		List<PatternBuilder.ItemKey> list = new ArrayList<PatternBuilder.ItemKey>();

		for (Map.Entry<String, PatternBuilder.MaterialSet> entry : PatternBuilder.instance.materialSets.entrySet()) {
			if (entry.getValue().materialID == material) {
				String key = entry.getKey();
				for (PatternBuilder.ItemKey itemKey : PatternBuilder.instance.materials) {
					if (key.equals(itemKey.key)) {
						if (!(itemKey.item instanceof ToolShard)) {
							list.add(itemKey);
						}
					}
				}
			}
		}

		return list;
	}

}
