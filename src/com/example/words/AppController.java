package com.example.words;

import java.util.HashMap;

import android.app.Application;

public class AppController extends Application {
	
	public static int NUM_GAMEBOARD_TILES = 10;
	public static final int NUM_MY_TILES = 7;

	public static double VERSION = .1;
	
	public static final String UPDATE_SITE = "market://details?id=fm.asot";
	
	private String[] alpha;
	private HashMap<String, Integer> indices;
	private HashMap<String, Integer> points;
	
	public AppController() {
		super();
	}
	
	@Override
	public void onCreate() {
		initPointsAndIndices();
		super.onCreate();
	}

	private void initPointsAndIndices() {
		points = new HashMap<String, Integer>();
		indices = new HashMap<String, Integer>();
		alpha = getResources().getStringArray(R.array.alpha);
		int[] point = getResources().getIntArray(R.array.points);
		
		for(int z = 0; z < alpha.length; z++){
			points.put(alpha[z], point[z]);
			indices.put(alpha[z], z); 
		}
	}
	
	public int getPointsForValidWord(String[] tiles, boolean usedAllTiles) {
		int points = getPoints(tiles);
		if(usedAllTiles)
			points *= 2;
		return points;

	}
	
	public int getPoints(String c){
		if(Utils.isNull(c))
			return 0;
		return points.get(c.toLowerCase());
	}
	
	public int getPoints(String[] gameBoard) {
		int sum = 0;
		for(String c : gameBoard){
			sum += getPoints(c);
		}
		return sum;
	}
	
	public int getIndex(String c){
		return points.get(c.toLowerCase());
	}
	
	public String getLetter(int index){
		return alpha[index];
	}

}
