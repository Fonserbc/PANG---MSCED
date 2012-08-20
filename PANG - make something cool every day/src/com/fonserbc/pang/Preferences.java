package com.fonserbc.pang;

public class Preferences {
	private static Preferences instance = new Preferences();
	
	private Preferences() {}
	
	public static Preferences getInstance() {
		return instance;
	}
	
	public int FPS = 60;
	public boolean showFPS = false;
	public boolean showCPU = false;
	public boolean showBalls = false;
	public boolean useSensor = false;
}
