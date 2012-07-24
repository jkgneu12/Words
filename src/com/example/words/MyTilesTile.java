package com.example.words;

import android.graphics.Color;

public class MyTilesTile extends Tile {

	public MyTilesTile(MainActivity activity, String text) {
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
