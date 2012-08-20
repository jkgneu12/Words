package com.example.words.view;

import java.util.Stack;

import android.content.Context;
import android.util.AttributeSet;

import com.example.words.Constants;
import com.example.words.R;

public class MyTiles extends FreeFormBoard {
	
	public MyTiles(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	
	
	protected boolean canDrop(Tile tile){
		return !tile.isPartOfLastWord();
	}
	
	public boolean usedAllTiles() {
		return getChildCount() == 0;
	}

	public void shuffle() {
		Stack<Tile> tiles = new Stack<Tile>();
		while(getChildCount() > 0){
			int index = (int)(Math.random() * getChildCount());
			tiles.push(getTileAt(index));
			removeViewAt(index);
		}
		while(!tiles.isEmpty()){
			addView(tiles.pop());
		}
	}



	@Override
	protected void goodHighlight() {
		setBackgroundResource(R.drawable.my_tiles_higlight_backround);
	}

	@Override
	protected void unhighlight() {
		setBackgroundResource(R.drawable.my_tiles_backround);
	}



	@Override
	protected int getMaxNumTiles() {
		return Constants.NUM_MY_TILES;
	}
}
