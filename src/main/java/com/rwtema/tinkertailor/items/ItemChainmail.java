package com.rwtema.tinkertailor.items;

import com.rwtema.tinkertailor.nbt.TinkersTailorConstants;
import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemChainmail extends Item {
	public ItemChainmail() {
		setHasSubtypes(true);
		setMaxDamage(0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void getSubItems(Item p_150895_1_, CreativeTabs p_150895_2_, List p_150895_3_) {
		for (int i = 0; i < ChainMailTypes.values().length * 2; i++) {
			p_150895_3_.add(new ItemStack(p_150895_1_, 1, i));
		}
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemStack) {
		int mat = itemStack.getItemDamage() >> 1;
		ChainMailTypes value = ChainMailTypes.getValue(mat);
		if ((itemStack.getItemDamage() & 1) == 0) {
			return value.toString() + " RING";
		} else {
			return value.toString() + " WEAVE";
		}
	}

	IIcon chainlink;
	IIcon chainweave1;
	IIcon chainweave2;

	@Override
	public void registerIcons(IIconRegister register) {
		chainlink = register.registerIcon(TinkersTailorConstants.RESOURCE_FOLDER + ":chainlink");
		chainweave1 = register.registerIcon(TinkersTailorConstants.RESOURCE_FOLDER + ":chainweave1");
		chainweave2 = register.registerIcon(TinkersTailorConstants.RESOURCE_FOLDER + ":chainweave2");
	}

	@Override
	public int getColorFromItemStack(ItemStack itemStack, int pass) {
		int mat = itemStack.getItemDamage() >> 1;
		ChainMailTypes value = ChainMailTypes.getValue(mat);
		if ((itemStack.getItemDamage() & 1) == 0) {
			return value.getCol3();
		} else {
			return pass == 0 ? value.getCol1() : value.getCol2();
		}
	}

	@Override
	public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining) {
		return getIconFromDamageForRenderPass(stack.getItemDamage(), renderPass);
	}

	@Override
	public IIcon getIcon(ItemStack stack, int pass) {
		return getIconFromDamageForRenderPass(stack.getItemDamage(), pass);
	}



	@Override
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	public IIcon getIconFromDamageForRenderPass(int p_77618_1_, int p_77618_2_) {
		if ((p_77618_1_ & 1) == 0)
			return chainlink;
		return p_77618_2_ == 0 ? chainweave1 : chainweave2;
	}

	public int getRenderPasses(int metadata) {
		return (metadata & 1) == 0 ? 1 : 2;
	}

	public enum ChainMailTypes {
		normal(0xC6C6C6, 0x6D6D6D),
		magic(0, 0) {
			@Override
			public int getCol1() {
				return 0x41F384;
			}

			@Override
			public int getCol2() {
				return 0x1CB52B;
			}

			@Override
			public int getCol3() {
				return super.getCol3();
			}
		},
		wither(0x595E5E, 0x333333);

		private final int col1;
		private final int col2;


		ChainMailTypes(int col1, int col2) {
			this.col1 = col1;
			this.col2 = col2;
		}

		public static int avg(int a, int b) {
			return ((a & 0xFEFEFE) >> 1) + ((b & 0xFEFEFE) >> 1) + (a & b & 0x010101);
		}

		public int getCol1() {
			return col1;
		}

		public int getCol2() {
			return col2;
		}

		public int getCol3() {
			return avg(getCol1(), getCol2());
		}

		public static ChainMailTypes getValue(int mat) {
			ChainMailTypes[] values = values();
			return values[mat % values.length];
		}
	}
}
