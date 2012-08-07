package com.example.words.state;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

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
	public String[] myTiles;
	public String[] lastWord;
	public boolean[] partOfLastWord;
	
	public Map<String, Object> bag;
	
	public String currentPlayerId;
	public int currentPlayerScore;
	public String currentPlayerName;
	public String waitingPlayerId;
	public int waitingPlayerScore;
	public String waitingPlayerName;
	
	public List<String> usedWords; 

	private ParseObject parseObject;	

	public Game(GameActivity activity) {
		this.activity = activity;
		
		this.gameBoard = new String[Constants.NUM_TILE_HOLDERS];  
		this.myTiles = new String[Constants.NUM_MY_TILES];  
		this.lastWord = new String[Constants.NUM_TILE_HOLDERS];  
		
		usedWords = new ArrayList<String>();
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
		for(int z = 0; z < myTiles.length; z++){
			myTiles[z] = takeFromBag();
		}
	}
	
	public String takeFromBag(){
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
		myTiles = mt.getLetters();
		lastWord = lw.getLetters();
		partOfLastWord = gb.partOfLastWordArray();
	}
	
	public void save() {
		updateParseObject();
		parseObject.saveInBackground(new SaveCallback() {
			
			@Override
			public void done(ParseException e) {
				if(e != null)
					Log.e("Parse", "Could not save game");
			}
		});
	}
	
	private void updateParseObject(){
		if(parseObject == null)
			parseObject = new ParseObject("Game");
		
		parseObject.put("myTiles",Constants.arrayToList(myTiles));
		parseObject.put("lastWord",Constants.arrayToList(gameBoard));
		
		parseObject.put("bag", bag);
		
		parseObject.put("currentPlayerId", waitingPlayerId);
		parseObject.put("currentPlayerName", waitingPlayerName);
		parseObject.put("currentPlayerScore", waitingPlayerScore);
		parseObject.put("waitingPlayerId", currentPlayerId);
		parseObject.put("waitingPlayerName", currentPlayerName);
		parseObject.put("waitingPlayerScore", currentPlayerScore);
		
		parseObject.put("usedWords", usedWords);
	}
	
	public void refresh(){
		if(parseObject == null){
			ParseQuery query = new ParseQuery("Game");
			query.getInBackground(id, new GetCallback() {
				@Override
				public void done(ParseObject obj, ParseException e) {
					parseObject = obj;
					myTiles = Constants.listToArray(obj.getList("myTiles"));
					lastWord = Constants.listToArray(obj.getList("lastWord"));
					bag = obj.getMap("bag");
					usedWords = obj.getList("usedWords");
					replenishTiles();
					activity.refreshUIFromGame();
				}
			});
		} else {
			parseObject.refreshInBackground(null);
		}
	}
	
	public void replenishTiles() {
		for(int z = 0; z < myTiles.length; z++){
			if(Constants.isNull(myTiles[z]))
				myTiles[z] = takeFromBag();
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
		dest.writeStringArray(myTiles);
		dest.writeStringArray(lastWord);
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

	
	
}
