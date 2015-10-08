package com.rwtema.tinkertailor.modifiers;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.particle.EntitySpellParticleFX;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

public class PotionParticlesEventHandler {

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void suppressParticles(EntityJoinWorldEvent event) {
		if (event.entity.getClass() != EntitySpellParticleFX.class) {
			return;
		}

		EntitySpellParticleFX particle = (EntitySpellParticleFX) event.entity;
	}
}
