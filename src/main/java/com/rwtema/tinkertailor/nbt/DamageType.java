package com.rwtema.tinkertailor.nbt;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

public enum DamageType {
	normal(new DamageSource("normal"), 8, new ItemStack(Items.iron_ingot), EnumChatFormatting.GRAY) {
		@Override
		public boolean handles(DamageSource source) {
			return !source.isMagicDamage();
		}
	},

	explosion((new DamageSource("explosion")).setDifficultyScaled().setExplosion(), 5, new ItemStack(Items.gunpowder), EnumChatFormatting.YELLOW) {
		@Override
		public boolean handles(DamageSource source) {
			return  source.isExplosion();
		}
	},

	projectile((new DamageSource("projectile")).setProjectile(), 5, new ItemStack(Items.slime_ball, 1 , Short.MAX_VALUE), EnumChatFormatting.GREEN) {
		@Override
		public boolean handles(DamageSource source) {
			return  source.isProjectile();
		}
	},

	magic(DamageSource.magic, 8, new ItemStack(Items.diamond), EnumChatFormatting.BLUE) {
		@Override
		public boolean handles(DamageSource source) {
			return source.isMagicDamage() && !(source == DamageSource.wither || "wither".equals(source.damageType));
		}
	},

	fire((new DamageSource("fire")).setFireDamage(), 4, new ItemStack(Items.netherbrick), EnumChatFormatting.RED) {
		@Override
		public boolean handles(DamageSource source) {
			return source.isFireDamage();
		}
	},

	wither(DamageSource.wither, 4, new ItemStack(Items.skull, 1, Short.MAX_VALUE), EnumChatFormatting.DARK_GRAY) {
		@Override
		public boolean handles(DamageSource source) {
			return source.isMagicDamage() && (source == DamageSource.wither || "wither".equals(source.damageType));
		}
	},

	fall(DamageSource.fall, 1, new ItemStack(Items.feather), EnumChatFormatting.AQUA) {
		@Override
		public boolean handles(DamageSource source) {
			return source == DamageSource.fall || "fall".equals(source.damageType);
		}

		@Override
		public int allowedArmorTypes() {
			return 7;
		}
	},
	anvil(DamageSource.anvil, 1, new ItemStack(Blocks.wool, 1, Short.MAX_VALUE), EnumChatFormatting.BLUE){
		@Override
		public boolean handles(DamageSource source) {
			return source == DamageSource.anvil || source == DamageSource.fallingBlock;
		}

		@Override
		public int allowedArmorTypes() {
			return 14;
		}
	}
;
	public final DamageSource damageSource;
	public final int maxLevel;
	public final ItemStack itemStack;
	public final EnumChatFormatting color;
	public final String name;


	DamageType(DamageSource damageSource, int factor, ItemStack itemStack, EnumChatFormatting color) {
		this.damageSource = damageSource;
		this.maxLevel = factor;
		this.itemStack = itemStack;
		this.color = color;
		this.name = damageSource.damageType;
	}

	public String getUnlocalizedName(){
		return "damage."+ name + ".name";
	}

	public String getLocalizedName(){
		if(StatCollector.canTranslate(getUnlocalizedName())){
			return StatCollector.translateToLocal(getUnlocalizedName());
		}else{
			return name.substring(0, 1).toUpperCase() + name.substring(1);
		}
	}


	public abstract boolean handles(DamageSource source);

	public int allowedArmorTypes() {
		return 0;
	}
}
