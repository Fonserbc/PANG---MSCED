package com.fonserbc.pang;

import android.content.Context;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

	private static final String TAG = GameView.class.getSimpleName();
	private GameThread thread;
	
	public GameView(Context context) {
		super(context);
		getHolder().addCallback(this);
		
		setFocusable(true);
	}

	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		thread = new GameThread(getHolder(), this);
		
		thread.setRunning(true);
		thread.start();
	}

	public void surfaceCreated(SurfaceHolder arg0) {
	
	}
	
	public void surfaceDestroyed(SurfaceHolder arg0) {
		boolean retry = true;
		while (retry) {
			try {
				thread.join();
				retry = false;
			} catch (InterruptedException e) {
				// try again shutting down the thread
			}
		}
	}
	
	public GameThread getThread() {
		return thread;
	}

	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			thread.onClick(new Vector2f(event.getX(), event.getY()));
		}
		return super.onTouchEvent(event);
	}
}
