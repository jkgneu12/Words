package com.example.words;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

public class GameBoard extends LinearLayout {

	public GameBoard(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOrientation(HORIZONTAL);
		setGravity(Gravity.CENTER);
		setBackgroundColor(Color.WHITE);
	}

	public boolean hasTileAt(int z) {
		return getTileHolderAt(z).getChildCount() > 0;
	}

	

	public void highlight(int z) {
		getTileHolderAt(z).highlight();
	}
	
	public void unhighlight(int z) {
		getTileHolderAt(z).unhighlight();
	}
	
	private TileHolder getTileHolderAt(int z) {
		return (TileHolder)getChildAt(z);
	}

	public void addTile(Tile tile, int index) {
		getTileHolderAt(index).addView(tile);
	}

	

}
