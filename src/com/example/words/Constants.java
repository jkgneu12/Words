package com.example.words;

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
	
	public static boolean validate(String word)  {
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
		
		return false;
	}
}
