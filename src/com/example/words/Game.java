package com.example.words;

import java.util.HashMap;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseObject;

public class Game implements Parcelable{
	
	private MainActivity activity;
	
	char[] gameBoard;
	char[] myTiles;
	char[] lastWord;
	boolean[] partOfLastWord;
	
	HashMap<String, Integer> bag;
	

	public Game(MainActivity activity) {
		this.activity = activity;
		
		initBag();
	}

	private void initBag() {
		bag = new HashMap<String, Integer>();
		String[] alpha = activity.getResources().getStringArray(R.array.alpha);
		int[] count = activity.getResources().getIntArray(R.array.count);
		
		for(int z = 0; z < alpha.length; z++){
			bag.put(alpha[z], count[z]);
		}
	}
	
	public String takeFromBag(){
		int randomLetter = (int) (Math.random() * bag.size());
		String key = activity.getAppController().getLetter(randomLetter);
		int remaining = bag.get(key);
		if(remaining > 0){
			bag.put(key, remaining--);
			return key;
		}
		return takeFromBag();
	}
	
	public boolean isBagEmpty(){
		for(int i : bag.values()){
			if(i > 0)
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
		dest.writeCharArray(gameBoard);
		dest.writeCharArray(myTiles);
		dest.writeCharArray(lastWord);
	}
	
	public ParseObject getParseObject(){
		ParseObject game = new ParseObject("Game");
		for(int z = 0; z < myTiles.length; z++){
			game.add("myTiles", "" + myTiles[z]);
		}
		for(int z = 0; z < gameBoard.length; z++){
			game.add("gameBoard", "" + gameBoard[z]);
		}
		for(int z = 0; z < lastWord.length; z++){
			game.add("lastWord", "" + lastWord[z]);
		}
		return game;
	}
	
	
}
