package com.example.words;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;

public class MainActivity extends Activity {
	
	String alphabet="ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	private Game game;

	private LastWord lastWord;
	private GameBoard gameBoard;
	private MyTiles myTiles;
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        game = new Game();
        
        gameBoard = ((GameBoard)findViewById(R.id.game_board));
        for(int z = 0; z < Constants.NUM_TILE_HOLDERS; z++)
        	gameBoard.addView(new TileHolder(this, z));
        
        myTiles = ((MyTiles)findViewById(R.id.my_tiles));
        for(int z = 0; z < Constants.NUM_MY_TILES; z++)
        	myTiles.addView(new Tile(this, alphabet.charAt(z), false));
        
        lastWord = ((LastWord)findViewById(R.id.last_word));
        for(int z = 0; z < Constants.NUM_MY_TILES; z++)
        	lastWord.addView(new Tile(this, alphabet.charAt(z + 5), true));
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

	public void startDrag(Tile tile) {
		/*for(int z = 0; z < gameBoard.getChildCount(); z++){
			if(!gameBoard.hasTileAt(z))
				gameBoard.highlight(z);
		}*/
	}

	public void stopDrag() {
		/*for(int z = 0; z < gameBoard.getChildCount(); z++){
			gameBoard.unhighlight(z);
		}*/
	}

	public void addToMyTiles(View v) {
		myTiles.addView(v);
	}

	public void replaceMyTile(Tile oldChild, Tile newChild) {
		myTiles.replaceTile(oldChild, newChild);
	}
	
	public void replaceLastWordTile(Tile oldChild, Tile newChild) {
		lastWord.replaceTile(oldChild, newChild);
	}

	public void addTileToGameBoard(Tile tile, int index) {
		gameBoard.addTile(tile, index);
	}

	

    
}
