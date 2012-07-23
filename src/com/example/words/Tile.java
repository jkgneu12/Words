package com.example.words;

import android.content.ClipData;
import android.graphics.Color;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Tile extends RelativeLayout {

	private MainActivity activity;
	
	private TextView textView;
	private TextView scoreView;
	private String text;

	private boolean partOfLastWord;

	private int points;
	
	
	public Tile(MainActivity activity, String text, boolean partOfLastWord) {
		super(activity);
		this.activity = activity;
		this.text = text.toUpperCase();
		this.partOfLastWord = partOfLastWord;
		this.points = activity.getAppController().getPoints(text);
		
		initLayoutParams();
		setBackgroundColor(partOfLastWord ? Color.YELLOW : Color.CYAN);
		
		initTextView();
	}

	private void initTextView() {
		textView = new TextView(activity);
		textView.setText(text);
		textView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		textView.setGravity(Gravity.CENTER);
		textView.setTextColor(Color.BLACK);
		addView(textView); 
		
		scoreView = new TextView(activity);
		scoreView.setText("" + points);
		scoreView.setTextSize(8);
		scoreView.setTextColor(Color.BLACK);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(ALIGN_PARENT_BOTTOM);
		params.addRule(ALIGN_PARENT_RIGHT);
		params.setMargins(2, 2, 5, 3);
		scoreView.setLayoutParams(params);
		addView(scoreView);
	}

	public void initLayoutParams() {
		int dim = Constants.getTileDimensions(activity);
		
		LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(dim, dim);
		int margin = Constants.getTileMargin(activity, Constants.NUM_MY_TILES);
		p.setMargins(margin, margin, margin, margin);
		setLayoutParams(p);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			startDrag(ClipData.newPlainText("", ""), new DragShadowBuilder(this), this, 0);
			setVisibility(INVISIBLE);
			return true;
		}
		return false;
	}
	
	public boolean isPartOfLastWord(){
		return partOfLastWord;
	}
	
	@Override
	public String toString() {
		return "Tile : " + text;
	}

	public char getLetter() {
		return text.charAt(0);
	}


}
