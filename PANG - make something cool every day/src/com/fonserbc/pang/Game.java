package com.fonserbc.pang;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

public class Game extends Activity {

	GameView game;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        game = new GameView(this);
        setContentView(game);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_game, menu);
        return true;
    } 
    
    @Override
    public void onStart() {
    	super.onStart();
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	game.pause();
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	game.resume();
    }
}
