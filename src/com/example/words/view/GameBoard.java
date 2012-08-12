package com.example.words.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;

import com.example.words.Constants;
import com.example.words.activity.GameActivity;

public class GameBoard extends TileHolderSet {

	public GameBoard(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOrientation(HORIZONTAL);
		setGravity(Gravity.CENTER);
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

	public void addTile(Tile tile, int index) {
		getTileHolderAt(index).addView(tile);
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
	
	public int[] getIndices() {
		int[] indices = new int[Constants.NUM_TILE_HOLDERS];
		for(int z = 0; z < getChildCount(); z++){
			Tile t = getTileAt(z);
			if(t != null && t.isPartOfLastWord()){
				indices[z] = ((LastWordTile)t).getIndex();
			}
		}
		return indices;
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
				((GameActivity)getContext()).returnTile(t);
			}
				
		}
	}

	

	

}
