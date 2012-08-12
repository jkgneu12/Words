package com.example.words.view;

import android.graphics.drawable.Drawable;

import com.example.words.R;
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
	
	protected Drawable getBackgroundDrawable(){
		return activity.getResources().getDrawable(R.drawable.last_word_tile_background);
	}

	@Override
	protected Drawable getSelectedBackgroundDrawable() {
		return activity.getResources().getDrawable(R.drawable.selected_last_word_tile_background);
	}

}
