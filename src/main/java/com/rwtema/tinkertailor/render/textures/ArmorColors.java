package com.rwtema.tinkertailor.render.textures;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.rwtema.tinkertailor.TinkersTailor;
import com.rwtema.tinkertailor.render.RendererHandler;
import com.rwtema.tinkertailor.utils.MultiKey;
import gnu.trove.list.array.TIntArrayList;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.PatternBuilder;
import tconstruct.library.tools.ToolMaterial;

public class ArmorColors implements IResourceManagerReloadListener {
	static HashMap<MultiKey.Tri<String, Integer, Integer>, TextureDetails> cachedDetails;

	static {
		cachedDetails = new HashMap<MultiKey.Tri<String, Integer, Integer>, TextureDetails>();
		((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(new ArmorColors());
	}

	@Nonnull
	public static List<TextureDetails> getTextures(int material) {
		List<TextureDetails> icons = new ArrayList<TextureDetails>();
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
	private static List<TextureDetails> getIcon(PatternBuilder.ItemKey itemKey) {
		List<TextureDetails> icons = new ArrayList<TextureDetails>();
		if (itemKey.item instanceof ItemBlock) {
			ItemBlock itemBlock = (ItemBlock) itemKey.item;
			Block field_150939_a = itemBlock.field_150939_a;
			if (field_150939_a != null)
				icons.addAll(getIcons(field_150939_a, itemKey.damage, itemBlock));
		}

		if (icons.isEmpty())
			icons.addAll(getIcon(itemKey.item, itemKey.damage));
		return icons;
	}

	@Nonnull
	private static List<TextureDetails> getIcon(Item item, int meta) {
		String iconName = null;
		int col = 0xffffffff;

		if (meta != OreDictionary.WILDCARD_VALUE) {
			try {
				ItemStack stack = new ItemStack(item, 1, meta);
				iconName = getIconName(item.getIcon(stack, 0));
				col = item.getColorFromItemStack(stack, 0);
			} catch (Throwable ignore) {

			}
		}

		if (iconName == null) {
			for (ItemStack itemStack : TinkersTailor.proxy.addVariants(item, new ArrayList<ItemStack>())) {
				try {
					iconName = getIconName(item.getIcon(itemStack, 0));
					col = item.getColorFromItemStack(itemStack, 0);
				} catch (Throwable ignore) {

				}
				if (iconName != null) break;
			}
		}

		if (iconName == null) {
			try {
				ItemStack stack = new ItemStack(item);
				iconName = getIconName(item.getIcon(stack, 0));
				col = item.getColorFromItemStack(stack, 0);
			} catch (Throwable ignore) {

			}
		}

		if (iconName == null) return ImmutableList.of();

		return Lists.newArrayList(TextureDetails.createTextureDetails(iconName, col, 1));
	}

	@Nonnull
	private static List<TextureDetails> getIcons(Block block, int meta, ItemBlock itemBlock) {
		TextureDetails any = null;
		TextureDetails top = null;
		TextureDetails side = null;

		int col = itemBlock.getColorFromItemStack(new ItemStack(itemBlock, 1, meta), 0);

		TextureDetails[] icons = new TextureDetails[6];

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

			TextureDetails textureDetails = TextureDetails.createTextureDetails(iconName, col, 0);

			icons[s] = textureDetails;
			if (any == null) any = textureDetails;
			if (s < 2) {
				if (top == null) top = textureDetails;
			} else {
				if (side == null) side = textureDetails;
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
//						if (!(itemKey.item instanceof ToolShard))
							list.add(itemKey);

					}
				}
			}
		}

		return list;
	}

	@Override
	public void onResourceManagerReload(IResourceManager p_110549_1_) {
		cachedDetails = new HashMap<MultiKey.Tri<String, Integer, Integer>, TextureDetails>();
	}

	final static class TextureDetails {
		private final static String[] maps = new String[]{"textures/blocks", "textures/items"};

		String texture;
		int color = 0xFFFFFFFF;
		int type;
		TIntArrayList cols;

		private TextureDetails(String texture, int color, int type) {
			this.texture = texture;
			this.color = color;
			this.type = type;
		}

		public static TextureDetails createTextureDetails(String texture, int color, int type) {
			MultiKey.Tri<String, Integer, Integer> key = new MultiKey.Tri<String, Integer, Integer>(texture, color, type);
			TextureDetails textureDetails = cachedDetails.get(key);
			if (textureDetails == null) {
				textureDetails = new TextureDetails(texture, color, type);
				cachedDetails.put(key, textureDetails);
			}
			return textureDetails;
		}

		private static ResourceLocation completeResourceLocation(ResourceLocation location, String basePath) {
			return new ResourceLocation(location.getResourceDomain(), String.format("%s/%s%s", basePath, location.getResourcePath(), ".png"));
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			TextureDetails that = (TextureDetails) o;

			return color == that.color && type == that.type && texture.equals(that.texture);
		}

		@Override
		public int hashCode() {
			int result = texture.hashCode();
			result = 31 * result + color;
			result = 31 * result + type;
			return result;
		}

		public TIntArrayList gettIntArrayList(IResourceManager mgr) {
			if (cols != null)
				return cols;

			cols = new TIntArrayList();
			int mult = this.color & 0xFFFFFF;
			float mr = 1, mg = 1, mb = 1;

			boolean hasMultiplier = mult != 0xFFFFFF;
			if (hasMultiplier) {
				mr = ProcessedTexture.rgb.getRed(mult) / 255F;
				mg = ProcessedTexture.rgb.getGreen(mult) / 255F;
				mb = ProcessedTexture.rgb.getBlue(mult) / 255F;
			}

			ResourceLocation loc = new ResourceLocation(this.texture);


			IResource resource;
			try {
				resource = mgr.getResource(completeResourceLocation(loc, maps[this.type]));
			} catch (IOException err) {
				try {
					resource = mgr.getResource(completeResourceLocation(loc, maps[this.type ^ 1]));
				} catch (IOException err2) {
					resource = null;
				}
			}

			if (resource != null) {
				InputStream inputStream = null;

				try {
					inputStream = resource.getInputStream();
					BufferedImage img2 = ImageIO.read(inputStream);
					int w = img2.getWidth();
					int h = img2.getHeight();
					int[] colorData = new int[w * h];
					img2.getRGB(0, 0, w, h, colorData, 0, w);
					for (int col : colorData) {
						if (ProcessedTexture.rgb.getAlpha(col) > 10) {
							int val = ProcessedTexture.makeSolidAlpha(col);
							if (hasMultiplier) {
								int r = (int) (ProcessedTexture.rgb.getRed(col) * mr);
								int g = (int) (ProcessedTexture.rgb.getGreen(col) * mg);
								int b = (int) (ProcessedTexture.rgb.getBlue(col) * mb);
								val = 0xFF000000 | (r << 16) | (g << 8) | (b);
							}

							cols.add(val);
						}
					}
				} catch (IOException ignore) {

				} finally {
					if (inputStream != null)
						try {
							inputStream.close();
						} catch (IOException ignore) {

						}
				}
			}
			return cols;
		}
	}
}
