package com.example.words.view;

import com.example.words.activity.GameActivity;

import android.graphics.Color;

public class LastWordTile extends Tile  {
	
	private int index;

	public LastWordTile(GameActivity activity, String text, int index) {
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