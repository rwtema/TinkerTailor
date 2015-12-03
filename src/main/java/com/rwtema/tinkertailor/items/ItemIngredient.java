package com.rwtema.tinkertailor.items;

import com.rwtema.tinkertailor.TinkersTailor;
import com.rwtema.tinkertailor.nbt.StringHelper;
import com.rwtema.tinkertailor.nbt.TinkersTailorConstants;
import com.rwtema.tinkertailor.utils.Lang;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemIngredient extends Item {

	private IIcon[] icons;
	private final String[] items;

	public ItemIngredient(String... items) {
		this.items = items;
		this.setMaxDamage(0);
		this.setHasSubtypes(true);

		if (TinkersTailor.deobf_folder) {
			for (int i = 0; i < items.length; i++) {
				Lang.translate(this.getUnlocalizedNameInefficiently(new ItemStack(this, 1, i)) + ".name", StringHelper.capFirst(items[i]));
			}
		}
	}

	@Override
	public int getMetadata(int p_77647_1_)
	{
		return p_77647_1_;
	}

	@Override
	public void registerIcons(IIconRegister p_94581_1_) {
		icons = new IIcon[items.length];
		for (int i = 0; i < items.length; i++) {
			icons[i] = p_94581_1_.registerIcon(TinkersTailorConstants.RESOURCE_FOLDER + ":" + "ingred_" + items[i]);
		}
	}

	@Override
	public IIcon getIconFromDamage(int p_77617_1_) {
		return icons[p_77617_1_ % items.length];
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "item.tinkerstailor.ingred." + items[stack.getItemDamage() % items.length];
	}


}
