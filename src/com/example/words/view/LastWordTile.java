package com.example.words.view;

import android.graphics.Color;

import com.example.words.activity.GameActivity;

public class LastWordTile extends Tile  {
	
	private int index;

	public LastWordTile(GameActivity activity, String text, int index) {
		super(activity, text);
		
		this.index = index;
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
	
	protected int getBackgroundColor(){
		return Color.YELLOW;
	}

}
