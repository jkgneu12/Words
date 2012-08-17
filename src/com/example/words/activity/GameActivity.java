package com.example.words.activity;

import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import com.example.words.network.PushManager;
import com.example.words.state.Game;
import com.example.words.view.GameBoard;
import com.example.words.view.LastWord;
import com.example.words.view.LastWordTile;
import com.example.words.view.MyTiles;
import com.example.words.view.MyTilesTile;
import com.example.words.view.StarWarsScroller;
import com.example.words.view.Tile;
import com.parse.ParseUser;

public class GameActivity extends BaseActivity implements OnClickListener{

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
	
	private Game game;

	private ParseUser currentUser;

	private Tile activeTile;
	
	private boolean isMyTurn;
	private boolean isGameOver;
	
	private SensorManager mSensorManager;
	private ShakeEventListener mSensorListener;
	private TextView currentScore;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Constants.initParse(this);
        
        setContentView(R.layout.activity_game);
        
        appController = (AppController)getApplication();
        
        setupShakeListener();
        
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
        currentScore = (TextView)findViewById(R.id.current_score);
        
        submit.setOnClickListener(this);
        reset.setOnClickListener(this);
        pass.setOnClickListener(this);
        reset.setOnClickListener(this);
        resign.setOnClickListener(this);
        
        isMyTurn = getIntent().getBooleanExtra("MyTurn", true);
        isGameOver = getIntent().getBooleanExtra("GameOver", false);
        boolean isNewGame = getIntent().getBooleanExtra("NewGame", true);
    	
        
        
        if(savedInstanceState == null){
        	game = new Game(this, getIntent(), isNewGame, isMyTurn);
        	if(isNewGame)
            	refreshUIFromGame();  
        } 
        
        if(!isMyTurn || isGameOver){
    		showReadOnlyButtons();
    	} else if(isNewGame){
    		showNewGameButtons();
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
    	update();
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
        
        MenuItem refresh = menu.findItem(R.id.chat);


		refresh.setOnMenuItemClickListener(
			new MenuItem.OnMenuItemClickListener () { 
				public boolean onMenuItemClick(MenuItem item) { 
					chat();
					return true;
				}
			} 
		); 
        
        return true;
    }

	protected void chat() {
		Intent intent = new Intent();
		intent.setClass(this, ChatActivity.class);
		intent.putExtra("receivingUser", isMyTurn ? game.waitingPlayer.id : game.currentPlayer.id);
		intent.putExtra("receivingUserName", isMyTurn ? game.waitingPlayer.userName : game.currentPlayer.userName);
		intent.putExtra("receivingName", isMyTurn ? game.waitingPlayer.displayName : game.currentPlayer.displayName);
		startActivity(intent);
	}

	public void update() {
		game.update(gameBoard, myTiles, lastWord);
		updateCurrentScore();
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
		update();
		return true;
	}

	public void refreshUIFromGame(){
    	myTiles.removeAllViews();
    	for(int z = 0; z < game.currentPlayer.tiles.length; z++){
    		if(!Constants.isNull(game.currentPlayer.tiles[z]))
    			myTiles.addView(new MyTilesTile(this, "" + game.currentPlayer.tiles[z]));
    	}
    	
    	lastWord.setCompleteLastWord(game.lastTurn.completeLastWord);
    	lastWord.setCurrentLastWord(game.lastTurn.currentLastWord);
    	remainingTiles.setText(game.bag.remainingTiles() + " Tiles Left");
    	opponent.setText(getMatchupText());
    	setScoreText();
    	setupPreviousWords();
    }
	
	private void refreshGameBoardUIFromGame() {
		for(int z = 0; z < game.board.tiles.length; z++){
        	if(!Constants.isNull(game.board.tiles[z]) && Character.isLetter(game.board.tiles[z].charAt(0))){
        		Tile tile = Tile.create(this, "" + game.board.tiles[z], game.board.indices[z], game.lastTurn.partOfLastWord[z]);
        		gameBoard.addView(tile);
        	}
        }
	}

