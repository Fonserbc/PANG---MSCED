package com.fonserbc.pang;

import java.util.ArrayList;
import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;

public class GameThread extends Thread 
{
	private Preferences Prefs = Preferences.getInstance();
	
	public int 		MAX_FPS = Prefs.FPS;
	private int		FRAME_PERIOD = 1000 / MAX_FPS;	
	public int		NUM_BALLS = 1;
	
	private static final String TAG = GameThread.class.getSimpleName();
	private static final float DEF_GX = 0f;
	private static final float DEF_GY = 300f;
	private static final float DEF_GM = 300f;
	
	private boolean running;
	
	private SurfaceHolder surfaceHolder;
	private GameView gameView;
	
	private Timer timer;
	private Timer timerCPU;
	
	private Stats stats;
	
	public ArrayList<Ball> balls;
	
	private Bitmap[] ballSprites;
	
	public Vector2f gravity;

	public GameThread(SurfaceHolder surfaceHolder, GameView gameView) {
		super();
		this.surfaceHolder = surfaceHolder;
		this.gameView = gameView;
		timer =  new Timer();
		timerCPU = new Timer();
		stats = new Stats(this);
		gravity = new Vector2f(DEF_GX, DEF_GY);
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	@Override
	public void run() {		
		//INIT		
		Canvas canvas;
		ballSprites = new Bitmap[3];
		ballSprites[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(gameView.getResources(), R.drawable.bola), 
				gameView.getHeight()/4, gameView.getHeight()/4, false);
		ballSprites[1] = Bitmap.createScaledBitmap(ballSprites[0], ballSprites[0].getWidth()/2, ballSprites[0].getHeight()/2, false);
		ballSprites[2] = Bitmap.createScaledBitmap(ballSprites[1], ballSprites[1].getWidth()/2, ballSprites[1].getHeight()/2, false);
		
		float deltaTime = 0;
		long deltaTimeMs = 0;
		long sleepTime = 0;
		float lastCPUtime = 0.0f;
		
		Random rand = new Random();
		balls = new ArrayList<Ball>();
		
		for (int i = 0; i < NUM_BALLS; ++i) {
			Ball ball = new Ball(ballSprites, gameView, this);
			ball.position = new Vector2f((gameView.getWidth() - ball.width)*rand.nextFloat(), (gameView.getHeight()/3)*rand.nextFloat());
			boolean positive = rand.nextBoolean();
			ball.velocity = new Vector2f(((positive)? 1 : -1)*rand.nextFloat()*150f+((positive)? 50 : -50), 0f);
			balls.add(ball);
		}
		
		//RUN
		while (!this.isInterrupted()) {
			if (running) {
				canvas = null;
				if (Prefs.showCPU) {
					float cpuTick = timerCPU.tick();
					stats.updateCPU(lastCPUtime/cpuTick);
				}
				
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
							
							//Draw	
							canvas.drawColor(Color.BLACK);
							for (Ball ball : balls)
								ball.draw(canvas);
						}
						stats.draw(canvas);
						/**Game Update&Draw**/
					}
					
					/**SLEEP**/
					long workingTime = timer.falseTickMs();
					sleepTime = FRAME_PERIOD - workingTime;
					stats.updateFPS();
					
					if (sleepTime > 0) {
						try {
							sleep(sleepTime);
						} catch (InterruptedException e) {}
					}
				}
				finally {
					if (canvas != null) {
						surfaceHolder.unlockCanvasAndPost(canvas);
					}
				}
				
				if (Prefs.showCPU) lastCPUtime = timerCPU.falseTick();
			}
			else {	//Paused
				try { Thread.sleep(100); } catch (InterruptedException ie) {}
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

	public void setFPS(Integer n) {
		if (n > 0) {
			MAX_FPS = n;
			FRAME_PERIOD = 1000 / MAX_FPS;
			if (n < 22) timer.setReal(true);
			else timer.setReal(false);
		}
	}

	public void setBalls(int n) {
		synchronized (balls) {
			NUM_BALLS = n;
			
			Random rand = new Random();
			balls.clear();
			for (int i = 0; i < NUM_BALLS; ++i) {
				Ball ball = new Ball(ballSprites, gameView, this);
				ball.position = new Vector2f((gameView.getWidth() - ball.width)*rand.nextFloat(), (gameView.getHeight() - ball.height)*rand.nextFloat());
				boolean positive = rand.nextBoolean();
				ball.velocity = new Vector2f(((positive)? 1 : -1)*rand.nextFloat()*150f+((positive)? 50 : -50), 0f);
				balls.add(ball);
			}
		}		
	}

	public void useGravity(boolean use) {
		if (!use)
			gravity = new Vector2f(DEF_GX, DEF_GY);
	}

	public void notifyGravity(Vector2f gravity) {
		Vector2f norm = gravity.normalized();
		this.gravity = new Vector2f(norm.x*DEF_GM, norm.y*DEF_GM);
	}

	public void restoreState(Bundle savedInstanceState) {
				
	}

	public void saveState(Bundle outState) {
		
	}
}
