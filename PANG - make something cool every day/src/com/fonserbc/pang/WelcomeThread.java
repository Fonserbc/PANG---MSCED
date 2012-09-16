package com.fonserbc.pang;

import android.R.color;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;

public class WelcomeThread extends Thread {

	private static final String TITLE = "PANG!";
	private static final String FLASH = "Tap to start";
	private static final float TITLE_SIZE = 100f;
	private static final float FLASH_SIZE = 20f;
	
	private static final float FLASH_RATE = 1f;
	
	private WelcomeView view;
	private SurfaceHolder surfaceHolder;
	private Timer timer;
	private Paint titlePaint;
	private Paint flashPaint;
	
	private boolean running = false;
	
	
	
	public WelcomeThread (SurfaceHolder holder, WelcomeView view) {
		super();
		
		this.view = view;
		this.surfaceHolder = holder;
		timer =  new Timer();
	}

	public void setRunning(boolean b) {
		running = b;
	}
	
	@Override
	public void run() {
		Canvas canvas = null;
		
		titlePaint = new Paint();
		titlePaint.setColor(Color.BLUE);
		titlePaint.setTextSize(TITLE_SIZE);
		float titleSize = titlePaint.measureText(TITLE);
		
		flashPaint = new Paint();
		flashPaint.setColor(Color.WHITE);
		flashPaint.setTextSize(FLASH_SIZE);
		float flashSize = flashPaint.measureText(FLASH);
		long flashTime = 0;
		long timeBetweenFlashes = (long) (1000/FLASH_RATE);
		boolean flash = false;
		
		while (!this.isInterrupted()) {
			if (running) {
				try {
					canvas = surfaceHolder.lockCanvas();
					synchronized (surfaceHolder) {
						//UPDATE
						timer.tick();
						flashTime += timer.getLastTickMs();
						
						if (flashTime > timeBetweenFlashes) {
							flashTime = 0;
							
							flash = !flash;
						}
						
						//DRAW
						canvas.drawColor(Color.BLACK);
						canvas.drawText(TITLE, view.getWidth()/2-titleSize/2, view.getHeight()/2-titlePaint.getTextSize()/2, titlePaint);
						if (flash) canvas.drawText(FLASH, view.getWidth()/2-flashSize/2, view.getHeight()/2+titlePaint.getTextSize()/2+10, flashPaint);
					}
				}
				finally {
					if (canvas != null) {
						surfaceHolder.unlockCanvasAndPost(canvas);
					}
				}
			}
			else try { Thread.sleep(100); } catch (InterruptedException ie) {}
		}
	}

	public void setHolder(SurfaceHolder holder) {
		surfaceHolder = holder;		
	}
}
