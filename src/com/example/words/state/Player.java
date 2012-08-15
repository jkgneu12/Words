package com.example.words.state;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.words.Constants;
import com.example.words.view.MyTiles;
import com.parse.ParseObject;

public class Player implements Parcelable{

	public String id;
	public int score;
	public String displayName;
	public String userName;
	public String[] tiles;
	
	private Game game;
	
	public Player(Game game) {
		this.game = game;
		this.tiles = new String[Constants.NUM_MY_TILES]; 
	}
	
	public void init(Intent intent, String prefix){
		id = intent.getStringExtra(prefix + "Id");
    	displayName = intent.getStringExtra(prefix + "Name");
    	userName = intent.getStringExtra(prefix + "UserName");
    	score = intent.getIntExtra(prefix + "Score", 0);
	}

	public void initMyTiles(){
		for(int z = 0; z < tiles.length; z++){
			tiles[z] = game.bag.takeFromBag();
		}
	}
	
	public void replenishTiles() {
		for(int z = 0; z < tiles.length; z++){
			if(Constants.isNull(tiles[z]))
				tiles[z] = game.bag.takeFromBag();
		}
	}
	
	public void incrementCurrentScore(int points) {
		score += points;
	}

	public ArrayList<String> getTilesList() {
		return Constants.arrayToList(tiles);
	}
	
	public void update(MyTiles mt) {
		tiles = mt.getLetters();
	}
	
	public void updateParseObject(ParseObject parseObject, String prefix) {
		parseObject.put(prefix + "Id", id);
		parseObject.put(prefix + "Name", displayName);
		parseObject.put(prefix + "UserName", userName);
		parseObject.put(prefix + "Score", score);
		parseObject.put(prefix + "Tiles", getTilesList());
	}
	
	
	

	public Player(Parcel in) {
		id = in.readString();
		displayName = in.readString();
		userName = in.readString();
		score = in.readInt();
		in.readStringArray(tiles);
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(displayName);
		dest.writeString(userName);
		dest.writeInt(score);
		dest.writeArray(tiles);
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	public static final Parcelable.Creator<Player> CREATOR
	= new Parcelable.Creator<Player>() {
		public Player createFromParcel(Parcel in) {
			return new Player(in);
		}

		public Player[] newArray(int size) {
			return new Player[size];
		}
	};

	
}
