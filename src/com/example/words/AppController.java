package com.example.words;

import java.util.HashMap;

import android.app.Application;
import android.content.Context;
import android.telephony.TelephonyManager;

public class AppController extends Application {
	
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
	
	public int getPoints(String c){
		if(c == null || c.equals("null"))
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
	public String getRandomLetter() {
		return alpha[(int)(Math.random() * alpha.length)];
	}



	

}
