package com.fonserbc.pang;

import android.util.FloatMath;

public class Vector2f {
	public float x;
	public float y;
	
	public Vector2f () {
		x = 0;
		y = 0;
	}
	
	public Vector2f (float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public float magnitude() {
		return FloatMath.sqrt((x*x + y*y));
	}
}
