package com.fonserbc.pang;

import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class WelcomeView extends SurfaceView implements SurfaceHolder.Callback {
	private WelcomeThread thread;
	
	private MainActivity activity;
	
	public WelcomeView (MainActivity activity) {
		super(activity);
		getHolder().addCallback(this);
		
		this.activity = activity;
		thread = new WelcomeThread(getHolder(), this);
		thread.setRunning(false);
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	public void surfaceCreated(SurfaceHolder holder) {
		thread.setRunning(true);
		thread.start();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
				
	}
	
	public WelcomeThread getThread() {
		return thread;
	}
	
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			thread.setRunning(false);
			activity.startGame();
		}
		return super.onTouchEvent(event);
	}
	
	public void resume() {
		thread.setHolder(getHolder());
		thread.setRunning(true);
	}
}
