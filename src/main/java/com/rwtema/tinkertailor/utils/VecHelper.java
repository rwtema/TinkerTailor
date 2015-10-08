package com.rwtema.tinkertailor.utils;

import net.minecraft.util.Vec3;

public class VecHelper {


	public static Vec3 add(Vec3 a, Vec3... bs) {
		for (Vec3 b : bs)
			a = a.addVector(b.xCoord, b.yCoord, b.zCoord);
		return a;
	}

	public static Vec3 mult(Vec3 v, double m) {
		return Vec3.createVectorHelper(v.xCoord * m, v.yCoord * m, v.zCoord * m);
	}
}
