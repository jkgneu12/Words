package com.example.words;

import java.util.ArrayList;
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
	
	private ArrayList<String> gamesToRefresh;
	private ArrayList<GameUpdateListener> gameUpdateListeners;
	private ArrayList<ChatUpdateListener> chatUpdateListeners;
	
	public AppController() {
		super();
		gamesToRefresh = new ArrayList<String>();
		gameUpdateListeners = new ArrayList<GameUpdateListener>();
		chatUpdateListeners = new ArrayList<ChatUpdateListener>();
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

	public void registerGameUpdateListener(GameUpdateListener listener) {
		gameUpdateListeners.add(listener);
	}
	
	public void unregisterGameUpdateListener(GameUpdateListener listener) {
		gameUpdateListeners.remove(listener);
	}
	
	public void addGameToRefresh(String id){
		gamesToRefresh.add(id);
		for(GameUpdateListener listener : gameUpdateListeners){
			listener.refresh(id);
		}
	}

	public boolean removeGamesToRefresh(String id) {
		for(int z = 0; z < gamesToRefresh.size(); z++){
			if(gamesToRefresh.get(z).equals(id)){
				gamesToRefresh.remove(z);
				return true;
			}
		}
		return false;
	}

	public void refreshChat(String id) {
		for(ChatUpdateListener listener : chatUpdateListeners){
			listener.refresh(id);
		}
	}

	public void registerChatUpdateListener(ChatUpdateListener listener) {
		chatUpdateListeners.add(listener);
	}

	public void unregisterChatUpdateListener(ChatUpdateListener listener) {
		chatUpdateListeners.remove(listener);
	}

}
