package com.example.words.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.util.AttributeSet;
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

	public PreviousWordsLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.activity = (GameActivity)context;
	}
	
	public void refreshUI(PreviousWords model, boolean force) {
		int prevWordCount = model.usedWords.size();
		
		if(force || getChildCount() != prevWordCount){
			
			float fontSize = Utils.getPreviousWordSize(activity);
			removeAllViews();
	
			for(int z = 0; z < prevWordCount; z++){
				LinearLayout row = (LinearLayout) ((LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.prev_word, this, false);
	
				LinearLayout word = (LinearLayout)row.findViewById(R.id.b);
				String wordText = model.usedWords.get(z);
				for(int y = 0; y < wordText.length(); y++){
					TextView letter = new TextView(activity);
					letter.setText("" + wordText.charAt(y));
					letter.setTextSize(fontSize);
					if(model.reused == null || model.reused.size() <= z || !model.reused.get(z).contains(y))
						letter.setTextColor(getResources().getColor(R.color.brown_light));
					else 
						letter.setTextColor(getResources().getColor(R.color.orange_light));
					letter.setShadowLayer(3, 2, 2, Color.BLACK);
					word.addView(letter);
				}
	
				if(model.scores != null && model.scores.size() > z){
					String scoreText = "" + model.scores.get(z);
					TextView score;
					TextView space;
					if(model.turns.size() > z && model.turns.get(z).equals(activity.currentUser.getObjectId())){
						score = (TextView)row.findViewById(R.id.a);
						space = (TextView)row.findViewById(R.id.c);
					} else {
						score = (TextView)row.findViewById(R.id.c);
						space = (TextView)row.findViewById(R.id.a);
					}
					score.setText(scoreText);
					score.setTextSize(fontSize / 2);
					String spaceText = "";
					for(int y = 0; y < wordText.length(); y++)
						spaceText += " ";
					space.setText(spaceText);
					space.setTextSize(fontSize / 2);
				} 
	
				addView(row);
			}
	
			addDummyPreviousWord(fontSize);
	
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

	public void addDummyPreviousWord(float fontSize) {
		LinearLayout row = (LinearLayout) ((LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.prev_word, this, false);

		LinearLayout word = (LinearLayout)row.findViewById(R.id.b);
		TextView text = new TextView(activity);
		text.setText(" ");
		if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
			fontSize *= 1.3;
		text.setTextSize((int)fontSize);
		word.addView(text);

		addView(row);
	}
	
	@TargetApi(11)
	public void relayout(int t, int height) {
		int top = t;
		
		for(int z = 0; z < getChildCount(); z++){
			LinearLayout child = (LinearLayout)getChildAt(z);
			
			int childBottom = child.getBottom();
			
			float scale = .3f + ((((float)childBottom - top) / height));
			
			//child.setTextSize(Constants.getPreviousWordSize((Activity)getContext()));
			child.setScaleX(scale);
			child.setScaleY(scale);
			
		}
	}
	
	
}
