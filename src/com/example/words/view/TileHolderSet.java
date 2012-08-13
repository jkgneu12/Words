package com.example.words.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.example.words.activity.GameActivity;

public abstract class TileHolderSet extends LinearLayout {

	protected GameActivity activity;

	public TileHolderSet(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.activity = (GameActivity)context;
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

	public abstract String[] getLetters();
	
}
