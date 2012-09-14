package com.example.words.activity;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.words.R;
import com.example.words.Utils;
import com.example.words.adapter.GameRowData;
import com.example.words.network.PushManager;
import com.example.words.state.Game;
import com.example.words.view.CurrentScore;
import com.example.words.view.GameBoard;
import com.example.words.view.LastWord;
import com.example.words.view.LastWordTile;
import com.example.words.view.MyTiles;
import com.example.words.view.PreviousWordsLayout;
import com.example.words.view.RemainingTiles;
import com.example.words.view.Score;
import com.example.words.view.Tile;

public class GameFragment extends Fragment implements OnClickListener{

	private LastWord lastWord;
	private GameBoard gameBoard;
	private MyTiles myTiles;
	private PreviousWordsLayout previousWords;
	private RemainingTiles remainingTiles;
	private Score score;
	private CurrentScore currentScore;
	
	private Button submit;
	private Button reset;
	private Button pass;
	private Button resign;

	

	Game game;

	

	private Tile activeTile;

	
	
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
			view = inflater.inflate(R.layout.fragment_game, null);
			
			gameBoard = (GameBoard)view.findViewById(R.id.game_board);
			lastWord = (LastWord)view.findViewById(R.id.last_word);
			myTiles = (MyTiles)view.findViewById(R.id.my_tiles);
			remainingTiles = (RemainingTiles)view.findViewById(R.id.remaining_tiles);
			score = (Score)view.findViewById(R.id.score);
			previousWords = (PreviousWordsLayout)view.findViewById(R.id.previous_words);
			submit = (Button)view.findViewById(R.id.submit);
			reset = (Button)view.findViewById(R.id.reset);
			pass = (Button)view.findViewById(R.id.pass);
			resign = (Button)view.findViewById(R.id.resign);
			currentScore = (CurrentScore)view.findViewById(R.id.current_score);
			
			gameBoard.setFragment(this);
			lastWord.setFragment(this);
			myTiles.setFragment(this);
	
			submit.setOnClickListener(this);
			reset.setOnClickListener(this);
			pass.setOnClickListener(this);
			reset.setOnClickListener(this);
			resign.setOnClickListener(this);
			
		} else {
			((FrameLayout)view.getParent()).removeView(view);
		}
		if(couldntLayout)
			refreshUI(true, false);
		return view;
	}
	
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		if(savedInstanceState != null){
			game = savedInstanceState.getParcelable("game");
			gameData = savedInstanceState.getParcelable("gameData");
			isNewGame = savedInstanceState.getBoolean("isNewGame");
			
			if(game != null){	
			    refreshUI(false, false);
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
		} else {
			showAllButtons();
		}
	}
	
	public void onFragmentShown(){
		if(game == null){
			game = new Game(activity, this, gameData, isNewGame, gameData.isCurrentPlayer);
			if(isNewGame)
				refreshUI(false, false);  
		} else if (activity.getAppController().removeGamesToRefresh(game.id)){
			game.fullRefresh();
		} else
			refreshUI(false, false);
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
			currentScore.refreshUI(game.board.tiles, usedAllTiles());
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

	public void refreshUI(boolean force, boolean showAllButtons){
		if(myTiles == null){
			couldntLayout = true;
			return;
		}
		couldntLayout = false;
		
		myTiles.refreshUI(game.currentPlayer.tiles, force);
		lastWord.refreshUI(game.lastTurn.currentLastWord, game.lastTurn.completeLastWord, force);
		previousWords.refreshUI(game.prevWords, force);
		gameBoard.refreshUI(game.board, game.lastTurn, force);
		remainingTiles.refreshUI(game.bag);
		score.refreshUI(game, gameData);
		currentScore.refreshUI(game.board.tiles, usedAllTiles());
		if(showAllButtons)
			showAllButtons();
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
		String validation = Utils.startValidateGameBoard(activity, this, game, lastWord);
		if(validation != "1")
			Toast.makeText(activity, validation, Toast.LENGTH_LONG).show();
	}

	public void validated(JSONObject result) {
		if(result != null && result.has("value")){
			boolean usedAllTiles = usedAllTiles();
			int points = activity.getAppController().getPointsForValidWord(game.board.tiles, usedAllTiles);
			game.currentPlayer.replenishTiles();
			game.currentPlayer.incrementCurrentScore(points);
			game.prevWords.addGameBoardToUsedWord(points, gameBoard.getReusedIndices());
			game.save();
			game.lastTurn.completeLastWord = gameBoard.getLetters();
			game.lastTurn.currentLastWord = gameBoard.getLetters();
			game.board.clearTiles();
			Toast.makeText(activity, getMessageForValidWord(usedAllTiles, points), Toast.LENGTH_LONG).show();
			PushManager.sendGameUpdatePush(game.currentPlayer.displayName, game.waitingPlayer.userName, game.id);
			showReadOnlyButtons();
			refreshUI(true, false);
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
	
	private void showAllButtons() {
		submit.setVisibility(View.VISIBLE);
		resign.setVisibility(View.VISIBLE);
		pass.setVisibility(View.VISIBLE);
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
					PushManager.sendGameOverPush(game.currentPlayer.displayName, game.waitingPlayer.userName, score.getEndScorePrefix(game, gameData), game.id);
				} else {
					game.save(true, false, false);
					PushManager.sendGameUpdatePush(game.currentPlayer.displayName, game.waitingPlayer.userName, game.id);
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
				PushManager.sendGameOverPush(game.currentPlayer.displayName, game.waitingPlayer.userName, "Won ", game.id);
			}
		})
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		builder.show();
	}

	

	public void shuffleTiles() {
		myTiles.shuffle();
	}


}
