package com.rwtema.tinkertailor.render;

import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

public class ModelRendererCustom extends ModelRenderer {
	public final List<IRender> staticIconList = new ArrayList<IRender>();
	public final List<IRender> dynamicIconList = new ArrayList<IRender>();
	private boolean compiled = false;
	private int displayList = -1;

	public ModelRendererCustom(ModelBase p_i1173_1_) {
		super(p_i1173_1_, null);
		RendererHandler.rendererCustoms.add(this);
	}

	public ModelRendererCustom(ModelBase p_i1174_1_, int offsetX, int offsetY) {
		this(p_i1174_1_);
		this.setTextureOffset(offsetX, offsetY);
	}

	public ModelRendererCustom(ModelBase p_i1172_1_, String p_i1172_2_) {
		super(p_i1172_1_, p_i1172_2_);
		RendererHandler.rendererCustoms.add(this);
	}
	public ModelRendererCustom(ModelBase parent, ModelRenderer copy) {
		this(parent, copy.boxName);
		textureWidth = copy.textureWidth;
		textureHeight = copy.textureHeight;
		cubeList.addAll(copy.cubeList);
		setRotationPoint(copy.rotationPointX, copy.rotationPointY, copy.rotationPointZ);
		mirror = copy.mirror;
	}

	@Override
	public void render(float partialTicks) {
		if (this.isHidden || !this.showModel) {
			return;
		}

		if (!this.compiled) {
			this.createDisplayList(partialTicks);
		}

		GL11.glTranslatef(this.offsetX, this.offsetY, this.offsetZ);
		int i;

		if (this.rotateAngleX == 0.0F && this.rotateAngleY == 0.0F && this.rotateAngleZ == 0.0F) {
			if (this.rotationPointX == 0.0F && this.rotationPointY == 0.0F && this.rotationPointZ == 0.0F) {
				draw(partialTicks);

				if (this.childModels != null) {
					for (i = 0; i < this.childModels.size(); ++i) {
						((ModelRenderer) this.childModels.get(i)).render(partialTicks);
					}
				}
			} else {
				GL11.glTranslatef(this.rotationPointX * partialTicks, this.rotationPointY * partialTicks, this.rotationPointZ * partialTicks);
				draw(partialTicks);

				if (this.childModels != null) {
					for (i = 0; i < this.childModels.size(); ++i) {
						((ModelRenderer) this.childModels.get(i)).render(partialTicks);
					}
				}

				GL11.glTranslatef(-this.rotationPointX * partialTicks, -this.rotationPointY * partialTicks, -this.rotationPointZ * partialTicks);
			}
		} else {
			GL11.glPushMatrix();
			GL11.glTranslatef(this.rotationPointX * partialTicks, this.rotationPointY * partialTicks, this.rotationPointZ * partialTicks);

			if (this.rotateAngleZ != 0.0F) {
				GL11.glRotatef(this.rotateAngleZ * (180F / (float) Math.PI), 0.0F, 0.0F, 1.0F);
			}

			if (this.rotateAngleY != 0.0F) {
				GL11.glRotatef(this.rotateAngleY * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
			}

			if (this.rotateAngleX != 0.0F) {
				GL11.glRotatef(this.rotateAngleX * (180F / (float) Math.PI), 1.0F, 0.0F, 0.0F);
			}

			draw(partialTicks);

			if (this.childModels != null) {
				for (i = 0; i < this.childModels.size(); ++i) {
					((ModelRenderer) this.childModels.get(i)).render(partialTicks);
				}
			}

			GL11.glPopMatrix();
		}

		GL11.glTranslatef(-this.offsetX, -this.offsetY, -this.offsetZ);
	}

	@Override
	public void renderWithRotation(float partialTicks) {
		if (this.isHidden || !this.showModel) {
			return;
		}
		if (!this.compiled) {
			this.createDisplayList(partialTicks);
		}

		GL11.glPushMatrix();
		GL11.glTranslatef(this.rotationPointX * partialTicks, this.rotationPointY * partialTicks, this.rotationPointZ * partialTicks);

		if (this.rotateAngleY != 0.0F) {
			GL11.glRotatef(this.rotateAngleY * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
		}

		if (this.rotateAngleX != 0.0F) {
			GL11.glRotatef(this.rotateAngleX * (180F / (float) Math.PI), 1.0F, 0.0F, 0.0F);
		}

		if (this.rotateAngleZ != 0.0F) {
			GL11.glRotatef(this.rotateAngleZ * (180F / (float) Math.PI), 0.0F, 0.0F, 1.0F);
		}

		draw(partialTicks);
		GL11.glPopMatrix();
	}

	public void draw(float partialTicks) {
		if (displayList != -1) {
			GL11.glCallList(this.displayList);

			for (IRender textureQuadIcon : dynamicIconList) {
				textureQuadIcon.render(partialTicks);
			}
		}
	}

	@Override
	public void postRender(float partialTicks) {
		if (this.isHidden || !this.showModel) {
			return;
		}
		if (this.rotateAngleX == 0.0F && this.rotateAngleY == 0.0F && this.rotateAngleZ == 0.0F) {
			if (this.rotationPointX != 0.0F || this.rotationPointY != 0.0F || this.rotationPointZ != 0.0F) {
				GL11.glTranslatef(this.rotationPointX * partialTicks, this.rotationPointY * partialTicks, this.rotationPointZ * partialTicks);
			}
		} else {
			GL11.glTranslatef(this.rotationPointX * partialTicks, this.rotationPointY * partialTicks, this.rotationPointZ * partialTicks);

			if (this.rotateAngleZ != 0.0F) {
				GL11.glRotatef(this.rotateAngleZ * (180F / (float) Math.PI), 0.0F, 0.0F, 1.0F);
			}

			if (this.rotateAngleY != 0.0F) {
				GL11.glRotatef(this.rotateAngleY * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
			}

			if (this.rotateAngleX != 0.0F) {
				GL11.glRotatef(this.rotateAngleX * (180F / (float) Math.PI), 1.0F, 0.0F, 0.0F);
			}
		}
	}

	public void createDisplayList(float f) {
		this.displayList = GLAllocation.generateDisplayLists(1);
		GL11.glNewList(this.displayList, GL11.GL_COMPILE);
		Tessellator tessellator = Tessellator.instance;

		for (Object aCubeList : this.cubeList) {
			((ModelBox) aCubeList).render(tessellator, f);
		}

		for (IRender textureQuadIcon : staticIconList) {
			textureQuadIcon.render(f);
		}

		GL11.glEndList();
		this.compiled = true;
	}

	public void destroyDisplayList() {
		if (displayList != -1) {
			GLAllocation.deleteDisplayLists(this.displayList);
			this.compiled = false;
			this.displayList = -1;
		}
	}

	public ModelRendererCustom addDynamic(IRender texture) {
		dynamicIconList.add(texture);
		return this;
	}

	public IRender addStatic(IRender texture) {
		staticIconList.add(texture);
		return texture;
	}

	public void addSurroundingCube() {
		AxisAlignedBB bb = null;
		for (IRender textureQuadIcon : Iterables.concat(staticIconList, dynamicIconList)) {
			Vec3[] vectors = textureQuadIcon.vectors();
			if (vectors != null)
				for (Vec3 vector : vectors) {
					AxisAlignedBB boundingBox = AxisAlignedBB.getBoundingBox(vector.xCoord, vector.yCoord, vector.zCoord, vector.xCoord, vector.yCoord, vector.zCoord);
					if (bb == null) {
						bb = boundingBox;
					} else {
						bb = bb.func_111270_a(boundingBox);
					}
				}
		}

		if (bb == null) {
			return;
		}

		ModelBox modelBox = new ModelBoxNoRender(this, 0, 0,
				(float) bb.minX, (float) bb.minY, (float) bb.minZ,
				(int) (bb.maxX - bb.minX), (int) (bb.maxY - bb.minY), (int) (bb.maxZ - bb.minZ),
				0);


		this.cubeList.add(modelBox);

	}
}
