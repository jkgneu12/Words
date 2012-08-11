package com.example.words.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.words.AppController;
import com.example.words.Constants;
import com.example.words.R;
import com.example.words.listener.ShakeEventListener;
import com.example.words.state.Game;
import com.example.words.view.GameBoard;
import com.example.words.view.GameBoardTileHolder;
import com.example.words.view.LastWord;
import com.example.words.view.LastWordTile;
import com.example.words.view.MyTiles;
import com.example.words.view.MyTilesTile;
import com.example.words.view.Tile;
import com.example.words.view.TileHolder;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SendCallback;

public class GameActivity extends Activity{

	private Game game;

	private LastWord lastWord;
	private GameBoard gameBoard;
	private MyTiles myTiles;
	private TextView remainingTiles;
	private TextView score;

	private AppController appController;

	private ParseUser currentUser;

	private Tile activeTile;

	private LinearLayout previousWords;

	private SensorManager mSensorManager;

	private ShakeEventListener mSensorListener;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Constants.initParse(this);
        
        setContentView(R.layout.activity_game);
        
        appController = (AppController)getApplication();
        
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorListener = new ShakeEventListener();   

        mSensorListener.setOnShakeListener(new ShakeEventListener.OnShakeListener() {

          public void onShake() {
            myTiles.shuffle();
          }
        });
        
        currentUser = ParseUser.getCurrentUser();
        
        
        gameBoard = (GameBoard)findViewById(R.id.game_board);
        lastWord = (LastWord)findViewById(R.id.last_word);
        myTiles = (MyTiles)findViewById(R.id.my_tiles);
        remainingTiles = (TextView)findViewById(R.id.remaining_tiles);
        score = (TextView)findViewById(R.id.score);
        previousWords = (LinearLayout)findViewById(R.id.previous_words);
        
        
        for(int z = 0; z < Constants.NUM_TILE_HOLDERS; z++)
        	gameBoard.addView(new GameBoardTileHolder(this, z));
        
        if(savedInstanceState == null){
        	
        	
        	if(getIntent().getBooleanExtra("NewGame", true)){
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
    protected void onResume() {
    	super.onResume();
    	mSensorManager.registerListener(mSensorListener,
    	        mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
    	        SensorManager.SENSOR_DELAY_UI);
    }
    
    @Override
    protected void onPause() {
      mSensorManager.unregisterListener(mSensorListener);
      super.onStop();
    }
    
    @Override
    protected void onSaveInstanceState(Bundle bundle) {
    	update();
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
		String validation = Constants.validateGameBoard(game, lastWord);
		if(validation.equals("1")){
			boolean usedAllTiles = lastWord.usedAllTiles() || myTiles.usedAllTiles();
			int points = getPointsForValidWord(usedAllTiles);
			game.replenishTiles();
			game.incrementCurrentScore(points);
			game.addGameBoardToUsedWord();
			game.save();
			Toast.makeText(this, getMessageForValidWord(usedAllTiles, points), Toast.LENGTH_LONG).show();
			sendPush(game.currentPlayerName, game.waitingPlayerName);
			finish();
		}
		else
			Toast.makeText(this, validation, Toast.LENGTH_LONG).show();
	}
	
	private String getMessageForValidWord(boolean usedAllTiles, int points){
		if(usedAllTiles)
			return "Double Points!!! " + points + " Points";
		else
			return points + " Points!!";
	}

	private int getPointsForValidWord(boolean usedAllTiles) {
		int points = appController.getPoints(game.gameBoard);
		if(usedAllTiles)
			points *= 2;
		return points;
		
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
		update();
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

	public boolean returnTile(Tile tile) {
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
			return true;
		}
		return false;
	}
	
	public void refreshUIFromGame(){
    	myTiles.removeAllViews();
    	for(int z = 0; z < game.currentPlayerTiles.length; z++){
    		if(!Constants.isNull(game.currentPlayerTiles[z]))
    			myTiles.addView(new MyTilesTile(this, "" + game.currentPlayerTiles[z]));
    	}
    	
    	lastWord.setCompleteLastWord(game.completeLastWord);
    	lastWord.setCurrentLastWord(game.currentLastWord);
    	remainingTiles.setText(game.remainingTiles() + " Tiles Remaining");
    	setScoreText();
    	for(int z = 0; z < game.usedWords.size() - 1; z++){
    		TextView word = new TextView(this);
    		word.setText(game.usedWords.get(z));
    		word.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    		word.setPadding(30, 2, 30, 2);
    		word.setTextSize(18);
    		word.setFocusable(true);
    		previousWords.addView(word);
    	}
    	
    	final ScrollView scroll = (ScrollView)previousWords.getParent();
    	if(Build.VERSION.SDK_INT >= 9)
			disableOverScroll(scroll);
    	scroll.post(new Runnable() {

            @Override
            public void run() {
            	scroll.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

	@TargetApi(9)
	private void disableOverScroll(final ScrollView scroll) {
		scroll.setOverScrollMode(ScrollView.OVER_SCROLL_NEVER);
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
		if(activeTile != null)
			activeTile.setActive(true);
	}
	
	public Tile getActiveTile(){
		if(activeTile != null && activeTile.isActive())
			return activeTile;
		return null;
	}
}
