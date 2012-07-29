package com.fonserbc.pang;

import java.util.ArrayList;
import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;

public class MainThread extends Thread 
{
	private final static int 	MAX_FPS = 60;
	private final static int	FRAME_PERIOD = 1000 / MAX_FPS;	
	public final static int		NUM_BALLS = 10;
	
	private static final String TAG = MainThread.class.getSimpleName();
	private boolean running;
	
	private SurfaceHolder surfaceHolder;
	private GameView gameView;
	
	public ArrayList<Ball> balls;

	public MainThread(SurfaceHolder surfaceHolder, GameView gameView) {
		super();
		this.surfaceHolder = surfaceHolder;
		this.gameView = gameView;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	@Override
	public void run() {		
		//INIT		
		Canvas canvas;
		Bitmap[] ballSprites = new Bitmap[3];
		ballSprites[0] = BitmapFactory.decodeResource(gameView.getResources(), R.drawable.bola);
		ballSprites[1] = Bitmap.createScaledBitmap(ballSprites[0], ballSprites[0].getWidth()/2, ballSprites[0].getHeight()/2, false);
		ballSprites[2] = Bitmap.createScaledBitmap(ballSprites[1], ballSprites[1].getWidth()/2, ballSprites[1].getHeight()/2, false);
		
		float deltaTime = 0;
		long deltaTimeMs = 0;
		long sleepTime = 0;
		Timer timer = new Timer();
		Stats stats = new Stats();
		
		Random rand = new Random();
		balls = new ArrayList<Ball>();
		
		for (int i = 0; i < NUM_BALLS; ++i) {
			Ball ball = new Ball(ballSprites, gameView);
			ball.position = new Vector2f((gameView.getWidth() - ball.width)*rand.nextFloat(), (gameView.getHeight()/3)*rand.nextFloat());
			ball.gravity = new Vector2f(0, 300f);
			boolean positive = rand.nextBoolean();
			ball.velocity = new Vector2f(((positive)? 1 : -1)*rand.nextFloat()*150f+((positive)? 50 : -50), 0f);
			balls.add(ball);
		}
		
		//RUN
		while (running) {
			canvas = null;
			
			try {				
				canvas = this.surfaceHolder.lockCanvas();
				synchronized (surfaceHolder) {					
					/**Game Update&Draw**/
					deltaTime = timer.tick();
					deltaTimeMs = timer.getLastTickMs();
					
					synchronized (balls) {
						//Update
						for (Ball ball : balls)
							ball.update(deltaTime);
						stats.update();
						
						//Draw	
						canvas.drawColor(Color.BLACK);
						for (Ball ball : balls)
							ball.draw(canvas);
					}
					stats.draw(canvas);
					/**Game Update&Draw**/
				}
			}
			finally {
				if (canvas != null) {
					surfaceHolder.unlockCanvasAndPost(canvas);
				}
			}
			
			/**SLEEP**/
			sleepTime = FRAME_PERIOD - timer.falseTickMs();
			
			if (sleepTime > 0) {
				try {
					sleep(sleepTime);
				} catch (InterruptedException e) {}
			}
		}
	}
	
	public void onClick(Vector2f pos) {
		synchronized (balls) {
			for (Ball b : balls) {
				if (b.collidesPoint(pos)) {
					ArrayList<Ball> newBalls = new ArrayList<Ball>();
					b.divide(newBalls);
					balls.remove(b);
					balls.addAll(newBalls);
					break;
				}
			}
		}
	}
}
