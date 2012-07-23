package com.example.words;

import java.util.HashMap;

import android.app.Application;

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
		return points.get(c.toLowerCase());
	}
	
	public int getIndex(String c){
		return points.get(c.toLowerCase());
	}
	
	public String getLetter(int index){
		return alpha[index];
	}

}
