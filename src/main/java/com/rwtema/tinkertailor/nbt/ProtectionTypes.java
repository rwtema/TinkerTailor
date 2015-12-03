package com.rwtema.tinkertailor.nbt;

import com.google.common.collect.Lists;
import com.rwtema.tinkertailor.modifiers.Modifier;
import com.rwtema.tinkertailor.modifiers.ModifierProtection;
import com.rwtema.tinkertailor.utils.oremapping.ItemValueMap;
import com.rwtema.tinkertailor.utils.oremapping.OreIntMap;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;

public enum ProtectionTypes {
	normal(new DamageSource("normal"), 8, "ingotIron", EnumChatFormatting.GRAY) {
		@Override
		public boolean handles(DamageSource source) {
			return !source.isMagicDamage();
		}

		@Override
		public ItemValueMap getOreValues() {
			return super.getOreValues().put("blockIron", 9);
		}
	},

	explosion((new DamageSource("explosion")).setDifficultyScaled().setExplosion(), 3, Lists.newArrayList(Items.gunpowder, "dustGunpowder"), EnumChatFormatting.YELLOW) {
		@Override
		public boolean handles(DamageSource source) {
			return source.isExplosion();
		}
	},

	projectile((new DamageSource("projectile")).setProjectile(), 3, "logWood", EnumChatFormatting.GREEN) {
		@Override
		public boolean handles(DamageSource source) {
			return source.isProjectile();
		}

		@Override
		public int modifierStep() {
			return 64;
		}
	},

	magic(DamageSource.magic, 8, "gemDiamond", EnumChatFormatting.BLUE) {
		@Override
		public boolean handles(DamageSource source) {
			return source.isMagicDamage() || (source == DamageSource.wither || "wither".equals(source.damageType));
		}

		@Override
		public ItemValueMap getOreValues() {
			return super.getOreValues().put("blockDiamond", 9);
		}
	},

	fire((new DamageSource("fire")).setFireDamage(), 3, "ingotBrickNether", EnumChatFormatting.RED) {
		@Override
		public boolean handles(DamageSource source) {
			return source.isFireDamage();
		}

		@Override
		public ItemValueMap getOreValues() {
			return super.getOreValues().put(Blocks.nether_brick, 4).put("blockBrickNether", 4);
		}
	},

	wither(DamageSource.wither, 4, Items.skull, EnumChatFormatting.DARK_GRAY) {
		@Override
		public boolean handles(DamageSource source) {
			return source.isMagicDamage() && (source == DamageSource.wither || "wither".equals(source.damageType));
		}
	},

	fall(DamageSource.fall, 1, Items.feather, EnumChatFormatting.AQUA) {
		@Override
		public boolean handles(DamageSource source) {
			return source == DamageSource.fall || "fall".equals(source.damageType);
		}

		@Override
		public int allowedArmorTypes() {
			return Modifier.ARMORTYPE_SHOES_ONLY;
		}
	},
	anvil(DamageSource.anvil, 1, Lists.newArrayList(Blocks.wool, "blockWool"), EnumChatFormatting.BLUE) {
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
	public ModifierProtection modifier;


	ProtectionTypes(DamageSource damageSource, int factor, Object itemStack, EnumChatFormatting color) {
		this.damageSource = damageSource;
		this.maxLevel = factor;
		this.itemStack = itemStack;
		this.color = color;
		this.name = toString();
	}


	public abstract boolean handles(DamageSource source);

	public int allowedArmorTypes() {
		return 0;
	}

	public ItemValueMap getOreValues() {
		ItemValueMap map = new OreIntMap();
		map.put(itemStack, 1);
		return map;
	}

	public int modifierStep() {
		return 16;
	}

	public ModifierProtection createModifier() {
		return (modifier = (ModifierProtection) new ModifierProtection(this).setNegativeMaloderous());
	}
}
