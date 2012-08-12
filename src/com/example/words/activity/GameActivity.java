package com.example.words.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.words.AppController;
import com.example.words.Constants;
import com.example.words.R;
import com.example.words.listener.ShakeEventListener;
import com.example.words.state.Game;
import com.example.words.view.GameBoard;
import com.example.words.view.LastWord;
import com.example.words.view.LastWordTile;
import com.example.words.view.MyTiles;
import com.example.words.view.MyTilesTile;
import com.example.words.view.StarWarsScroller;
import com.example.words.view.Tile;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SendCallback;

public class GameActivity extends Activity implements OnClickListener{

	private Game game;

	private LastWord lastWord;
	private GameBoard gameBoard;
	private MyTiles myTiles;
	private LinearLayout previousWords;
	private TextView remainingTiles;
	private TextView score;
	private TextView opponent;
	
	private Button submit;
	private Button reset;
	private Button pass;
	private Button resign;

	private AppController appController;

	private ParseUser currentUser;

	private Tile activeTile;

	private SensorManager mSensorManager;

	private ShakeEventListener mSensorListener;

	private boolean isMyTurn;

	private boolean isGameOver;



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
        opponent = (TextView)findViewById(R.id.opponent);
        previousWords = (LinearLayout)findViewById(R.id.previous_words);
        submit = (Button)findViewById(R.id.submit);
        reset = (Button)findViewById(R.id.reset);
        pass = (Button)findViewById(R.id.pass);
        resign = (Button)findViewById(R.id.resign);
        
        submit.setOnClickListener(this);
        reset.setOnClickListener(this);
        pass.setOnClickListener(this);
        reset.setOnClickListener(this);
        
