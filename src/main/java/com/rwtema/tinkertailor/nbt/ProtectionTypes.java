package com.rwtema.tinkertailor.nbt;

import com.google.common.collect.Lists;
import com.rwtema.tinkertailor.modifiers.Modifier;
import com.rwtema.tinkertailor.utils.oremapping.OreIntMap;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;

public enum ProtectionTypes {
	normal(new DamageSource("normal"), 12, "ingotIron", EnumChatFormatting.GRAY) {
		@Override
		public boolean handles(DamageSource source) {
			return !source.isMagicDamage();
		}

		@Override
		public OreIntMap getOreValues() {
			return super.getOreValues().put("blockIron", 9);
		}
	},

	explosion((new DamageSource("explosion")).setDifficultyScaled().setExplosion(), 6, Lists.newArrayList(Items.gunpowder, "dustGunpowder"), EnumChatFormatting.YELLOW) {
		@Override
		public boolean handles(DamageSource source) {
			return source.isExplosion();
		}
	},

	projectile((new DamageSource("projectile")).setProjectile(), 6, "logWood", EnumChatFormatting.GREEN) {
		@Override
		public boolean handles(DamageSource source) {
			return source.isProjectile();
		}

		@Override
		public int modifierStep() {
			return 64;
		}
	},

	magic(DamageSource.magic, 20, "gemDiamond", EnumChatFormatting.BLUE) {
		@Override
		public boolean handles(DamageSource source) {
			return source.isMagicDamage() || (source == DamageSource.wither || "wither".equals(source.damageType));
		}

		@Override
		public OreIntMap getOreValues() {
			return super.getOreValues().put("blockDiamond", 9);
		}
	},

	fire((new DamageSource("fire")).setFireDamage(), 6, "ingotBrickNether", EnumChatFormatting.RED) {
		@Override
		public boolean handles(DamageSource source) {
			return source.isFireDamage();
		}

		@Override
		public OreIntMap getOreValues() {
			return super.getOreValues().put(Blocks.nether_brick, 4).put("blockBrickNether", 4);
		}
	},

	wither(DamageSource.wither, 8, Items.skull, EnumChatFormatting.DARK_GRAY) {
		@Override
		public boolean handles(DamageSource source) {
			return source.isMagicDamage() && (source == DamageSource.wither || "wither".equals(source.damageType));
		}
	},

	fall(DamageSource.fall, 4, Items.feather, EnumChatFormatting.AQUA) {
		@Override
		public boolean handles(DamageSource source) {
			return source == DamageSource.fall || "fall".equals(source.damageType);
		}

		@Override
		public int allowedArmorTypes() {
			return Modifier.ARMORTYPE_SHOES_ONLY;
		}
	},
	anvil(DamageSource.anvil, 3, Lists.newArrayList(Blocks.wool, "blockWool"), EnumChatFormatting.BLUE) {
		@Override
		public boolean handles(DamageSource source) {
			return source == DamageSource.anvil || source == DamageSource.fallingBlock;
		}

		@Override
		public int allowedArmorTypes() {
			return Modifier.ARMORTYPE_HAT_ONLY;
		}
	};
	public final DamageSource damageSource;
	public final int maxLevel;
	public final Object itemStack;
	public final EnumChatFormatting color;
	public final String name;


	ProtectionTypes(DamageSource damageSource, int factor, Object itemStack, EnumChatFormatting color) {
		this.damageSource = damageSource;
		this.maxLevel = factor;
		this.itemStack = itemStack;
		this.color = color;
		this.name = damageSource.damageType;
	}


	public abstract boolean handles(DamageSource source);

	public int allowedArmorTypes() {
		return 0;
	}

	public OreIntMap getOreValues() {
		OreIntMap map = new OreIntMap();
		map.put(itemStack, 1);
		return map;
	}

	public int modifierStep() {
		return 16;
	}
}
