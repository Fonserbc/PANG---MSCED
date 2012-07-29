package com.fonserbc.pang;

import java.text.DecimalFormat;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Stats {
	
    private DecimalFormat df = new DecimalFormat("0.##");
	private final static int 	STAT_INTERVAL = 500;
	private final static int	FPS_HISTORY_NR = 32;
	
	private long statusIntervalTimer	= 0l;
	
	private double 	tickStore[];
	private double 	averageFps = 0.0;
	
	private Timer timer;
	
	private String fpsString;
	
	private int it = 0;
	
	public Stats() {
		timer = new Timer();
		tickStore = new double[FPS_HISTORY_NR];
		for (int i = 0; i < FPS_HISTORY_NR; i++) {
			tickStore[i] = 0.0;
		}
	}
	
	public void update() {
		float deltaTime = timer.tick();
		long deltaMs = timer.getLastTickMs();
		
		tickStore[it] = deltaTime;
		it = (it+1)%FPS_HISTORY_NR;

		statusIntervalTimer += deltaMs;

		if (statusIntervalTimer >= STAT_INTERVAL) {
			statusIntervalTimer = 0;
			
			float totalTime = 0;
			for (int i = 0; i < FPS_HISTORY_NR; ++i) {
				totalTime += tickStore[i];
			}
			
			averageFps = FPS_HISTORY_NR/totalTime;
			
			fpsString = "FPS: " + df.format(averageFps);
		}
	}
	
	public void draw (Canvas canvas) {
		if (canvas != null && fpsString != null) {
			Paint paint = new Paint();
			paint.setARGB(255, 255, 255, 255);
			canvas.drawText(fpsString, 20, 20, paint);
		}
	}
	
}
