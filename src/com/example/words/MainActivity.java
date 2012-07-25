package com.example.words;

import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

import com.parse.Parse;

public class MainActivity extends Activity {

	private Game game;

	private LastWord lastWord;
	private GameBoard gameBoard;
	private MyTiles myTiles;

	private AppController appController;
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        appController = (AppController)getApplication();
        
        Parse.initialize(this, "VhnMRCE8J0r9fJLuXvGWMQvdNEw6GSxoAQCApqf2", "r4BwcVVLoX7wo92garHMfPa10O6xdmlVIS57ymt8"); 
        
        gameBoard = ((GameBoard)findViewById(R.id.game_board));
        lastWord = ((LastWord)findViewById(R.id.last_word));
        myTiles = ((MyTiles)findViewById(R.id.my_tiles));
        
        
        for(int z = 0; z < Constants.NUM_TILE_HOLDERS; z++)
        	gameBoard.addView(new GameBoardTileHolder(this, z));
        
        if(savedInstanceState == null){
        	game = new Game(this);
            
            for(int z = 0; z < Constants.NUM_MY_TILES; z++)
            	myTiles.addView(new MyTilesTile(this, appController.getLetter(z)));

            lastWord.setLastWord("Test");
            
            update();
        } 
        
        
    }
    
    @Override
    protected void onSaveInstanceState(Bundle bundle) {
    	bundle.putParcelable("game", game);
    	super.onSaveInstanceState(bundle);
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle bundle) {
    	game = bundle.getParcelable("game");
    	
        for(int z = 0; z < game.gameBoard.length; z++){
        	if(Character.isLetter(game.gameBoard[z])){
        		if(game.partOfLastWord[z])
        			gameBoard.addTile(new LastWordTile(this, "" + game.gameBoard[z], z), z);
        		else
        			gameBoard.addTile(new MyTilesTile(this, "" + game.gameBoard[z]), z);
        	}
        }
        
        for(int z = 0; z < game.myTiles.length; z++)
        	myTiles.addView(new MyTilesTile(this, "" + game.myTiles[z]));
        
        lastWord.setLastWord(new String(game.lastWord));
    	
    	super.onRestoreInstanceState(bundle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

	public void update() {
		game.update(gameBoard, myTiles, lastWord);
	}
	
	public void submit(){
		update();
		if(Constants.validate(new String(game.gameBoard))){
			Toast.makeText(this, "Nice Work!!!", Toast.LENGTH_LONG).show();
			game.getParseObject().saveInBackground();
		}
		else
			Toast.makeText(this, "Not a word", Toast.LENGTH_LONG).show();
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
	
	public AppController getAppController(){
		return appController;
	}

	public void returnTile(Tile tile) {
		TileHolder holder = tile.getHolder(); 
		if(holder != null && holder.isGameBoardHolder()){
			if(tile.isPartOfLastWord()){
				holder.unhighlight();
				holder.removeView(tile);
				lastWord.returnTile((LastWordTile)tile);
			} else {
				holder.unhighlight();
				holder.removeView(tile);
				myTiles.addView(tile);
			}
		}
	}

	

    
}
