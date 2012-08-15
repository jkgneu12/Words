package com.example.words.state;

import java.util.HashMap;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.words.R;
import com.example.words.activity.GameActivity;
import com.parse.ParseObject;

public class Bag implements Parcelable{
	
	private GameActivity activity;
	private Game game;
	
	public Map<String, Object> tiles;
	
	public Bag(GameActivity activity, Game game) {
		this.activity = activity;
		this.game = game;
	}
	
	public void initBag() {
		String[] alpha = activity.getResources().getStringArray(R.array.alpha);
		int[] count = activity.getResources().getIntArray(R.array.count);
		
		tiles = new HashMap<String, Object>();
		
		for(int z = 0; z < alpha.length; z++){
			tiles.put(alpha[z], count[z]);
		}
	}
	
	public String takeFromBag(){
		if(isBagEmpty())
			return null;
		int randomLetter = (int) (Math.random() * tiles.size());
		String key = activity.getAppController().getLetter(randomLetter);
		int remaining = (Integer)tiles.get(key);
		if(remaining > 0){
			tiles.put(key, remaining - 1);
			return key;
		}
		return takeFromBag();
	}
	
	public boolean isBagEmpty(){
		for(String c : tiles.keySet()){
			if((Integer)tiles.get(c) > 0)
				return false;
		}
		return true;
	}
	
	public int remainingTiles(){
		int sum = 0;
		for(String c : tiles.keySet())
			sum += (Integer)tiles.get(c);
		return sum;
	}
	
	public void refresh(ParseObject obj) {
		tiles = obj.getMap("bag");
	}
	
	
	
	
	
	
	public Bag(Parcel in) {
		tiles = in.readHashMap(null);
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeMap(tiles);
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	public static final Parcelable.Creator<Bag> CREATOR
	= new Parcelable.Creator<Bag>() {
		public Bag createFromParcel(Parcel in) {
			return new Bag(in);
		}

		public Bag[] newArray(int size) {
			return new Bag[size];
		}
	};

}
