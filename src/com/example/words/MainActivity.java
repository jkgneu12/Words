package com.example.words;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.LinearLayout;

public class MainActivity extends Activity {
	
	String alphabet="ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	private Game game;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        game = new Game();
        
        LinearLayout gameBoard = ((LinearLayout)findViewById(R.id.game_board));
        for(int z = 0; z < Constants.NUM_TILE_HOLDERS; z++)
        	gameBoard.addView(new TileHolder(this));
        
        LinearLayout myTiles = ((LinearLayout)findViewById(R.id.my_tiles));
        for(int z = 0; z < Constants.NUM_MY_TILES; z++)
        	myTiles.addView(new Tile(this, alphabet.charAt(z)));
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    
}
