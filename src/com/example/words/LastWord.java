package com.example.words;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class LastWord extends LinearLayout {
	
	int[] tiles;

	public LastWord(Context context, AttributeSet attrs) {
		super(context, attrs);
		tiles = new int[Constants.NUM_MY_TILES];
	}
	
	@Override
	public void addView(View child) {
		tiles[getChildCount()] = child.hashCode();
		super.addView(child);
		((Tile)child).initLayoutParams();
		
	}
	
	@Override
	public void addView(View child, int index) {
		super.addView(child, index);
		((Tile)child).initLayoutParams();
	}

	public void replaceTile(Tile oldChild, Tile newChild) {
		addView(newChild, indexOf(newChild));
	}
	
	private int indexOf(Tile newChild) {
		int id = newChild.hashCode();
		for(int z = 0; z < getChildCount(); z++){
			if(tiles[z] == id)
				return z;
		}
		return 0;
	}

	

}
