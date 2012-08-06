package com.example.words;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONObject;

import android.app.Activity;
import android.util.DisplayMetrics;

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
	
	public static boolean validate(String[] wordArray)  {
		return true;
		/*String word = arrayToString(wordArray);
		word = word.trim().toLowerCase();
		String url = "http://api.wordnik.com//v4/word.json/" + word + "/scrabbleScore?api_key=be7067c9f3d5828a9e0e618f32f08a06c3d0e3e3a6abad472";

		try {
			GetTask task = new GetTask(); 
			task.execute(url);
			JSONObject json = task.get();
			return json != null && json.has("value");
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return false;*/
	}

	public static String arrayToString(String[] wordArray) {
		StringBuilder s = new StringBuilder(wordArray.length);
		for(int z = 0; z < wordArray.length; z++){
			if(wordArray[z] != null && !wordArray[z].equals("null"))
				s.append(wordArray[z]);
		}
		return s.toString();
	}
	
	public static ArrayList<String> arrayToList(String[] wordArray) {
		ArrayList<String> list = new ArrayList<String>(wordArray.length);
		for(int z = 0; z < wordArray.length; z++){
			if(wordArray[z] == null)
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
}
