package com.example.words;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class LastWord extends LinearLayout {
	
	int[] tiles;
	private String lastWord;

	public LastWord(Context context, AttributeSet attrs) {
		super(context, attrs);
		tiles = new int[Constants.NUM_MY_TILES];
	}
	
	public void setLastWord(String lastWord){
		this.lastWord = lastWord;
		for(int z = 0; z < lastWord.length(); z++){
			if(Character.isLetter(lastWord.charAt(z))){
				Tile tile = new Tile((MainActivity)getContext(), "" + lastWord.charAt(z), true);
				tiles[z] = tile.hashCode();
				((TileHolder)getChildAt(z)).addView(tile);
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
	
	private TileHolder getTileHolderAt(int z) {
		return (TileHolder)getChildAt(z);
	}
	
	private Tile getTileAt(int z){
		TileHolder holder = getTileHolderAt(z);
		if(holder != null)
			return holder.getTile();
		return null;
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

	

}
