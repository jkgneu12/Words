package com.example.words.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.words.state.Bag;

public class RemainingTiles extends TextView {

	public RemainingTiles(Context context, AttributeSet attrs) {
		super(context, attrs);
		
	}
	
	public void refreshUI(Bag bag){
		setText(bag.remainingTiles() + " Tiles Left");
	}

}
