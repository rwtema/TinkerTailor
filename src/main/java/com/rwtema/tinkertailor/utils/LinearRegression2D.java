package com.rwtema.tinkertailor.utils;

public class LinearRegression2D {
	private int n = 0, Sx = 0, Sy = 0, Sxy = 0, Sx2 = 0;
	private float d = Float.NaN;

	public void addXY(int x, int y) {
		n++;
		Sx += x;
		Sy += y;
		Sxy += x * y;
		Sx2 += x * x;
		d = Float.NaN;
	}

	public float m() {
		if (Float.isNaN(d)) {
			d = 1.0F / (n * Sx2 - Sx * Sx);
		}

		return (n * Sxy - Sy * Sx) * d;
	}

	public float c() {
		if (Float.isNaN(d)) {
			d = 1.0F / (n * Sx2 - Sx * Sx);
		}

		return (Sy * Sx2 - Sxy * Sx) * d;
	}

}
