package com.rwtema.tinkertailor.render;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;

public class TextureQuadIcon implements IRender {
	public static final int defaultColor = 0xFFFFFF;
	private static final double[][] defaultUV = new double[][]{{0, 0}, {16, 0}, {16, 16}, {0, 16}};
	private final Vec3 crossProduct;
	private final String iconName;
	public Vec3[] vectors = new Vec3[4];
	public double[][] uv = defaultUV;
	public int color;

	public IIcon icon;

	public TextureQuadIcon(Vec3[] vectors, double[][] uv, String iconName) {
		this.vectors = vectors;
		this.iconName = iconName;
		if (uv != null) this.uv = uv;
		crossProduct = (this.vectors[1].subtract(vectors[2])).crossProduct(this.vectors[1].subtract(vectors[0])).normalize();
		if (iconName != null) {
			if (!RendererHandler.iconHashMap.containsKey(iconName))
				RendererHandler.iconHashMap.put(iconName, RendererHandler.registerIcon(iconName));
			else
				icon = RendererHandler.iconHashMap.get(iconName);
		}
		color = defaultColor;
	}

	public TextureQuadIcon(Vec3[] vectors, String iconName) {
		this(vectors, null, iconName);
	}

	public TextureQuadIcon(Vec3[] vectors) {
		this(vectors, null, null);
	}

	public static Vec3[] getSideVecs(float ox, float oy, float oz, float w, float h, float d, float scale, int side, boolean mirror) {
		return getBoxVecs(ox, oy, oz, w, h, d, scale, mirror)[side];
	}

	public static Vec3[][] getBoxVecs(float ox, float oy, float oz, float w, float h, float d, float scale, boolean mirror) {
		Vec3[][] quadList = new Vec3[6][];
		float ux = ox + w;
		float uy = oy + h;
		float uz = oz + d;
		ox -= scale;
		oy -= scale;
		oz -= scale;
		ux += scale;
		uy += scale;
		uz += scale;

		if (mirror) {
			float temp = ux;
			ux = ox;
			ox = temp;
		}


		Vec3 v00 = Vec3.createVectorHelper(ux, oy, oz);
		Vec3 v01 = Vec3.createVectorHelper(ox, oy, oz);
		Vec3 v02 = Vec3.createVectorHelper(ox, uy, oz);
		Vec3 v03 = Vec3.createVectorHelper(ux, uy, oz);

		Vec3 v10 = Vec3.createVectorHelper(ox, oy, uz);
		Vec3 v11 = Vec3.createVectorHelper(ux, oy, uz);
		Vec3 v12 = Vec3.createVectorHelper(ux, uy, uz);
		Vec3 v13 = Vec3.createVectorHelper(ox, uy, uz);

		quadList[0] = new Vec3[]{v11, v00, v03, v12};
		quadList[1] = new Vec3[]{v01, v10, v13, v02};
		quadList[2] = new Vec3[]{v11, v10, v01, v00};
		quadList[3] = new Vec3[]{v03, v02, v13, v12};
		quadList[4] = new Vec3[]{v00, v01, v02, v03};
		quadList[5] = new Vec3[]{v10, v11, v12, v13};

		return quadList;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public void render(float partialTicks) {
		Tessellator tes = Tessellator.instance;
		if (icon == null) {
			if (iconName == null)
				return;
			onReload();
			if (icon == null) {
				return;
			}
		}

		tes.startDrawingQuads();
		tes.setColorOpaque_I(color);
		tes.setNormal((float) -crossProduct.xCoord, (float) -crossProduct.yCoord, (float) -crossProduct.zCoord);
		for (int i = 0; i < 4; i++) {
			addVertex(tes, partialTicks, i);
		}

		tes.draw();

	}

	@Override
	public Vec3[] vectors() {
		return vectors;
	}

	public void addVertex(Tessellator tes, float s, int i) {
		tes.addVertexWithUV(
				vectors[i].xCoord * s,
				vectors[i].yCoord * s,
				vectors[i].zCoord * s,
				icon.getInterpolatedU(uv[i][0]), icon.getInterpolatedV(uv[i][1]));
	}

	public void onReload() {
		if (iconName != null) {
			icon = RendererHandler.iconHashMap.get(iconName);
		}
	}

	public TextureQuadIcon setIcon(IIcon icon) {
		this.icon = icon;
		return this;
	}

	public double[][] getScaledUV(Vec3[] vecs, int scale) {
		double[][] d = new double[4][2];

		return d;
	}

}
