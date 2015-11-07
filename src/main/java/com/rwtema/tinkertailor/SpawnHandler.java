package com.rwtema.tinkertailor;

import com.rwtema.tinkertailor.items.ArmorCore;
import com.rwtema.tinkertailor.modifiers.ModifierInstance;
import com.rwtema.tinkertailor.modifiers.ModifierRegistry;
import com.rwtema.tinkertailor.nbt.TinkersTailorConstants;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import java.util.Random;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.item.ItemStack;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;

public class SpawnHandler {

	@SubscribeEvent
	public void spawn(LivingSpawnEvent.SpecialSpawn event) {
		EntityLivingBase entityLiving = event.entityLiving;
		if (shouldAddArmor(entityLiving)) {
			Random random = TinkersTailorConstants.RANDOM;
			if (random.nextFloat() < 20.15F * entityLiving.worldObj.func_147462_b(entityLiving.posX, entityLiving.posY, entityLiving.posZ)) {
				DamageEventHandler.getOrderedMaterialList();

				float f = entityLiving.worldObj.difficultySetting == EnumDifficulty.HARD ? 0.1F : 0.25F;

				int mat = DamageEventHandler.getRandomMaterial(random);


				for (int j = 4; j >= 1; --j) {
					ItemStack itemstack = entityLiving.getEquipmentInSlot(j);

					if (j < 4 && random.nextFloat() < f) {
						break;
					}

					if (itemstack == null) {
						ArmorCore armor = ArmorCore.armors[4 - j];
						itemstack = armor.createDefaultStack(mat);
						armor.addRandomModifiers(itemstack, 2, 2, TinkersTailorConstants.RANDOM);

						if (entityLiving instanceof EntitySkeleton && ((EntitySkeleton) entityLiving).getSkeletonType() == 1) {
							ModifierInstance.addModLevel(itemstack, ModifierRegistry.wither, ModifierRegistry.wither.getModifierStep());
						}

						entityLiving.setCurrentItemOrArmor(j, itemstack);

						((EntityLiving) entityLiving).setEquipmentDropChance(j, 0.05F);
					}
				}
			}
		}
	}

	private boolean shouldAddArmor(EntityLivingBase entityLiving) {
		return (entityLiving instanceof EntityZombie && !(entityLiving instanceof EntityPigZombie)) || entityLiving instanceof EntitySkeleton;
	}

}
