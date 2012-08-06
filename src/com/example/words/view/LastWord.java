package com.example.words.view;

import com.example.words.Constants;
import com.example.words.activity.GameActivity;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class LastWord extends TileHolderSet {
	
	int[] tiles;
	private String lastWord;

	public LastWord(Context context, AttributeSet attrs) {
		super(context, attrs);
		tiles = new int[Constants.NUM_TILE_HOLDERS];
	}
	
	public void setLastWord(String[] lastWordArray){
		this.lastWord = Constants.arrayToString(lastWordArray);
		for(int z = 0; z < lastWord.length(); z++){
			if(Character.isLetter(lastWord.charAt(z))){
				Tile tile = new LastWordTile((GameActivity)getContext(), "" + lastWord.charAt(z), z);
				tiles[z] = tile.hashCode();
				addView(new LastWordTileHolder((GameActivity)getContext(), z, tile));
			}
		}
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

	

}
