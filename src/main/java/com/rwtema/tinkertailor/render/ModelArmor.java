package com.rwtema.tinkertailor.render;

import com.rwtema.tinkertailor.render.textures.ArmorTextureManager;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import tconstruct.library.tools.DynamicToolPart;
import tconstruct.tools.TinkerTools;

public class ModelArmor extends ModelBiped {
	public int material;
	public ResourceLocation location;
	public final ArmorTextureManager manager;

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
		super.render(p_78088_1_, p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_);
	}


	public void setMaterial(int material) {
		this.material = material;
		location = manager.getArmorResource(material);
	}


}