        isMyTurn = getIntent().getBooleanExtra("MyTurn", true);
        isGameOver = getIntent().getBooleanExtra("GameOver", false);
        
        
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
        	} else if(isMyTurn) {
        		game = new Game(this);
        		game.currentPlayerId = getIntent().getStringExtra("CurrentPlayerId");
	        	game.currentPlayerName = getIntent().getStringExtra("CurrentPlayerName");
	        	game.currentPlayerScore = getIntent().getIntExtra("CurrentPlayerScore", 0);
	        	game.waitingPlayerId = getIntent().getStringExtra("WaitingPlayerId");
	        	game.waitingPlayerName = getIntent().getStringExtra("WaitingPlayerName");
	        	game.waitingPlayerScore = getIntent().getIntExtra("WaitingPlayerScore", 0);
	        	game.id = getIntent().getStringExtra("id");
        		game.refresh();
        	} else {
        		game = new Game(this);
        		game.currentPlayerId = getIntent().getStringExtra("WaitingPlayerId");
	        	game.currentPlayerName = getIntent().getStringExtra("WaitingPlayerName");
	        	game.currentPlayerScore = getIntent().getIntExtra("WaitingPlayerScore", 0);
	        	game.waitingPlayerId = getIntent().getStringExtra("CurrentPlayerId");
	        	game.waitingPlayerName = getIntent().getStringExtra("CurrentPlayerName");
	        	game.waitingPlayerScore = getIntent().getIntExtra("CurrentPlayerScore", 0);
	        	game.id = getIntent().getStringExtra("id");
        		game.refresh();
        		
        	}
        	game.isMyTurn = isMyTurn;
        } 
        
        if(!isMyTurn || isGameOver){
    		submit.setVisibility(View.GONE);
    		resign.setVisibility(View.GONE);
    		pass.setVisibility(View.GONE);
    	}
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	if(Build.VERSION.SDK_INT >= 11)
    		hideActionBar();
    	Constants.checkVersion(this, false);
    	mSensorManager.registerListener(mSensorListener,
    	        mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
    	        SensorManager.SENSOR_DELAY_UI);
    }

	@TargetApi(11)
	public void hideActionBar() {
		if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
    		getActionBar().hide();
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
			game.addGameBoardToUsedWord(points, gameBoard.getReusedIndices());
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
	
	protected void sendGameOverPush(String yourName, String opponentName) {
		ParsePush push = new ParsePush();
		push.setChannel("User" + opponentName.replaceAll("\\s", ""));
		push.setExpirationTimeInterval(86400);
		push.setMessage("You " + getEndScorePrefix() + " your game with " + yourName);
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

	public void addTileToGameBoard(Tile tile) {
		gameBoard.addView(tile);
	}

	public boolean returnTile(Tile tile) {
		ViewGroup v = (ViewGroup)tile.getParent();
		if(v != null)
			v.removeView(tile);
		if(tile.isPartOfLastWord()){
			lastWord.returnTile((LastWordTile)tile);
		} else {
			myTiles.addView(tile);
		}
		return true;
	}

	public void refreshUIFromGame(){
    	myTiles.removeAllViews();
    	for(int z = 0; z < game.currentPlayerTiles.length; z++){
    		if(!Constants.isNull(game.currentPlayerTiles[z]))
    			myTiles.addView(new MyTilesTile(this, "" + game.currentPlayerTiles[z]));
    	}
    	
    	lastWord.setCompleteLastWord(game.completeLastWord);
    	lastWord.setCurrentLastWord(game.currentLastWord);
    	remainingTiles.setText(game.remainingTiles() + " Tiles Left");
    	opponent.setText(getMatchupText());
    	setScoreText();
    	setupPreviousWords();
    }

	public void setupPreviousWords() {
		int prevWordCount = game.usedWords.size();
    	for(int z = 0; z < prevWordCount - 1; z++){
    		LinearLayout row = (LinearLayout) ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.prev_word, previousWords, false);
    		
    		LinearLayout word = (LinearLayout)row.findViewById(R.id.b);
    		float fontSize = Constants.getPreviousWordSize(this);
    		String wordText = game.usedWords.get(z);
    		for(int y = 0; y < wordText.length(); y++){
    			TextView letter = new TextView(this);
    			letter.setText("" + wordText.charAt(y));
    			letter.setTextSize(fontSize);
    			if(game.reused == null || game.reused.size() <= z || !game.reused.get(z).contains(y))
    				letter.setTextColor(getResources().getColor(R.color.brown));
    			word.addView(letter);
        		//word.setFocusable(true);
    		}
    		
    		
    		if(game.scores != null && game.scores.size() > z){
	    		String scoreText = "" + game.scores.get(z);
	    		TextView score;
	    		TextView space;
	    		if(game.turns.size() > z && game.turns.get(z).equals(currentUser.getObjectId())){
	    			score = (TextView)row.findViewById(R.id.a);
	    			space = (TextView)row.findViewById(R.id.c);
	    		} else {
	    			score = (TextView)row.findViewById(R.id.c);
	    			space = (TextView)row.findViewById(R.id.a);
	    		}
	    		score.setText(scoreText);
	    		score.setTextSize(fontSize / 2);
	    		String spaceText = "";
	    		for(int y = 0; y < wordText.length(); y++)
	    			spaceText += " ";
	    		space.setText(spaceText);
	    		space.setTextSize(fontSize / 2);
    		} 
    		
    		
    		
    		
    		
    		previousWords.addView(row);
    	}
    	
    	final StarWarsScroller scroll = (StarWarsScroller)previousWords.getParent();
    	scroll.post(new Runnable() {
            @Override
            public void run() {
            	scroll.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
	}

	public String getMatchupText() {
		if(isMyTurn)
			return game.currentPlayerName + " vs. " + game.waitingPlayerName;
		return game.waitingPlayerName + " vs. " + game.currentPlayerName;
	}
	
	private void refreshGameBoardUIFromGame() {
		for(int z = 0; z < game.gameBoard.length; z++){
        	if(!Constants.isNull(game.gameBoard[z]) && Character.isLetter(game.gameBoard[z].charAt(0))){
        		Tile tile = Tile.create(this, "" + game.gameBoard[z], game.gameBoardIndices[z], game.partOfLastWord[z]);
        		gameBoard.addView(tile);
        	}
        }
	}

	private void setScoreText() {
		String scoreText = getScorePrefix() + game.currentPlayerScore + " : " + game.waitingPlayerScore;
    	score.setText(scoreText);
	}
	
	private String getScorePrefix() {
		if(isGameOver)
			return getEndScorePrefix();
		else
			return getLiveScorePrefix();
	}

	private String getLiveScorePrefix(){
		if(isMyTurn){
			if(game.currentPlayerScore > game.waitingPlayerScore)
	    		return "Winning ";
	    	else if(game.currentPlayerScore < game.waitingPlayerScore)
	    		return "Losing ";
		} else {
			if(game.currentPlayerScore > game.waitingPlayerScore)
	    		return "Losing ";
	    	else if(game.currentPlayerScore < game.waitingPlayerScore)
	    		return "Winning ";
		}
    	return "Tied ";
	}
	
	private String getEndScorePrefix(){
		if(game.currentPlayerScore > game.waitingPlayerScore)
    		return "Won ";
    	else if(game.currentPlayerScore < game.waitingPlayerScore)
    		return "Lost ";
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

	@Override
	public void onClick(View v) {
		if(v == submit)
			submit();
		else if(v == reset)
			reset();
		else if(v == pass)
			pass();
	}

	private void pass() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Are you sure you want to skip your turn?")
		       .setCancelable(false)
		       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   reset();
		        	   if(game.lastPlayerPassed){
		        		   game.save(true, true);
		        		   sendGameOverPush(game.currentPlayerName, game.waitingPlayerName);
		        		   finish();
		        	   } else {
		        		   game.save(true, false);
		        		   sendPush(game.currentPlayerName, game.waitingPlayerName);
		        	   }
		        	   finish();
		           }
		       })
		       .setNegativeButton("No", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		builder.show();
	}

	protected void endGame() {
		
	}
}
