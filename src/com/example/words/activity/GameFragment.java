package com.example.words.activity;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.words.Constants;
import com.example.words.R;
import com.example.words.adapter.GameRowData;
import com.example.words.network.PushManager;
import com.example.words.state.Game;
import com.example.words.view.GameBoard;
import com.example.words.view.LastWord;
import com.example.words.view.LastWordTile;
import com.example.words.view.MyTiles;
import com.example.words.view.MyTilesTile;
import com.example.words.view.StarWarsScroller;
import com.example.words.view.Tile;

public class GameFragment extends Fragment implements OnClickListener{

	private LastWord lastWord;
	private GameBoard gameBoard;
	private MyTiles myTiles;
	private LinearLayout previousWords;
	private TextView remainingTiles;
	private TextView score;
	private Button submit;
	private Button reset;
	private Button pass;
	private Button resign;

	

	Game game;

	

	private Tile activeTile;

	
	private TextView currentScore;
	private GameActivity activity;
	private boolean isNewGame;
	public GameRowData gameData;
	private View view;
	private boolean couldntLayout;
	
	
	
	public GameFragment() {
		super();
	}

	public GameFragment(GameActivity activity, GameRowData gameData, boolean isNewGame) {
		super();
		this.activity = activity;
		this.gameData = gameData;
		this.isNewGame = isNewGame;
	}
	
