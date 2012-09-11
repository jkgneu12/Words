package com.example.words.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

import com.example.words.R;
import com.example.words.activity.GameActivity;

public class CurrentScore extends TextView {

	private GameActivity activity;

	public CurrentScore(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.activity = (GameActivity)context;
	}
	
	public void refreshUI(String[] tiles, boolean usedAllTiles) {
		int points = activity.getAppController().getPointsForValidWord(tiles, usedAllTiles);
		setText("" + points);
		setTextColor(usedAllTiles ? getResources().getColor(R.color.orange) : getResources().getColor(R.color.text_light));
		setTextSize(TypedValue.COMPLEX_UNIT_DIP, usedAllTiles ? 15 : 13);
	}

}
