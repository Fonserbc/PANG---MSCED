package com.fonserbc.pang;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class Scene {
	private Bitmap background;
	
	private GameView gameView;
	
	public Scene(GameView view) {
		gameView = view;
		background = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(gameView.getResources(), R.drawable.green_hills),
				gameView.getWidth(), gameView.getHeight(), false);
	}
	
	public void onDraw (Canvas canvas) {
		synchronized (canvas) {
			canvas.drawBitmap(background, 0, 0, null);
		}
	}
}
