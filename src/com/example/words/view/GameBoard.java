package com.example.words.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;

import com.example.words.Constants;
import com.example.words.R;

public class GameBoard extends FreeFormBoard {

	public GameBoard(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOrientation(HORIZONTAL);
		setGravity(Gravity.CENTER);
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
		while(getChildCount() > 0){
			Tile t = getTileAt(0);
			if(t != null){
				activity.returnTile(t);
			}
				
		}
	}

	public List<Integer> getReusedIndices() {
		int found = 0;
		List<Integer> indices = new ArrayList<Integer>();
		for(int z = 0; z < getChildCount(); z++){
			if(getTileAt(z) != null){
				if(getTileAt(z).isPartOfLastWord())
					indices.add(found);
				found++;
			}
				
		}
		return indices;
	}

	@Override
	protected boolean canDrop(Tile tile) {
		return getChildCount() < Constants.NUM_MY_TILES || tile.getParent() == this;
	}
	
	@Override
	protected void goodHighlight() {
		setBackgroundResource(R.drawable.gameboard_higlighted_background);
	}

	@Override
	protected void unhighlight() {
		setBackgroundResource(R.drawable.gameboard_background);
	}

}
