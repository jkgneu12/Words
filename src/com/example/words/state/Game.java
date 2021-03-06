package com.example.words.state;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;

import com.example.words.AppController;
import com.example.words.Utils;
import com.example.words.activity.GameActivity;
import com.example.words.activity.GameFragment;
import com.example.words.adapter.GameRowData;
import com.example.words.view.GameBoard;
import com.example.words.view.LastWord;
import com.example.words.view.MyTiles;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

public class Game implements Parcelable{
	
	public GameActivity activity;
	private ParseObject parseObject;
	
	public String id;

	public boolean isMyTurn = false;

	public Player currentPlayer;
	public Player waitingPlayer;

	public Board board;
	public Bag bag;
	
	public PreviousWords prevWords;
	public LastTurn lastTurn;
	private GameFragment fragment;

	

	public Game(GameActivity activity, GameFragment fragment, GameRowData gameData, boolean isNewGame, boolean isMyTurn) {
		this.activity = activity;
		this.fragment = fragment;
		
		this.currentPlayer = new Player(this);
		this.waitingPlayer = new Player(this);
		
		this.board = new Board(this);
		this.bag = new Bag(activity);
		
		this.prevWords = new PreviousWords(this);
		
		this.lastTurn = new LastTurn();
		
		this.isMyTurn = isMyTurn;
		
		init(gameData, isNewGame);
	}

	public void init(GameRowData gameData, boolean isNewGame) {
		if(isNewGame){
        	bag.initBag();
        	initMyTiles();
        	setupUsers(gameData);
    	} else {
    		this.id = gameData.id;
    		setupUsers(gameData);
    		refresh(false);
    	}
	}
	
	public void initMyTiles(){
		currentPlayer.initMyTiles();
		waitingPlayer.initMyTiles();
	}
	
	public void setupUsers(GameRowData gameData) {
		currentPlayer.init(gameData, isMyTurn);
		waitingPlayer.init(gameData, !isMyTurn);
	}
	
	public void update(GameBoard gb, MyTiles mt, LastWord lw) {
		board.update(gb);
		currentPlayer.update(mt);
		lastTurn.update(gb, lw);
	}
	
	public void save(){
		save(false, false, false);
	}
	
	public void save(boolean passing, boolean gameOver, boolean resigning) {
		updateParseObject(passing, gameOver);
		parseObject.saveInBackground(new SaveCallback() {
			
			@Override
			public void done(ParseException e) {
				if(e != null)
					Utils.handleParseErrors(e, activity);
			}
		});
	}
	
	private void updateParseObject(boolean passing, boolean gameOver){
		if(parseObject == null)
			parseObject = new ParseObject("Game");
		
		if(!passing)
			parseObject.put("lastWord", board.getTilesList());
		
		parseObject.put("passed", passing);
		parseObject.put("gameOver", gameOver);
		
		parseObject.put("bag", bag.tiles);
		
		currentPlayer.updateParseObject(parseObject, "waitingPlayer");
		waitingPlayer.updateParseObject(parseObject, "currentPlayer");
		
		prevWords.updateParseObject(parseObject);
		
		parseObject.put("Version", AppController.VERSION);
	}
	
	public void refresh(boolean force){
		if(parseObject == null){
			ProgressDialog waiting = new ProgressDialog(activity, ProgressDialog.STYLE_SPINNER);
			waiting.setTitle("Please Wait");
			waiting.setMessage("Loading Game vs. " + waitingPlayer.displayName);
			waiting.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					activity.finish();
				}
			});
			waiting.show();
			
			
			queryForParseObject(waiting, force);
		} else {
			parseObject.refreshInBackground(null);
		}
	}
	
	public void fullRefresh(){
		parseObject = null;
		refresh(true);
	}

	public void queryForParseObject(final ProgressDialog waiting, final boolean force) {
		ParseQuery query = new ParseQuery("Game");
		query.getInBackground(id, new GetCallback() {
			@Override
			public void done(ParseObject obj, ParseException e) {
				if(e == null){
					parseObject = obj;
					
					isMyTurn = obj.getString("currentPlayerId").equals(activity.currentUser.getObjectId());
					
					refreshPlayers(obj);
					lastTurn.refresh(obj);
					prevWords.refresh(obj);
					bag.refresh(obj);
					
					currentPlayer.replenishTiles();//they should be full, but just in case something went wrong
					fragment.refreshUI(force, isMyTurn);
					
				} else {
					Toast.makeText(activity, "Game Failed to Load. Please try again.", Toast.LENGTH_LONG).show();
					activity.finish();
				}
				waiting.dismiss();
			}

			public void refreshPlayers(ParseObject obj) {
				currentPlayer.init(obj, isMyTurn);
				waitingPlayer.init(obj, !isMyTurn);
			}
		});
	}

	
	
	
	
	
	
	
	
	
	
	public Game(Parcel in) {
		currentPlayer = in.readParcelable(null);
		waitingPlayer = in.readParcelable(null);
		board = in.readParcelable(null);
		bag = in.readParcelable(null);
		prevWords = in.readParcelable(null);
		lastTurn = in.readParcelable(null);
		id = in.readString();
	}
	
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(currentPlayer, 0);
		dest.writeParcelable(waitingPlayer, 0);
		dest.writeParcelable(board, 0);
		dest.writeParcelable(bag, 0);
		dest.writeParcelable(prevWords, 0);
		dest.writeParcelable(lastTurn, 0);
		dest.writeString(id);
	}
	
	public static final Parcelable.Creator<Game> CREATOR
	= new Parcelable.Creator<Game>() {
		public Game createFromParcel(Parcel in) {
			return new Game(in);
		}

		public Game[] newArray(int size) {
			return new Game[size];
		}
	};



	

	
	
}
