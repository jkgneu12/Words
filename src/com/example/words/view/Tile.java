package com.example.words.view;

import android.content.ClipData;
import android.graphics.Color;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.words.Constants;
import com.example.words.activity.GameActivity;
import com.example.words.state.Game;

public abstract class Tile extends RelativeLayout {

	protected GameActivity activity;
	
	protected TextView textView;
	protected TextView scoreView;
	protected String text;

	private int points;
	
	public static Tile create(GameActivity activity, String text, int index, boolean isPartOfLastWord){
		if(isPartOfLastWord)
			return new LastWordTile(activity, text, index);
		else
			return new MyTilesTile(activity, text);
	}
	
	
	public Tile(GameActivity activity, String text) {
		super(activity);
		this.activity = activity;
		this.text = text.toUpperCase();
		this.points = activity.getAppController().getPoints(text);
		
		initLayoutParams();
		
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
		if(event.getAction() == MotionEvent.ACTION_MOVE){
			if(startDrag(ClipData.newPlainText("", ""), new DragShadowBuilder(this), this, 0)){
				setVisibility(INVISIBLE);
			}
		}
		else if(event.getAction() == MotionEvent.ACTION_UP){
			activity.returnTile(this);
		}
		return true;
	}
	
	public abstract boolean isPartOfLastWord();
	
	@Override
	public String toString() {
		return "Tile : " + text;
	}

	public String getLetter() {
		return text;
	}
	
	public TileHolder getHolder() {
		if(getParent() != null && getParent() instanceof TileHolder)
			return (TileHolder)getParent();
		return null;
	}


}
