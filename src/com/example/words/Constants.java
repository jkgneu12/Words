package com.example.words;

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
}
