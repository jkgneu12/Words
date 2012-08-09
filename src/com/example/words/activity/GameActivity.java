package com.example.words.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.words.AppController;
import com.example.words.Constants;
import com.example.words.R;
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
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.SendCallback;

public class GameActivity extends Activity {

	private Game game;

	private LastWord lastWord;
	private GameBoard gameBoard;
	private MyTiles myTiles;
	private TextView remainingTiles;
	private TextView score;

	private AppController appController;

	private ParseUser currentUser;

	private Tile activeTile;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Parse.initialize(this, "VhnMRCE8J0r9fJLuXvGWMQvdNEw6GSxoAQCApqf2", "r4BwcVVLoX7wo92garHMfPa10O6xdmlVIS57ymt8"); 
        
        setContentView(R.layout.activity_game);
        
        appController = (AppController)getApplication();
        
        currentUser = ParseUser.getCurrentUser();
        
        
        gameBoard = (GameBoard)findViewById(R.id.game_board);
        lastWord = (LastWord)findViewById(R.id.last_word);
        myTiles = (MyTiles)findViewById(R.id.my_tiles);
        remainingTiles = (TextView)findViewById(R.id.remaining_tiles);
        score = (TextView)findViewById(R.id.score);
        
        
        for(int z = 0; z < Constants.NUM_TILE_HOLDERS; z++)
        	gameBoard.addView(new GameBoardTileHolder(this, z));
        
        if(savedInstanceState == null){
        	
        	
        	if(getIntent().getBooleanExtra("NewGame", true)){
        		int[] ids = {1,2};
        		String[] names = {"Jim","Bob"};
	        	game = new Game(this);
	        	game.initBag();
	        	game.initMyTiles();
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
    
    @Override
    protected void onSaveInstanceState(Bundle bundle) {
    	bundle.putParcelable("game", game);
    	super.onSaveInstanceState(bundle);
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle bundle) {
    	game = bundle.getParcelable("game");
    	
        refreshGameBoardUIFromGame();
	    refreshUIFromGame();
    	
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
		String validation = Constants.validateGameBoard(game, lastWord.getLastWord());
		if(validation.equals("1")){
			Toast.makeText(this, "Nice Work!!!", Toast.LENGTH_LONG).show();
			game.replenishTiles();
			game.incrementCurrentScore(appController.getPoints(game.gameBoard));
			game.addGameBoardToUsedWord();
			game.save();
			sendPush(game.currentPlayerName, game.waitingPlayerName);
			finish();
		}
		else
			Toast.makeText(this, validation, Toast.LENGTH_LONG).show();
	}
	
	private void sendPush(String yourName, String opponentName) {
		ParsePush push = new ParsePush();
		push.setChannel("User" + opponentName.replaceAll("\\s", ""));
		push.setExpirationTimeInterval(86400);
		push.setMessage("Your turn with " + yourName);
		push.sendInBackground(new SendCallback() {
			
			@Override
			public void done(ParseException e) {
				if(e != null)
					Log.w("PUSH", e.getMessage());
			}
		});
	}

	public void reset() {
		gameBoard.reset();
		setActiveTile(null);
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
	
	public void refreshUIFromGame(){
    	myTiles.removeAllViews();
    	for(int z = 0; z < game.myTiles.length; z++){
    		if(!Constants.isNull(game.myTiles[z]))
    			myTiles.addView(new MyTilesTile(this, "" + game.myTiles[z]));
    	}
    	
    	lastWord.setLastWord(game.lastWord);
    	remainingTiles.setText(game.remainingTiles() + " Tiles Remaining");
    	setScoreText();
    }
	
	private void refreshGameBoardUIFromGame() {
		for(int z = 0; z < game.gameBoard.length; z++){
        	if(!Constants.isNull(game.gameBoard[z]) && Character.isLetter(game.gameBoard[z].charAt(0))){
        		Tile tile = Tile.create(this, "" + game.gameBoard[z], z, game.partOfLastWord[z]);
        		gameBoard.addTile(tile, z);
        	}
        }
	}

	private void setScoreText() {
		String scoreText = getScorePrefix() + game.currentPlayerScore + " : " + game.waitingPlayerScore;
    	score.setText(scoreText);
	}
	
	private String getScorePrefix(){
		if(game.currentPlayerScore > game.waitingPlayerScore)
    		return "Winning ";
    	else if(game.currentPlayerScore < game.waitingPlayerScore)
    		return "Losing ";
    	return "Tied ";
	}

	public AppController getAppController(){
		return appController;
	}

	public void setActiveTile(Tile tile) {
		if(activeTile != null)
			activeTile.setActive(false);
		activeTile = tile;
	}
	
	public Tile getActiveTile(){
		if(activeTile != null && activeTile.isActive())
			return activeTile;
		return null;
	}
}
