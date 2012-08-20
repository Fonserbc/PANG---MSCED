package com.fonserbc.pang;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

	private static final String TAG = GameView.class.getSimpleName();
	private GameThread thread;
	
	public GameView(Context context) {
		super(context);getHolder().addCallback(this);
		
		thread = new GameThread(getHolder(), this);
		
		setFocusable(true);
	}

	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		
	}

	public void surfaceCreated(SurfaceHolder arg0) {
		thread.setRunning(true);
		thread.start();
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
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	((Activity)getContext()).finish();
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	public void pause() {
		thread.setRunning(false);
	}

	public void resume() {
		thread.setRunning(true);
	}	
}
