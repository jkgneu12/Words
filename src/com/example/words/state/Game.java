package com.example.words.state;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.example.words.Constants;
import com.example.words.R;
import com.example.words.activity.GameActivity;
import com.example.words.view.GameBoard;
import com.example.words.view.LastWord;
import com.example.words.view.MyTiles;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

public class Game implements Parcelable{
	
	private GameActivity activity;
	
	public String id;
	
	public String[] gameBoard;
	public String[] currentLastWord;
	public String[] completeLastWord;
	public boolean[] partOfLastWord;
	
	public Map<String, Object> bag;
	
	public String currentPlayerId;
	public int currentPlayerScore;
	public String currentPlayerName;
	public String[] currentPlayerTiles;
	public String waitingPlayerId;
	public int waitingPlayerScore;
	public String waitingPlayerName;
	public String[] waitingPlayerTiles;
	public boolean lastPlayerPassed;	
	
	public List<String> usedWords; 

	private ParseObject parseObject;

	public Game(GameActivity activity) {
		this.activity = activity;
		
		this.gameBoard = new String[Constants.NUM_TILE_HOLDERS];  
		this.currentPlayerTiles = new String[Constants.NUM_MY_TILES]; 
		this.waitingPlayerTiles = new String[Constants.NUM_MY_TILES]; 
		this.currentLastWord = new String[Constants.NUM_TILE_HOLDERS];  
		this.completeLastWord = new String[Constants.NUM_TILE_HOLDERS];  
		this.lastPlayerPassed = false;
		
		usedWords = new ArrayList<String>();
	}

	public Game(Parcel in) {
		in.readStringArray(gameBoard);
		in.readStringArray(currentPlayerTiles);
		in.readStringArray(waitingPlayerTiles);
		in.readStringArray(currentLastWord);
		in.readStringArray(completeLastWord);
		in.readBooleanArray(partOfLastWord);
		id = in.readString();
		in.readMap(bag, null);
		currentPlayerId = in.readString();
		currentPlayerScore = in.readInt();
		currentPlayerName = in.readString();
		waitingPlayerId = in.readString();
		waitingPlayerScore = in.readInt();
		waitingPlayerName = in.readString();
		in.readList(usedWords, null);
	}

	public void initBag() {
		bag = new HashMap<String, Object>();
		String[] alpha = activity.getResources().getStringArray(R.array.alpha);
		int[] count = activity.getResources().getIntArray(R.array.count);
		
		for(int z = 0; z < alpha.length; z++){
			bag.put(alpha[z], count[z]);
		}
	}
	
	public void initMyTiles(){
		for(int z = 0; z < currentPlayerTiles.length; z++){
			currentPlayerTiles[z] = takeFromBag();
			waitingPlayerTiles[z] = takeFromBag();
		}
	}
	
	public String takeFromBag(){
		if(isBagEmpty())
			return null;
		int randomLetter = (int) (Math.random() * bag.size());
		String key = activity.getAppController().getLetter(randomLetter);
		int remaining = (Integer)bag.get(key);
		if(remaining > 0){
			bag.put(key, remaining - 1);
			return key;
		}
		return takeFromBag();
	}
	
	public boolean isBagEmpty(){
		for(String c : bag.keySet()){
			if((Integer)bag.get(c) > 0)
				return false;
		}
		return true;
	}
	
	public int remainingTiles(){
		int sum = 0;
		for(String c : bag.keySet())
			sum += (Integer)bag.get(c);
		return sum;
	}

	public void update(GameBoard gb, MyTiles mt, LastWord lw) {
		gameBoard = gb.getLetters();
		currentPlayerTiles = mt.getLetters();
		currentLastWord = lw.getLetters();
		partOfLastWord = gb.partOfLastWordArray();
	}
	
	public void save(){
		save(false, false);
	}
	
	public void save(boolean passing, boolean gameOver) {
		updateParseObject(passing, gameOver);
		parseObject.saveInBackground(new SaveCallback() {
			
			@Override
			public void done(ParseException e) {
				if(e != null)
					Log.e("Parse", "Could not save game");
			}
		});
	}
	
