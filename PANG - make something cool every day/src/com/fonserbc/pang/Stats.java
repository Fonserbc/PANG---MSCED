package com.fonserbc.pang;

import java.text.DecimalFormat;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Stats {
	
    private DecimalFormat df = new DecimalFormat("0.##");
	private final static int 	STAT_INTERVAL = 500;
	private final static int	FPS_HISTORY_NR = 32;
	private final static int	FONT_SIZE = 20;
	
	private long statusIntervalTimer	= 0l;
	
	private double 	tickStore[];
	private double 	averageFps = 0.0;
	
	private Timer timer;
	
	private String fpsString;
	
	private int it = 0;
	
	private MainThread game;
	
	public Stats(MainThread thread) {
		game = thread;
		timer = new Timer();
		timer.setReal(true);
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
			paint.setTextSize(FONT_SIZE);
			canvas.drawText(fpsString+" / "+game.MAX_FPS, 20, 20, paint);
			String ballsString;
			synchronized (game.balls) {
				ballsString = game.balls.size() + " balls";
			}
			canvas.drawText(ballsString, canvas.getWidth()-paint.measureText(ballsString)-20, 20, paint);
			//String gravityStr = "("+df.format(game.gravity.x)+", "+df.format(game.gravity.y)+")";
			//canvas.drawText(gravityStr, 20, 40, paint);
		}
	}
	
}
