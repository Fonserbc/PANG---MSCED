package com.fonserbc.pang;

import java.text.DecimalFormat;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

public class Stats {
	private Preferences Prefs = Preferences.getInstance();
	
	private static final String TAG = MainActivity.class.getSimpleName();
	
    private DecimalFormat df = new DecimalFormat("0.##");
    private DecimalFormat dfCPU = new DecimalFormat("0.###");
	private final static int 	STAT_INTERVAL_FPS = 300;
	private final static int 	STAT_INTERVAL_CPU = 200;
	private final static int	FPS_HISTORY = 10;
	private final static int	CPU_HISTORY = 60;
	private final static int	FONT_SIZE = 20;
	
	private long FPSIntervalTimer	= 0l;
	private long CPUIntervalTimer	= 0l;
	
	private double 	tickStore[];
	private double 	averageFps = 0.0;
	
	private double cpuStore[];
	private double averageCpu = 0.0;
	
	private Timer timerFPS;
	private Timer timerCPU;
	
	private String fpsString;
	private String cpuString;
	
	private int it = 0;
	private int cpuIt = 0;
	
	private GameThread game;
	
	public Stats(GameThread thread) {
		game = thread;
		timerFPS = new Timer();
		timerFPS.setReal(true);
		timerCPU = new Timer();
		timerCPU.setReal(true);
		
		tickStore = new double[FPS_HISTORY];
		cpuStore = new double[CPU_HISTORY];
		for (int i = 0; i < FPS_HISTORY; i++)
			tickStore[i] = 0.0;
		for (int i = 0; i < CPU_HISTORY; i++)
			cpuStore[i] = 0.0;
		
		fpsString = "FPS:";
		cpuString = "CPU:";
	}
	
	public void updateFPS () {
		float deltaTime = timerFPS.tick();
		long deltaMs = timerFPS.getLastTickMs();
		
		tickStore[it] = deltaTime;
		it = (it+1)%FPS_HISTORY;

		FPSIntervalTimer += deltaMs;

		if (FPSIntervalTimer >= STAT_INTERVAL_FPS) {
			double totalTime = 0;
			for (int i = 0; i < FPS_HISTORY; ++i)
				totalTime += tickStore[i];
			
			averageFps = FPS_HISTORY/totalTime;
			
			fpsString = "FPS: " + df.format(averageFps);
			
			FPSIntervalTimer = 0;
		}
	}
	
	public void draw (Canvas canvas) {
		if (canvas != null) {
			Paint paint = new Paint();
			paint.setARGB(255, 255, 255, 255);
			paint.setTextSize(FONT_SIZE);
			if (Prefs.showFPS) canvas.drawText(fpsString+" / "+game.MAX_FPS, 20, 20, paint);
			if (Prefs.showCPU) canvas.drawText(cpuString, 20, 40, paint);
			if (Prefs.showBalls) {
				String ballsString;
				synchronized (game.balls) {
					ballsString = game.balls.size() + " balls";
				}
				canvas.drawText(ballsString, canvas.getWidth()-paint.measureText(ballsString)-20, 20, paint);
			}
		}
	}

	public void updateCPU(double cpuUsage) {
		timerCPU.tick();
		long deltaTime = timerCPU.getLastTickMs();
		
		cpuStore[cpuIt] = cpuUsage;
		cpuIt = (cpuIt+1)%CPU_HISTORY;
		
		CPUIntervalTimer += deltaTime;
		
		if (CPUIntervalTimer >= STAT_INTERVAL_CPU) {
			
			double cpuTime = 0;			
			for (int i = 0; i < CPU_HISTORY; i++)
				cpuTime += cpuStore[i];
			averageCpu = cpuTime/(double)CPU_HISTORY;
			
			cpuString = "CPU: " + dfCPU.format(averageCpu) + " %";
			
			CPUIntervalTimer = 0;
		}
	}	
}
