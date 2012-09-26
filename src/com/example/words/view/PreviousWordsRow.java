package com.example.words.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.words.R;
import com.example.words.activity.GameActivity;
import com.example.words.state.PreviousWords;

public class PreviousWordsRow extends LinearLayout {

	private LinearLayout word;
	private TextView left;
	private TextView right;
	
	private GameActivity activity;

	public PreviousWordsRow(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.activity = (GameActivity)context;
	}
	
	public void layout(PreviousWords model, int z){
		String text = model.usedWords.get(z);
		for(int y = 0; y < text.length(); y++){
			TextView letter = new TextView(activity);
			letter.setText("" + text.charAt(y));
			letter.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 26);
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
				score = left;
				space = right;
			} else {
				score = right;
				space = left;
			}
			score.setText(scoreText);
			score.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
			String spaceText = "";
			for(int y = 0; y < text.length(); y++)
				spaceText += " ";
			space.setText(spaceText);
			space.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
		} 
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		word = (LinearLayout)findViewById(R.id.b);
		left = (TextView)findViewById(R.id.a);
		right = (TextView)findViewById(R.id.c);
	}

}