	public void setupPreviousWords() {
		int prevWordCount = game.prevWords.usedWords.size();
		float fontSize = Constants.getPreviousWordSize(this);
		
    	for(int z = 0; z < prevWordCount - 1; z++){
    		LinearLayout row = (LinearLayout) ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.prev_word, previousWords, false);
    		
    		LinearLayout word = (LinearLayout)row.findViewById(R.id.b);
    		String wordText = game.prevWords.usedWords.get(z);
    		for(int y = 0; y < wordText.length(); y++){
    			TextView letter = new TextView(this);
    			letter.setText("" + wordText.charAt(y));
    			letter.setTextSize(fontSize);
    			if(game.prevWords.reused == null || game.prevWords.reused.size() <= z || !game.prevWords.reused.get(z).contains(y))
    				letter.setTextColor(getResources().getColor(R.color.brown));
    			else 
    				letter.setTextColor(getResources().getColor(R.color.orange));
    			word.addView(letter);
    		}
    		
    		if(game.prevWords.scores != null && game.prevWords.scores.size() > z){
	    		String scoreText = "" + game.prevWords.scores.get(z);
	    		TextView score;
	    		TextView space;
	    		if(game.prevWords.turns.size() > z && game.prevWords.turns.get(z).equals(currentUser.getObjectId())){
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
    	
    	addDummyPreviousWord(fontSize);
    	
    	final StarWarsScroller scroll = (StarWarsScroller)previousWords.getParent();
    	scroll.post(new Runnable() {
            @Override
            public void run() {
            	scroll.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
	}

	public void addDummyPreviousWord(float fontSize) {
		LinearLayout row = (LinearLayout) ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.prev_word, previousWords, false);
		
		LinearLayout word = (LinearLayout)row.findViewById(R.id.b);
		TextView text = new TextView(this);
		text.setText(" ");
		if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
			fontSize *= 1.3;
		text.setTextSize((int)fontSize);
		word.addView(text);
		
    	previousWords.addView(row);
	}

	public String getMatchupText() {
		if(isMyTurn)
			return game.currentPlayer.displayName + " vs. " + game.waitingPlayer.displayName;
		return game.waitingPlayer.displayName + " vs. " + game.currentPlayer.displayName;
	}
	
	

	private void setScoreText() {
		String scoreText;
		if(isMyTurn)
			scoreText = getScorePrefix() + game.currentPlayer.score + " : " + game.waitingPlayer.score;
		else
			scoreText = getScorePrefix() + game.waitingPlayer.score + " : " + game.currentPlayer.score;
    	score.setText(scoreText);
	}
	
	private String getScorePrefix() {
		if(isGameOver)
			return getEndScorePrefix(isMyTurn);
		else
			return getLiveScorePrefix();
	}

	private String getLiveScorePrefix(){
		if(isMyTurn){
			if(game.currentPlayer.score > game.waitingPlayer.score)
	    		return "Winning ";
	    	else if(game.currentPlayer.score < game.waitingPlayer.score)
	    		return "Losing ";
		} else {
			if(game.currentPlayer.score > game.waitingPlayer.score)
	    		return "Losing ";
	    	else if(game.currentPlayer.score < game.waitingPlayer.score)
	    		return "Winning ";
		}
    	return "Tied ";
	}
	
	private String getEndScorePrefix(boolean forMe){
		if(forMe){
			if(game.currentPlayer.score > game.waitingPlayer.score)
				return "Won ";
			else if(game.currentPlayer.score < game.waitingPlayer.score)
				return "Lost ";
		} else {
			if(game.currentPlayer.score > game.waitingPlayer.score)
				return "Lost ";
			else if(game.currentPlayer.score < game.waitingPlayer.score)
				return "Won ";
		}
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
		else if(v == resign)
			resign();
	}
	
	public void submit(){
		update();
		String validation = Constants.startValidateGameBoard(this, game, lastWord);
		if(validation != "1")
			Toast.makeText(this, validation, Toast.LENGTH_LONG).show();
	}
	
	public void validated(JSONObject result) {
		if(result != null && result.has("value")){
			boolean usedAllTiles = usedAllTiles();
			int points = getPointsForValidWord(usedAllTiles);
			game.currentPlayer.replenishTiles();
			game.currentPlayer.incrementCurrentScore(points);
			game.prevWords.addGameBoardToUsedWord(points, gameBoard.getReusedIndices());
			game.save();
			Toast.makeText(this, getMessageForValidWord(usedAllTiles, points), Toast.LENGTH_LONG).show();
			PushManager.sendGameUpdatePush(game.currentPlayer.displayName, game.waitingPlayer.userName);
			showReadOnlyButtons();
		}
		else
			Toast.makeText(this, "Not a Word", Toast.LENGTH_SHORT).show();
	}

	private void showReadOnlyButtons() {
		submit.setVisibility(View.GONE);
		resign.setVisibility(View.GONE);
		pass.setVisibility(View.GONE);
	}
	
	private void showNewGameButtons() {
		resign.setVisibility(View.GONE);
		pass.setVisibility(View.GONE);
	}

	public boolean usedAllTiles() {
		return lastWord.usedAllTiles() || myTiles.usedAllTiles();
	}
	
	private String getMessageForValidWord(boolean usedAllTiles, int points){
		if(usedAllTiles)
			return "Double Points!!! " + points + " Points";
		else
			return points + " Points!!";
	}

	private int getPointsForValidWord(boolean usedAllTiles) {
		int points = appController.getPoints(game.board.tiles);
		if(usedAllTiles)
			points *= 2;
		return points;
		
	}
	
	public void reset() {
		gameBoard.reset();
		setActiveTile(null);
		update();
	}

	private void pass() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Are you sure you want to skip your turn?")
		       .setCancelable(false)
		       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   reset();
		        	   if(game.lastTurn.lastPlayerPassed){
		        		   game.save(true, true, false);
		        		   PushManager.sendGameOverPush(game.currentPlayer.displayName, game.waitingPlayer.userName, getEndScorePrefix(false));
		        	   } else {
		        		   game.save(true, false, false);
		        		   PushManager.sendGameUpdatePush(game.currentPlayer.displayName, game.waitingPlayer.userName);
		        	   }
		        	   showReadOnlyButtons();
		           }
		       })
		       .setNegativeButton("No", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		builder.show();
	}
	
	private void resign() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Are you sure you want to resign and lose this game?")
		       .setCancelable(false)
		       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   game.save(false, true, true);
	        		   PushManager.sendGameOverPush(game.currentPlayer.displayName, game.waitingPlayer.userName, "Won ");
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
	
	
	
	
	
	
	public void setupShakeListener() {
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorListener = new ShakeEventListener();   

        mSensorListener.setOnShakeListener(new ShakeEventListener.OnShakeListener() {

          public void onShake() {
            myTiles.shuffle();
          }
        });
	}

	public void updateCurrentScore() {
		boolean usedAllTiles = usedAllTiles();
		int points = getPointsForValidWord(usedAllTiles);
		currentScore.setText("" + points);
		currentScore.setTextColor(usedAllTiles ? getResources().getColor(R.color.orange) : getResources().getColor(R.color.text_light));
		currentScore.setTextSize(TypedValue.COMPLEX_UNIT_DIP, usedAllTiles ? 15 : 13);
	}

	
}
