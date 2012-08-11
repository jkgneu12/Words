package com.example.words.view;

import android.content.Context;
import android.util.AttributeSet;

import com.example.words.Constants;
import com.example.words.activity.GameActivity;

public class LastWord extends TileHolderSet {
	
	int[] tiles;
	private String lastWord;

	public LastWord(Context context, AttributeSet attrs) {
		super(context, attrs);
		tiles = new int[Constants.NUM_TILE_HOLDERS];
	}
	
	public void setCompleteLastWord(String[] lastWordArray){
		if(lastWord == null)
			lastWord = Constants.arrayToString(lastWordArray);
	}
	
	public void setCurrentLastWord(String[] lastWordArray) {
		for(int z = 0; z < lastWord.length(); z++){
			String c = lastWordArray[z];
			if(Constants.isNullOrEmpty(c)){
				addView(new LastWordTileHolder((GameActivity)getContext(), z, null));
			}
			else {
				Tile tile = new LastWordTile((GameActivity)getContext(), lastWordArray[z], z);
				tiles[z] = tile.hashCode();
				addView(new LastWordTileHolder((GameActivity)getContext(), z, tile));
			}
		}
	}
	
	public String getLastWord(){
		return lastWord.toLowerCase();
	}

	public void replaceTile(Tile oldChild, Tile newChild) {
		((TileHolder)getChildAt(indexOf(newChild))).addView(newChild);
	}
	
	private int indexOf(Tile newChild) {
		int id = newChild.hashCode();
		for(int z = 0; z < getChildCount(); z++){
			if(tiles[z] == id)
				return z;
		}
		return 0;
	}

	public void returnTile(LastWordTile tile) {
		getTileHolderAt(tile.getIndex()).addView(tile);
	}
	
	public boolean usedAtLeastOneTile(){
		if(Constants.isNullOrEmpty(lastWord)) return true;
		for(int z = 0; z < lastWord.length(); z++){
			if(getTileAt(z) == null)
				return true;
		}
		return false;
	}
	
	public boolean usedAllTiles(){
		if(Constants.isNullOrEmpty(lastWord)) return false;
		for(int z = 0; z < lastWord.length(); z++){
			if(getTileAt(z) != null)
				return false;
		}
		return true;
	}

	

}
