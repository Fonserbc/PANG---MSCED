package com.fonserbc.pang;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
	
	GameView game;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        if(sm.getSensorList(Sensor.TYPE_GRAVITY).size()!=0){
        	Sensor s = sm.getSensorList(Sensor.TYPE_GRAVITY).get(0);
        	sm.registerListener(this,s, SensorManager.SENSOR_DELAY_NORMAL);
        }
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        game = new GameView(this);
        
        setContentView(game);
        Log.d(TAG, "Create");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_game, menu);
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
            	return true;
            case R.id.ballsCheck:
            	item.setChecked(!item.isChecked());
            	showBalls(item.isChecked());
            	return true;
            case R.id.cpu:
            	item.setChecked(!item.isChecked());
            	showCPU(item.isChecked());
            	return true;
            case R.id.fpsCheck:
            	item.setChecked(!item.isChecked());
            	showFPS(item.isChecked());
            	return true;
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
		game.getThread().useGravity(checked);	
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
               
              game.getThread().setFPS(Integer.parseInt(name.getText().toString()));
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
               
              game.getThread().setBalls(Integer.parseInt(name.getText().toString()));
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

	public void onAccuracyChanged(Sensor arg0, int arg1) {
		
	}
}
