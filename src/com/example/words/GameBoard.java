package com.example.words;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
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

	

	public void goodHighlight(int z) {
		getTileHolderAt(z).goodHighlight();
	}
	
	public void badHighlight(int z) {
		getTileHolderAt(z).badHighlight();
	}
	
	public void unhighlight(int z) {
		getTileHolderAt(z).unhighlight();
	}
	
	private TileHolder getTileHolderAt(int z) {
		return (TileHolder)getChildAt(z);
	}
	
	private Tile getTileAt(int z){
		TileHolder holder = getTileHolderAt(z);
		if(holder != null)
			return holder.getTile();
		return null;
	}

	public void addTile(Tile tile, int index) {
		getTileHolderAt(index).addView(tile);
	}

	public char[] getLetters() {
		char[] letters = new char[Constants.NUM_TILE_HOLDERS];
		for(int z = 0; z < getChildCount(); z++){
			Tile t = getTileAt(z);
			if(t != null)
				letters[z] = t.getLetter();
		}
		return letters;
	}

	public boolean[] partOfLastWordArray() {
		boolean[] partOfLastWord = new boolean[Constants.NUM_TILE_HOLDERS];
		for(int z = 0; z < getChildCount(); z++){
			Tile t = getTileAt(z);
			if(t != null)
				partOfLastWord[z] = t.isPartOfLastWord();
		}
		return partOfLastWord;
	}

	public void reset() {
		for(int z = 0; z < getChildCount(); z++){
			Tile t = getTileAt(z);
			if(t != null){
				((MainActivity)getContext()).returnTile(t);
			}
				
		}
	}

	

}
