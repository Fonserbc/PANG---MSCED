package com.fonserbc.pang;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;

public class Game extends Activity implements SensorEventListener {

	private static final String TAG = Game.class.getSimpleName();
	
	private SensorManager sm;
	
	private SharedPreferences mPrefs;
	
	private boolean disableSensor = false;
	
	GameView game;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        if(sm.getSensorList(Sensor.TYPE_GRAVITY).size() == 0)
        	disableSensor = true;
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        game = new GameView(this);
        
        restorePrefs();
        
        setContentView(game);
        Log.d(TAG, "Create");
    }

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_game, menu);
        return true;
    }
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MainThread thread = game.getThread();
		
		menu.findItem(R.id.sensor).setChecked(thread.useSensor);
		menu.findItem(R.id.ballsCheck).setChecked(thread.stats.showBalls);
		menu.findItem(R.id.fpsCheck).setChecked(thread.stats.showFPS);
		menu.findItem(R.id.cpu).setChecked(thread.showCPU);
		
		if (disableSensor) {
			menu.findItem(R.id.sensor).setEnabled(false);
			menu.findItem(R.id.sensor).setChecked(false);
			useGravity(false);
		}
		
		return true;
	}
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.quit:
                finish();
                return true;
            case R.id.number_of_balls:
            	setBalls();
            	return true;
            case R.id.fps:
            	setFPS();
            	return true;
            case R.id.sensor:
            	item.setChecked(!item.isChecked());
            	useGravity(item.isChecked());
            	return false;
            case R.id.ballsCheck:
            	item.setChecked(!item.isChecked());
            	showBalls(item.isChecked());
            	return false;
            case R.id.cpu:
            	item.setChecked(!item.isChecked());
            	showCPU(item.isChecked());
            	return false;
            case R.id.fpsCheck:
            	item.setChecked(!item.isChecked());
            	showFPS(item.isChecked());
            	return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    private void showBalls(boolean checked) {
		game.getThread().showBalls(checked);
	}

	private void showFPS(boolean checked) {
		game.getThread().showFPS(checked);
	}

	private void showCPU(boolean checked) {
		game.getThread().showCPU(checked);		
	}

	private void useGravity(boolean checked) {
		activateSensor(checked);
		game.getThread().useGravity(checked);	
	}
	
	private void activateSensor (boolean activate) {
		if (activate) {
			Sensor s = sm.getSensorList(Sensor.TYPE_GRAVITY).get(0);
        	sm.registerListener(this,s, SensorManager.SENSOR_DELAY_NORMAL);
		}
		else {
			Sensor s = sm.getSensorList(Sensor.TYPE_GRAVITY).get(0);
        	sm.unregisterListener(this,s);
		}
	}

	private void setFPS() {
    	final View addView = getLayoutInflater().inflate(R.layout.number_of_balls, null);
        
        new AlertDialog.Builder(this)
          .setTitle("Set Frames per second")
          .setView(addView)
          .setPositiveButton("Set",
                              new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,
                                  int whichButton) {
              EditText name=(EditText)addView.findViewById(R.id.title);
              
              String text = name.getText().toString();
              if (text != null) {
            	  int n = game.getThread().MAX_FPS;
            	  boolean parsed = false;
            	  try {
            		  n = Integer.parseInt(text);
            		  parsed = true;
            	  } catch (NumberFormatException e) {}
            	  
            	  if (parsed) game.getThread().setFPS(n);
              }
            }
          })
          .setNegativeButton("Discard", null)
          .show();
	}

	private void setBalls() {
    	final View addView = getLayoutInflater().inflate(R.layout.number_of_balls, null);
        
        new AlertDialog.Builder(this)
          .setTitle("Set number of balls")
          .setView(addView)
          .setPositiveButton("Set",
                              new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,
                                  int whichButton) {
              EditText name=(EditText)addView.findViewById(R.id.title);
               
              String text = name.getText().toString();
              if (text != null) {
            	  int n = game.getThread().NUM_BALLS;
            	  boolean parsed = false;
            	  try {
            		  n = Integer.parseInt(text);
            		  parsed = true;
            	  } catch (NumberFormatException e) {}
            	  
            	  if (parsed) game.getThread().setBalls(n);
              }
            }
          })
          .setNegativeButton("Discard", null)
          .show();
    }
    
	public void onSensorChanged (SensorEvent event) {
		if (event.sensor.getType() != Sensor.TYPE_GRAVITY) return;
		
		Vector2f gravity = new Vector2f(event.values[1], event.values[0]);
		
		game.getThread().notifyGravity(gravity);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		Log.d(TAG, "Saving");
		super.onSaveInstanceState(outState);
	}
	
	@Override
	public void onRestoreInstanceState (Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		Log.d(TAG, "Restoring");
	}
	
	@Override
	public void onRestart() {
    	Log.d(TAG, "Restart");
    	super.onRestart();
	}
	
	@Override
    public void onResume() {
    	Log.d(TAG, "Resume");
    	super.onResume();
    	game.resume();
    }
	
	@Override
	public void onStart() {
    	Log.d(TAG, "Start");
    	super.onStart();
	}
	
    @Override
    public void onPause() {
    	Log.d(TAG, "Pause");
    	super.onPause();
    	game.pause();
    	savePrefs();
    }    

	@Override
    public void onStop() {
    	Log.d(TAG, "Stop");
    	super.onStop();
    	game.pause();
    }
    
    @Override
    public void onDestroy() {
    	Log.d(TAG, "Destroy");
    	super.onDestroy();
    }
    
    @Override 
    public void finish() {
    	Log.d(TAG, "Finishing");
    	super.finish();
    }
    
    private void savePrefs() {
    	SharedPreferences.Editor ed = mPrefs.edit();
    	MainThread thread = game.getThread();
    	
        ed.putInt("FPS", thread.MAX_FPS);
        ed.putBoolean("useSensor", thread.useSensor);
        
        ed.putBoolean("showCPU", thread.showCPU);
        ed.putBoolean("showFPS", thread.stats.showFPS);
        ed.putBoolean("showBalls", thread.stats.showBalls);
        
        ed.commit();
	}
    
    private void restorePrefs() {
    	mPrefs = getPreferences(MODE_PRIVATE);
    	MainThread thread = game.getThread();
    	
        thread.setFPS(mPrefs.getInt("FPS", thread.MAX_FPS));
        thread.useSensor = mPrefs.getBoolean("useSensor", thread.useSensor);
        
        thread.showCPU(mPrefs.getBoolean("showCPU", thread.showCPU));
        thread.showFPS(mPrefs.getBoolean("showFPS", thread.stats.showFPS));
        thread.showBalls(mPrefs.getBoolean("showBalls", thread.stats.showBalls));
	}

	public void onAccuracyChanged(Sensor arg0, int arg1) {
		
	}
}
