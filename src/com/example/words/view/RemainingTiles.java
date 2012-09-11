package com.example.words.view;

import com.example.words.state.Bag;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class RemainingTiles extends TextView {

	public RemainingTiles(Context context, AttributeSet attrs) {
		super(context, attrs);
		
	}
	
	public void refreshUI(Bag bag){
		setText(bag.remainingTiles() + " Tiles Left");
	}

}
