package com.fonserbc.pang;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

	private static final String TAG = GameView.class.getSimpleName();
	private MainThread thread;
	
	private boolean firstCreated = true;
	
	public GameView(Context context) {
		super(context);getHolder().addCallback(this);
		
		thread = new MainThread(getHolder(), this);
		
		setFocusable(true);
	}

	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		
	}

	public void surfaceCreated(SurfaceHolder arg0) {
		thread.setRunning(true);
		if (firstCreated) {
			thread.start();
			firstCreated = false;
		}
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

	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			thread.onClick(new Vector2f(event.getX(), event.getY()));
		}
		return super.onTouchEvent(event);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	((Activity)getContext()).moveTaskToBack(true);
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	public void pause() {
		thread.setRunning(false);
		//thread.stop();
	}

	public void resume() {
		thread.setRunning(true);
		//thread.start();
	}

	
}