package com.example.words;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONObject;

import android.app.Activity;
import android.util.DisplayMetrics;

import com.example.words.state.Game;
import com.example.words.view.LastWord;
import com.parse.Parse;

public class Constants {

	public static final int NUM_TILE_HOLDERS = 10;
	public static final int NUM_MY_TILES = 7;
	
	public static int getActivityWidth(Activity activity){
		DisplayMetrics pSize = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(pSize);
		return pSize.widthPixels;
	}
	
	public static int getTileDimensions(Activity activity){
		return (getActivityWidth(activity) / Constants.NUM_TILE_HOLDERS) - 10;
	}

	public static int getTileMargin(Activity activity, int numTiles) {
		int spaceForEachTile = getActivityWidth(activity) / numTiles;
		return (spaceForEachTile - getTileDimensions(activity)) / 2;
	}
	
	public static String validateGameBoard(Game game, LastWord lastWord)  {
		if(!lastWord.usedAtLeastOneTile())
			return "You must use at least 1 tile from the last word";
		
		String[] wordArray = game.gameBoard;
		String word = arrayToString(wordArray).trim().toUpperCase();
		if(game.usedWords.contains(word)) 
			return "Already Used";
		
		String lastWordString = lastWord.getLastWord().toUpperCase();
		if(!isNullOrEmpty(lastWordString) && word.contains(lastWordString))
			return "Can't reuse the last word with rearranging the letters";
		
		word = word.toLowerCase();
		
		String url = "http://api.wordnik.com//v4/word.json/" + word + "/scrabbleScore?api_key=be7067c9f3d5828a9e0e618f32f08a06c3d0e3e3a6abad472";
		try {
			GetTask task = new GetTask(); 
			task.execute(url);
			JSONObject json = task.get();
			if(json != null && json.has("value"))
				return "1";
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return "Not a Word";
	}

	public static String arrayToString(String[] wordArray) {
		StringBuilder s = new StringBuilder(wordArray.length);
		for(int z = 0; z < wordArray.length; z++){
			if(!isNull(wordArray[z]))
				s.append(wordArray[z]);
		}
		return s.toString();
	}
	
	public static ArrayList<String> arrayToList(String[] wordArray) {
		ArrayList<String> list = new ArrayList<String>(wordArray.length);
		for(int z = 0; z < wordArray.length; z++){
			if(isNull(wordArray[z]))
				list.add("null");
			else
				list.add(wordArray[z]);
		}
		return list;
	}
	
	public static ArrayList<Integer> arrayToList(int[] intArray) {
		ArrayList<Integer> list = new ArrayList<Integer>(intArray.length);
		for(int z = 0; z < intArray.length; z++){
			list.add(intArray[z]);
		}
		return list;
	}

	public static String[] listToArray(List<Object> list) {
		String[] array = new String[list.size()];
		list.toArray(array);
		return array;
	}

	public static boolean isNull(String string) {
		return string == null || string.equals("null");
	}

	public static boolean isNullOrEmpty(String string) {
		return isNull(string) || string.length() <= 0;
	}
	
	public static void initParse(Activity a){
		Parse.initialize(a, "VhnMRCE8J0r9fJLuXvGWMQvdNEw6GSxoAQCApqf2", "r4BwcVVLoX7wo92garHMfPa10O6xdmlVIS57ymt8");
	}
}
