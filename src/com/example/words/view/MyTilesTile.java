package com.example.words.view;

import android.graphics.Color;

import com.example.words.activity.GameActivity;

public class MyTilesTile extends Tile {

	public MyTilesTile(GameActivity activity, String text) {
		super(activity, text);
		
		setBackgroundColor(Color.CYAN);
	}

	@Override
	public boolean isPartOfLastWord() {
		return false;
	}
	
	@Override
	public String toString() {
		return "MyTiles" + super.toString();
	}

}
