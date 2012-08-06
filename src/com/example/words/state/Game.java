package com.example.words.state;

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

	private ParseObject parseObject;	

	public Game(GameActivity activity) {
		this.activity = activity;
		
		initBag();
		
		this.gameBoard = new String[Constants.NUM_TILE_HOLDERS];  
		this.myTiles = new String[Constants.NUM_MY_TILES];  
		this.lastWord = new String[Constants.NUM_TILE_HOLDERS];  
	}
	
	public void randomTiles(){
		for(int z = 0; z < myTiles.length; z++){
			myTiles[z] = activity.getAppController().getRandomLetter();
		}
	}

	private void initBag() {
		bag = new HashMap<String, Object>();
		String[] alpha = activity.getResources().getStringArray(R.array.alpha);
		int[] count = activity.getResources().getIntArray(R.array.count);
		
		for(int z = 0; z < alpha.length; z++){
			bag.put(alpha[z], count[z]);
		}
	}
	
	public String takeFromBag(){
		int randomLetter = (int) (Math.random() * bag.size());
		String key = activity.getAppController().getLetter(randomLetter);
		int remaining = (Integer)bag.get(key);
		if(remaining > 0){
			bag.put(key, remaining--);
			return key;
		}
		return takeFromBag();
	}
	
	public boolean isBagEmpty(){
		for(int z = 0; z < bag.size(); z++){
			if((Integer)bag.get(z) > 0)
				return false;
		}
		return true;
	}

	public void update(GameBoard gb, MyTiles mt, LastWord lw) {
		gameBoard = gb.getLetters();
		myTiles = mt.getLetters();
		lastWord = lw.getLetters();
		partOfLastWord = gb.partOfLastWordArray();
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeStringArray(gameBoard);
		dest.writeStringArray(myTiles);
		dest.writeStringArray(lastWord);
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
	}

	public void save() {
		updateParseObject();
		parseObject.saveInBackground(new SaveCallback() {
			
			@Override
			public void done(ParseException e) {
				Log.e("Parse", "Could not save game");
			}
		});
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
					replenishTiles();
					activity.refreshUIFromGame();
				}
			});
		} else {
			parseObject.refreshInBackground(null);
		}
	}
	
	private void replenishTiles() {
		for(int z = 0; z < myTiles.length; z++){
			if(myTiles[z] == null || myTiles[z].equals("null"))
				myTiles[z] = takeFromBag();
		}
	}

	public void incrementCurrentScore(int points) {
		currentPlayerScore += points;
	}
	
	
}
