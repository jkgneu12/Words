package com.example.words.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.words.R;
import com.example.words.Utils;
import com.example.words.activity.GameActivity;
import com.example.words.state.PreviousWords;

public class PreviousWordsLayout extends LinearLayout {

	private GameActivity activity;
	public boolean hasLaidOut = false;

	public PreviousWordsLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.activity = (GameActivity)context;
	}
	
	public void refreshUI(PreviousWords model, boolean force) {
		int prevWordCount = model.usedWords.size();
		
		if(force || getChildCount() - 1 != prevWordCount){
			
			removeAllViews();
	
			for(int z = 0; z < prevWordCount; z++){
				PreviousWordsRow row = (PreviousWordsRow ) ((LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.prev_word, this, false);
				row.layout(model, z);
				addView(row);
			}
	
			addDummyPreviousWord();
	
			final StarWarsScroller scroll = (StarWarsScroller)getParent();
			scroll.post(new Runnable() {
				@Override
				public void run() {
					scroll.fullScroll(ScrollView.FOCUS_DOWN);
					scroll.invalidate();
				}
			});
		}
	}

	public void addDummyPreviousWord() {
		LinearLayout row = (LinearLayout) ((LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.prev_word, this, false);

		LinearLayout word = (LinearLayout)row.findViewById(R.id.b);
		TextView text = new TextView(activity);
		text.setText(" ");
		text.setTextSize((int)TypedValue.COMPLEX_UNIT_DIP, 30);
		word.addView(text);

		addView(row);
	}
	
	@TargetApi(11)
	public void relayout(int t, int height) {
		hasLaidOut  = true;
		int top = t;
		
		for(int z = 0; z < getChildCount(); z++){
			PreviousWordsRow child = (PreviousWordsRow)getChildAt(z);
			
			int childBottom = child.getBottom();
			
			float scale = .3f + ((((float)childBottom - top) / height));
			child.setScaleX(scale);
			child.setScaleY(scale);
			
		}
	}
	
	
}
