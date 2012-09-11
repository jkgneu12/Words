package com.example.words.view;

import android.graphics.drawable.Drawable;

import com.example.words.R;
import com.example.words.activity.GameActivity;
import com.example.words.activity.GameFragment;

public class MyTilesTile extends Tile {

	public MyTilesTile(GameActivity activity, GameFragment fragment, String text) {
		super(activity, fragment, text);
	}

	@Override
	public boolean isPartOfLastWord() {
		return false;
	}
	
	@Override
	public String toString() {
		return "MyTiles" + super.toString();
	}

	@Override
	protected Drawable getBackgroundDrawable() {
		return activity.getResources().getDrawable(R.drawable.my_tile_background);
	}
	
	@Override
	protected Drawable getSelectedBackgroundDrawable() {
		return activity.getResources().getDrawable(R.drawable.selected_my_tile_background);
	}

}
