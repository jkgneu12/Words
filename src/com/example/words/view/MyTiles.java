package com.example.words.view;

import java.util.Stack;

import android.content.Context;
import android.util.AttributeSet;

import com.example.words.AppController;
import com.example.words.R;
import com.example.words.Utils;

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
		return AppController.NUM_MY_TILES;
	}



	public void refreshUI(String[] letters, boolean force) {
		if(force || getChildCount() != Utils.countNonNulls(letters)){
			removeAllViews();
			for(int z = 0; z < letters.length; z++){
				if(!Utils.isNull(letters[z]))
					addView(new MyTilesTile(activity, fragment, letters[z]));
			}
		}
	}
}
