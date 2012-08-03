package com.fonserbc.pang;

import java.text.DecimalFormat;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Stats {
	
    private DecimalFormat df = new DecimalFormat("0.##");
	private final static int 	STAT_INTERVAL = 300;
	private final static int	FPS_HISTORY_NR = 10;
	private final static int	FONT_SIZE = 20;
	
	private long statusIntervalTimer	= 0l;
	
	private double 	tickStore[];
	private double 	averageFps = 0.0;
	
	private long cpuStore[];
	private double averageCpu = 0.0;
	
	private Timer timer;
	
	private String fpsString;
	private String cpuString;
	
	private int it = 0;
	
	private MainThread game;
	
	public Stats(MainThread thread) {
		game = thread;
		timer = new Timer();
		timer.setReal(true);
		tickStore = new double[FPS_HISTORY_NR];
		cpuStore = new long[FPS_HISTORY_NR];
		for (int i = 0; i < FPS_HISTORY_NR; i++) {
			tickStore[i] = 0.0;
			cpuStore[i] = 0;
		}
	}
	
	public void update (long workingTime) {
		float deltaTime = timer.tick();
		long deltaMs = timer.getLastTickMs();
		
		tickStore[it] = deltaTime;
		cpuStore[it] = workingTime;
		it = (it+1)%FPS_HISTORY_NR;

		statusIntervalTimer += deltaMs;

		if (statusIntervalTimer >= STAT_INTERVAL) {
			
			double totalTime = 0;
			long cpuTime = 0;
			for (int i = 0; i < FPS_HISTORY_NR; ++i) {
				totalTime += tickStore[i];
				cpuTime += cpuStore[i];
			}
			
			averageFps = FPS_HISTORY_NR/totalTime;
			averageCpu = ((double)cpuTime*100d)/(double)statusIntervalTimer;
			
			fpsString = "FPS: " + df.format(averageFps);
			cpuString = "CPU: " + df.format(averageCpu) + " %";
			
			statusIntervalTimer = 0;
		}
	}
	
	public void draw (Canvas canvas) {
		if (canvas != null && fpsString != null && cpuString != null) {
			Paint paint = new Paint();
			paint.setARGB(255, 255, 255, 255);
			paint.setTextSize(FONT_SIZE);
			canvas.drawText(fpsString+" / "+game.MAX_FPS, 20, 20, paint);
			canvas.drawText(cpuString, 20, 40, paint);
			String ballsString;
			synchronized (game.balls) {
				ballsString = game.balls.size() + " balls";
			}
			canvas.drawText(ballsString, canvas.getWidth()-paint.measureText(ballsString)-20, 20, paint);
		}
	}
	
}
