package com.example.words.view;

import com.example.words.Constants;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public abstract class TileHolderSet extends LinearLayout {

	public TileHolderSet(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	protected TileHolder getTileHolderAt(int z) {
		return (TileHolder)getChildAt(z);
	}
	
	protected Tile getTileAt(int z){
		TileHolder holder = getTileHolderAt(z);
		if(holder != null)
			return holder.getTile();
		return null;
	}
	
	public boolean hasTileAt(int z) {
		return getTileHolderAt(z).getChildCount() > 0;
	}

	public String[] getLetters() {
		String[] letters = new String[Constants.NUM_TILE_HOLDERS];
		for(int z = 0; z < getChildCount(); z++){
			Tile t = getTileAt(z);
			if(t != null)
				letters[z] = t.getLetter();
		}
		return letters;
	}
	
}