	@Override
	public void onAttach(Activity activity) {
		this.activity = (GameActivity)activity;
		super.onAttach(activity);
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(view == null){
			view = inflater.inflate(R.layout.activity_game, null);
			
			gameBoard = (GameBoard)view.findViewById(R.id.game_board);
			gameBoard.setFragment(this);
			lastWord = (LastWord)view.findViewById(R.id.last_word);
			lastWord.setFragment(this);
			myTiles = (MyTiles)view.findViewById(R.id.my_tiles);
			myTiles.setFragment(this);
			remainingTiles = (TextView)view.findViewById(R.id.remaining_tiles);
			score = (TextView)view.findViewById(R.id.score);
			previousWords = (LinearLayout)view.findViewById(R.id.previous_words);
			submit = (Button)view.findViewById(R.id.submit);
			reset = (Button)view.findViewById(R.id.reset);
			pass = (Button)view.findViewById(R.id.pass);
			resign = (Button)view.findViewById(R.id.resign);
			currentScore = (TextView)view.findViewById(R.id.current_score);
	
			submit.setOnClickListener(this);
			reset.setOnClickListener(this);
			pass.setOnClickListener(this);
			reset.setOnClickListener(this);
			resign.setOnClickListener(this);
			
		} else {
			((FrameLayout)view.getParent()).removeView(view);
		}
		if(couldntLayout)
			refreshGameBoardUIFromGame();
		return view;
	}
	
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		if(savedInstanceState != null){
			game = savedInstanceState.getParcelable("game");
			gameData = savedInstanceState.getParcelable("gameData");
			isNewGame = savedInstanceState.getBoolean("isNewGame");
			
			if(game != null){	
			    refreshUIFromGame();
			} 
		} else if(activity.isFirstFragmentLoad){
			onFragmentShown();
			activity.isFirstFragmentLoad = false;
		}
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		if(!gameData.isCurrentPlayer || gameData.isGameOver){
			showReadOnlyButtons();
		} else if(isNewGame){
			showNewGameButtons();
		}
	}
	
	public void onFragmentShown(){
		if(game == null){
			game = new Game(activity, this, gameData, isNewGame, gameData.isCurrentPlayer);
			if(isNewGame)
				refreshUIFromGame();  
		} else
			refreshUIFromGame();
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}

	
	@Override
	public void onSaveInstanceState(Bundle bundle) {
		update();
		bundle.putParcelable("game", game);
		bundle.putParcelable("gameData", gameData);
		bundle.putBoolean("isNewGame", isNewGame);
		super.onSaveInstanceState(bundle);
	}

	

	public void update() {
		if(game != null){
			game.update(gameBoard, myTiles, lastWord);
			updateCurrentScore();
		} 
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
		if(myTiles == null){
			couldntLayout = true;
			return;
		}
		couldntLayout = false;
		if(myTiles.getChildCount() != Constants.countNonNulls(game.currentPlayer.tiles)){
			myTiles.removeAllViews();
			for(int z = 0; z < game.currentPlayer.tiles.length; z++){
				if(!Constants.isNull(game.currentPlayer.tiles[z]))
					myTiles.addView(new MyTilesTile(activity, this, "" + game.currentPlayer.tiles[z]));
			}
		}
		if(lastWord.getTileCount() != Constants.countNonNulls(game.lastTurn.currentLastWord)){
			lastWord.setCompleteLastWord(game.lastTurn.completeLastWord);
			lastWord.setCurrentLastWord(game.lastTurn.currentLastWord);
		}
		remainingTiles.setText(game.bag.remainingTiles() + " Tiles Left");
		setScoreText();
		
		Log.e("PREV CHILD COUNT", "" +previousWords.getChildCount());
		Log.e("PREV WORD COUNT", ""+game.prevWords.usedWords.size());
		setupPreviousWords();
		refreshGameBoardUIFromGame();
	}

	private void refreshGameBoardUIFromGame() {
		if(gameBoard.getChildCount() != Constants.countNonNulls(game.board.tiles)){
			gameBoard.removeAllViews();
			for(int z = 0; z < game.board.tiles.length; z++){
				if(!Constants.isNull(game.board.tiles[z]) && Character.isLetter(game.board.tiles[z].charAt(0))){
					Tile tile = Tile.create(activity, this, "" + game.board.tiles[z], game.board.indices[z], game.lastTurn.partOfLastWord[z]);
					gameBoard.addView(tile);
				}
			}
		}
		updateCurrentScore();
	}

	public void setupPreviousWords() {
		int prevWordCount = game.prevWords.usedWords.size();
		float fontSize = Constants.getPreviousWordSize(activity);
		
		if(previousWords.getChildCount() != game.prevWords.usedWords.size()){
			
			previousWords.removeAllViews();
	
			for(int z = 0; z < prevWordCount - 1; z++){
				LinearLayout row = (LinearLayout) ((LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.prev_word, previousWords, false);
	
				LinearLayout word = (LinearLayout)row.findViewById(R.id.b);
				String wordText = game.prevWords.usedWords.get(z);
				for(int y = 0; y < wordText.length(); y++){
					TextView letter = new TextView(activity);
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
					if(game.prevWords.turns.size() > z && game.prevWords.turns.get(z).equals(activity.currentUser.getObjectId())){
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
					scroll.invalidate();
				}
			});
		}
//		} else {
//			for(int z = 0; z < previousWords.getChildCount(); z++){
//				LinearLayout row = (LinearLayout)previousWords.getChildAt(z);
//				LinearLayout word = (LinearLayout)row.findViewById(R.id.b);
//				String wordText = game.prevWords.usedWords.get(z);
//				for(int y = 0; y < word.getChildCount(); y++){
//					TextView letter = (TextView) word.getChildAt(y);
//					letter.setTextSize(fontSize);
//				}
//			}
//			previousWords.invalidate();
//		}
	}

	public void addDummyPreviousWord(float fontSize) {
		LinearLayout row = (LinearLayout) ((LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.prev_word, previousWords, false);

		LinearLayout word = (LinearLayout)row.findViewById(R.id.b);
		TextView text = new TextView(activity);
		text.setText(" ");
		if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
			fontSize *= 1.3;
		text.setTextSize((int)fontSize);
		word.addView(text);

		previousWords.addView(row);
	}



	private void setScoreText() {
		String scoreText;
		if(gameData.isCurrentPlayer)
			scoreText = getScorePrefix() + game.currentPlayer.score + " : " + game.waitingPlayer.score;
		else
			scoreText = getScorePrefix() + game.waitingPlayer.score + " : " + game.currentPlayer.score;
		score.setText(scoreText);
	}

	private String getScorePrefix() {
		if(gameData.isGameOver)
			return getEndScorePrefix(gameData.isCurrentPlayer);
		else
			return getLiveScorePrefix();
	}

	private String getLiveScorePrefix(){
		if(gameData.isCurrentPlayer){
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
		String validation = Constants.startValidateGameBoard(activity, this, game, lastWord);
		if(validation != "1")
			Toast.makeText(activity, validation, Toast.LENGTH_LONG).show();
	}

	public void validated(JSONObject result) {
		if(result != null && result.has("value")){
			boolean usedAllTiles = usedAllTiles();
			int points = getPointsForValidWord(usedAllTiles);
			game.currentPlayer.replenishTiles();
			game.currentPlayer.incrementCurrentScore(points);
			game.prevWords.addGameBoardToUsedWord(points, gameBoard.getReusedIndices());
			game.save();
			Toast.makeText(activity, getMessageForValidWord(usedAllTiles, points), Toast.LENGTH_LONG).show();
			PushManager.sendGameUpdatePush(game.currentPlayer.displayName, game.waitingPlayer.userName);
			showReadOnlyButtons();
			refreshUIFromGame();
		}
		else
			Toast.makeText(activity, "Not a Word", Toast.LENGTH_SHORT).show();
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
		int points = activity.getAppController().getPoints(game.board.tiles);
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
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
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
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setMessage("Are you sure you want to resign and lose this game?")
		.setCancelable(false)
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				game.save(false, true, true);
				PushManager.sendGameOverPush(game.currentPlayer.displayName, game.waitingPlayer.userName, "Won ");
			}
		})
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		builder.show();
	}

	public void updateCurrentScore() {
		boolean usedAllTiles = usedAllTiles();
		int points = getPointsForValidWord(usedAllTiles);
		currentScore.setText("" + points);
		currentScore.setTextColor(usedAllTiles ? getResources().getColor(R.color.orange) : getResources().getColor(R.color.text_light));
		currentScore.setTextSize(TypedValue.COMPLEX_UNIT_DIP, usedAllTiles ? 15 : 13);
	}

	public void shuffleTiles() {
		myTiles.shuffle();
	}





	



}
