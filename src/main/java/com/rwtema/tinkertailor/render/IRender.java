package com.rwtema.tinkertailor.render;

import net.minecraft.util.Vec3;

public interface IRender {
	void render(float f);

	Vec3[] vectors();

	void onReload();
}
