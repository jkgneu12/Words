package com.example.words.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

import com.example.words.AppController;
import com.example.words.Constants;
import com.example.words.R;
import com.example.words.R.id;
import com.example.words.R.layout;
import com.example.words.R.menu;
import com.example.words.state.Game;
import com.example.words.view.GameBoard;
import com.example.words.view.GameBoardTileHolder;
import com.example.words.view.LastWord;
import com.example.words.view.LastWordTile;
import com.example.words.view.MyTiles;
import com.example.words.view.MyTilesTile;
import com.example.words.view.Tile;
import com.example.words.view.TileHolder;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class GameActivity extends Activity {

	private Game game;

	private LastWord lastWord;
	private GameBoard gameBoard;
	private MyTiles myTiles;

	private AppController appController;

	private ParseUser currentUser;

	private String userId;
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Parse.initialize(this, "VhnMRCE8J0r9fJLuXvGWMQvdNEw6GSxoAQCApqf2", "r4BwcVVLoX7wo92garHMfPa10O6xdmlVIS57ymt8"); 
        
        setContentView(R.layout.activity_game);
        
        appController = (AppController)getApplication();
        
        currentUser = ParseUser.getCurrentUser();
        userId = currentUser.getObjectId();
        
        
        gameBoard = ((GameBoard)findViewById(R.id.game_board));
        lastWord = ((LastWord)findViewById(R.id.last_word));
        myTiles = ((MyTiles)findViewById(R.id.my_tiles));
        
        
        for(int z = 0; z < Constants.NUM_TILE_HOLDERS; z++)
        	gameBoard.addView(new GameBoardTileHolder(this, z));
        
        if(savedInstanceState == null){
        	
        	
        	if(getIntent().getBooleanExtra("NewGame", true)){
        		int[] ids = {1,2};
        		String[] names = {"Jim","Bob"};
	        	game = new Game(this);
	        	game.randomTiles();
	        	game.currentPlayerId = currentUser.getObjectId();
	        	game.currentPlayerName = currentUser.getUsername();
	        	game.currentPlayerScore = 0;
	        	game.waitingPlayerId = getIntent().getStringExtra("WaitingPlayerId");
	        	game.waitingPlayerName = getIntent().getStringExtra("WaitingPlayerName");
	        	game.waitingPlayerScore = 0;
	        	refreshUIFromGame();  
	        	game.save();
        	} else {
        		game = new Game(this);
        		game.currentPlayerId = getIntent().getStringExtra("CurrentPlayerId");
	        	game.currentPlayerName = getIntent().getStringExtra("CurrentPlayerName");
	        	game.currentPlayerScore = getIntent().getIntExtra("CurrentPlayerScore", 0);
	        	game.waitingPlayerId = getIntent().getStringExtra("WaitingPlayerId");
	        	game.waitingPlayerName = getIntent().getStringExtra("WaitingPlayerName");
	        	game.waitingPlayerScore = getIntent().getIntExtra("WaitingPlayerScore", 0);
	        	game.id = getIntent().getStringExtra("id");
        		game.refresh();
        	}
        	
        	
        } 
    }
    
    public void refreshUIFromGame(){
    	myTiles.removeAllViews();
    	for(int z = 0; z < game.myTiles.length; z++){
    		if(game.myTiles[z] != null && !game.myTiles[z].equals("null"))
    			myTiles.addView(new MyTilesTile(this, "" + game.myTiles[z]));
    	}
    	
    	lastWord.setLastWord(game.lastWord);
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
        	if(Character.isLetter(game.gameBoard[z].charAt(0))){
        		if(game.partOfLastWord[z])
        			gameBoard.addTile(new LastWordTile(this, "" + game.gameBoard[z], z), z);
        		else
        			gameBoard.addTile(new MyTilesTile(this, "" + game.gameBoard[z]), z);
        	}
        }
        
        for(int z = 0; z < game.myTiles.length; z++)
        	myTiles.addView(new MyTilesTile(this, "" + game.myTiles[z]));
        
        lastWord.setLastWord(game.lastWord);
    	
    	super.onRestoreInstanceState(bundle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_game, menu);
        return true;
    }

	public void update() {
		game.update(gameBoard, myTiles, lastWord);
	}
	
	public void submit(){
		update();
		if(Constants.validateGameBoard(game)){
			Toast.makeText(this, "Nice Work!!!", Toast.LENGTH_LONG).show();
			game.incrementCurrentScore(appController.getPoints(game.gameBoard));
			game.usedWords.add(Constants.arrayToString(game.gameBoard));
			game.save();
			finish();
		}
		else
			Toast.makeText(this, "Not a word", Toast.LENGTH_LONG).show();
	}
	
	public void reset() {
		gameBoard.reset();
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
