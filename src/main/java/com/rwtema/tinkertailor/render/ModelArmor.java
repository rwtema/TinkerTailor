package com.rwtema.tinkertailor.render;

import com.rwtema.tinkertailor.render.textures.ArmorTextureManager;
import java.util.WeakHashMap;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import tconstruct.library.tools.DynamicToolPart;
import tconstruct.tools.TinkerTools;

public class ModelArmor extends ModelBiped {
	public final ArmorTextureManager manager;
	public int material;
	public ResourceLocation location;
	double invis = 0;
	private WeakHashMap<Entity, Double> invisCache = new WeakHashMap<Entity, Double>();


	public ModelArmor(int armorSlot) {
		super(0.5F, 0, 32, 32);

		manager = ArmorTextureManager.getManager(armorSlot);

		this.boxList.clear();

		ModelRendererNull rendererNull = new ModelRendererNull(this);

		bipedHead = bipedHeadwear = bipedBody = bipedRightArm = bipedLeftArm = bipedRightLeg = bipedLeftLeg = bipedEars = bipedCloak = rendererNull;

		this.textureWidth = 32;
		this.textureHeight = 32;

		float scale = armorSlot == 1 ? 1.0F : 0.5F;

		if (armorSlot == 0) {
			this.textureWidth = 32;
			this.textureHeight = 16;
			this.bipedHead = new ModelRenderArmor(this, 0, 0);
			this.bipedHead.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, scale);
			this.bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
		}

		if (armorSlot == 1) {

			this.bipedBody = new ModelRenderArmor(this, 0, 0);
			this.bipedBody.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, scale);
			this.bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);

			scale += 0.01F;
			this.bipedRightArm = new ModelRenderArmor(this, 0, 16);
			this.bipedRightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, scale);
			this.bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
			this.bipedLeftArm = new ModelRenderArmor(this, 0, 16);
			this.bipedLeftArm.mirror = true;
			this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, scale);
			this.bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);

			float v = 4.5F;

			((ModelRenderArmor) bipedBody).addDynamic(
					new TextureHeavyPlate(TextureQuadIcon.getSideVecs(-v / 2, 6 - 0.25F - v / 2, -2.001F, v, v, 0, 1.0F, 4, !bipedBody.mirror),
							this, (DynamicToolPart) TinkerTools.largePlate)
			).addDynamic(
					new TextureHeavyPlate(TextureQuadIcon.getSideVecs(-v / 2, 6 - 0.25F - v / 2, 2.001F, v, v, 0, 1.0F, 5, !bipedBody.mirror),
							this, (DynamicToolPart) TinkerTools.largePlate)
			);
		}

		if (armorSlot == 2) {
			this.bipedBody = new ModelRenderArmor(this, 0, 16);
			this.bipedBody.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, scale);
			this.bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);

			this.bipedRightLeg = new ModelRenderArmor(this, 0, 0);
			this.bipedRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, scale);
			this.bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);

			this.bipedLeftLeg = new ModelRenderArmor(this, 0, 0);
			this.bipedLeftLeg.mirror = true;
			this.bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, scale);
			this.bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
		}

		if (armorSlot == 3) {
			scale = 1;
			this.textureWidth = 16;
			this.textureHeight = 16;
			this.bipedRightLeg = new ModelRenderArmor(this, 0, 0);
			this.bipedRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, scale);
			this.bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
			this.bipedLeftLeg = new ModelRenderArmor(this, 0, 0);
			this.bipedLeftLeg.mirror = true;
			this.bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, scale);
			this.bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
		}
	}

	@Override
	public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_) {

		if (invis == 1) return;
		if (invis != 0) {
			GL11.glEnable(GL11.GL_BLEND);
			OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
			GL11.glColor4d(1, 1, 1, 1 - invis);
		}

		super.render(p_78088_1_, p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_);
		if (invis != 0) {
			GL11.glDisable(GL11.GL_BLEND);
		}
	}

	public void setMaterial(int material) {
		this.material = material;
		location = manager.getArmorResource(material);
	}

	public void setInvisible(EntityLivingBase entityLiving, boolean invisible) {
		if (invisible) {
			invis = 1;
			invisCache.put(entityLiving, 1.0);
		} else {
			invis = 0;
			invisCache.put(entityLiving, 0.0);
		}
	}

	public void stepInvisible(EntityLivingBase entityLiving, boolean invisible) {
		if (!invisCache.containsKey(entityLiving)) {
			if (invisible) {
				invisCache.put(entityLiving, 1.0);
			} else {
				invisCache.put(entityLiving, 0.0);
			}
		}

		double d = invisCache.get(entityLiving);
		double step = 0.05;
		if (invisible) {

			if (d == 1) {
				invis = 1;
				return;
			}
			d = d + step;
			if (d > 1) d = 1;
			invisCache.put(entityLiving, d);
		} else {
			if (d == 0) {
				invis = 0;
				return;
			}
			d = d - step;
			if (d <= 0) {
				invisCache.put(entityLiving, 0.0);
			} else
				invisCache.put(entityLiving, d);
		}
		invis = d;
	}
}
