package com.example.words;

import android.graphics.Color;

public class LastWordTile extends Tile  {
	
	private int index;

	public LastWordTile(MainActivity activity, String text, int index) {
		super(activity, text);
		
		this.index = index;
		
		setBackgroundColor(Color.YELLOW );
	}

	@Override
	public boolean isPartOfLastWord() {
		return true;
	}
	
	@Override
	public String toString() {
		return "LastWord" + super.toString();
	}

	public int getIndex() {
		return index;
	}

}
