package com.fonserbc.pang;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

public class Ball {
	
	private static final String TAG = Ball.class.getSimpleName();
	
	private Bitmap[] sprites;
	private Bitmap sprite;
	private GameView gameView;
	
	public Vector2f position;
	public Vector2f velocity;
	public Vector2f gravity;
	public float width;
	public float height;
	public float bounce;
	public float scale;
	public int it;
	
	
	public Ball (Bitmap[] sprites, GameView gameView) {
		this.sprites = sprites;
		this.gameView = gameView;
		scale = 1;
		
		setScale(0);
		
		position = new Vector2f();
		velocity = new Vector2f();
		gravity = new Vector2f();
	}
	
	private void setScale(int s) {
		it = s;
		for (int i = 0; i < s; ++i) scale /= 2;
		
		sprite = sprites[it];
		width = sprites[it].getWidth();
		height = sprites[it].getHeight();
		bounce = 450f*(0.5f + (float)Math.log(1f + ((float)Math.E-1) * scale) * 0.5f);
	}
	
	public void update (float deltaTime) {
		float sqrDeltaTime = deltaTime*deltaTime;
		
		position.x += velocity.x * deltaTime + 0.5f * gravity.x * sqrDeltaTime;
		position.y += velocity.y * deltaTime + 0.5f * gravity.y * sqrDeltaTime;
		
		velocity.x += gravity.x * deltaTime;
		velocity.y += gravity.y * deltaTime;
		
		if (position.x + width > gameView.getWidth() && velocity.x > 0f) {
			velocity.x = -velocity.x;
		}
		else if (position.x < 0f && velocity.x < 0f) {
			velocity.x = -velocity.x;
		}

		if (position.y < 0f && velocity.y < 0f) {
			velocity.y = -velocity.y;
		}
		else if (position.y + height > gameView.getHeight() && velocity.y > 0f) {
			velocity.y = -bounce;
		}
	}
	
	public void draw (Canvas canvas) {
		canvas.drawBitmap(sprite, position.x, position.y, null);
	}

	public boolean collidesPoint(Vector2f pos) {
		Vector2f center = new Vector2f (position.x + width/2, position.y + height/2);
		float dx = Math.abs(pos.x - center.x);
		float dy = Math.abs(pos.y - center.y);
		
		if (dx <= width/2 && dy <= height/2) return true;
		else return false;
	}

	public void divide(ArrayList<Ball> balls) {
		if (scale > 0.25) {
			Ball b1 = new Ball(sprites, gameView);
			Ball b2 = new Ball(sprites, gameView);
			
			b1.setScale(it+1);
			b2.setScale(it+1);
			
			b1.position = new Vector2f(position.x+width/2, position.y+height/2);
			b2.position = new Vector2f(position.x+width/2, position.y+height/2);
			
			b1.velocity = new Vector2f(velocity.x, velocity.y);
			b2.velocity = new Vector2f(-velocity.x, velocity.y);
			
			b1.gravity = gravity;
			b2.gravity = gravity;
			
			balls.add(b1);
			balls.add(b2);
		}
	}
}