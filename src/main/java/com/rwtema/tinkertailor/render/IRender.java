package com.rwtema.tinkertailor.render;

import net.minecraft.util.Vec3;

public interface IRender {
	public void render(float f);

	public Vec3[] vectors();

	void onReload();
}
