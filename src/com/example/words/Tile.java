package com.example.words;

import android.content.ClipData;
import android.graphics.Color;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Tile extends FrameLayout {

	private MainActivity activity;
	
	private TextView textView;
	private char text;

	public Tile(MainActivity activity, char text) {
		super(activity);
		this.activity = activity;
		this.text = text;
		
		initLayoutParams();
		setBackgroundColor(Color.BLUE);
		
		initTextView();
	}

	private void initTextView() {
		textView = new TextView(activity);
		textView.setText("" + text);
		textView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		textView.setGravity(Gravity.CENTER);
		textView.setTextColor(Color.BLACK);
		addView(textView); 
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


}