	private void updateParseObject(boolean passing, boolean gameOver){
		if(parseObject == null)
			parseObject = new ParseObject("Game");
		
		if(!passing)
			parseObject.put("lastWord",Constants.arrayToList(gameBoard));
		
		parseObject.put("passed", passing);
		parseObject.put("gameOver", gameOver);
		
		parseObject.put("bag", bag);
		
		parseObject.put("currentPlayerId", waitingPlayerId);
		parseObject.put("currentPlayerName", waitingPlayerName);
		parseObject.put("currentPlayerScore", waitingPlayerScore);
		parseObject.put("currentPlayerTiles",Constants.arrayToList(waitingPlayerTiles));
		parseObject.put("waitingPlayerId", currentPlayerId);
		parseObject.put("waitingPlayerName", currentPlayerName);
		parseObject.put("waitingPlayerScore", currentPlayerScore);
		parseObject.put("waitingPlayerTiles",Constants.arrayToList(currentPlayerTiles));
		parseObject.put("myTiles",Constants.arrayToList(currentPlayerTiles));
		
		parseObject.put("usedWords", usedWords);
	}
	
	public void refresh(){
		if(parseObject == null){
			final ProgressDialog waiting = new ProgressDialog(activity, ProgressDialog.STYLE_SPINNER);
			waiting.setTitle("Please Wait");
			waiting.setMessage("Loading Game vs. " + waitingPlayerName);
			waiting.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					activity.finish();
				}
			});
			waiting.show();
			ParseQuery query = new ParseQuery("Game");
			query.getInBackground(id, new GetCallback() {
				@Override
				public void done(ParseObject obj, ParseException e) {
					if(e == null){
						parseObject = obj;
						List temp = obj.getList("currentPlayerTiles");
						if(temp == null)
							currentPlayerTiles = Constants.listToArray(obj.getList("myTiles"));
						else
							currentPlayerTiles = Constants.listToArray(temp);
						temp = obj.getList("waitingPlayerTiles");
						if(temp != null)
							waitingPlayerTiles = Constants.listToArray(temp);
						else
							waitingPlayerTiles = currentPlayerTiles;
						currentLastWord = Constants.listToArray(obj.getList("lastWord"));
						completeLastWord = Constants.listToArray(obj.getList("lastWord"));
						bag = obj.getMap("bag");
						usedWords = obj.getList("usedWords");
						lastPlayerPassed = obj.getBoolean("passed");
						replenishTiles();
						activity.refreshUIFromGame();
						
					} else {
						Toast.makeText(activity, "Game Failed to Load. Please try again.", Toast.LENGTH_LONG).show();
						activity.finish();
					}
					waiting.dismiss();
				}
			});
		} else {
			parseObject.refreshInBackground(null);
		}
	}
	
	public void replenishTiles() {
		for(int z = 0; z < currentPlayerTiles.length; z++){
			if(Constants.isNull(currentPlayerTiles[z]))
				currentPlayerTiles[z] = takeFromBag();
		}
	}

	public void incrementCurrentScore(int points) {
		currentPlayerScore += points;
	}
	
	public void addGameBoardToUsedWord() {
		usedWords.add(Constants.arrayToString(gameBoard));
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeStringArray(gameBoard);
		dest.writeStringArray(currentPlayerTiles);
		dest.writeStringArray(waitingPlayerTiles);
		dest.writeStringArray(currentLastWord);
		dest.writeStringArray(completeLastWord);
		dest.writeBooleanArray(partOfLastWord);
		dest.writeString(id);
		dest.writeMap(bag);
		dest.writeString(currentPlayerId);
		dest.writeInt(currentPlayerScore);
		dest.writeString(currentPlayerName);
		dest.writeString(waitingPlayerId);
		dest.writeInt(waitingPlayerScore);
		dest.writeString(waitingPlayerName);
		dest.writeList(usedWords);
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
