package com.example.words.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.words.Constants;
import com.example.words.activity.GameActivity;

public abstract class Tile extends RelativeLayout {

	protected static final float DRAG_SCALE = 1.5f;

	protected GameActivity activity;

	protected TextView textView;
	protected TextView scoreView;
	protected String text;

	private int points;

	private boolean active;

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

		setBackgroundColor(getBackgroundColor());

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

	@SuppressLint("NewApi")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			if(Build.VERSION.SDK_INT < 11){
				toggleActive();
			} 
		}
		else if(event.getAction() == MotionEvent.ACTION_MOVE){
			if(Build.VERSION.SDK_INT >= 11){
				if(startDrag(ClipData.newPlainText("", ""), new TileDragShadowBuilder(this), this, 0))
					setVisibility(INVISIBLE);
				else
					activity.setActiveTile(null);
			}
		}
		else if(event.getAction() == MotionEvent.ACTION_UP){
			if(Build.VERSION.SDK_INT >= 11){
				if(activity.returnTile(this))
					activity.setActiveTile(null);
				else
					toggleActive();


			}
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

	public void toggleActive() {
		if(active){
			activity.setActiveTile(null);
		} else {
			activity.setActiveTile(this);
		}
	}

	public boolean isActive(){
		return active;
	}

	public void setActive(boolean active){

		this.active = active;
		if(active){
			setBackgroundColor(Color.GREEN);
		}
		else {
			setBackgroundColor(getBackgroundColor());
		}
	}

	protected abstract int getBackgroundColor();
	
	@TargetApi(11)
	class TileDragShadowBuilder extends DragShadowBuilder {
		public TileDragShadowBuilder(Tile tile) {
			super(tile);
		}

		@Override
		public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
			super.onProvideShadowMetrics(shadowSize, shadowTouchPoint);
			shadowSize.x *= DRAG_SCALE;
			shadowSize.y *= DRAG_SCALE;
			shadowTouchPoint.x *= DRAG_SCALE;
			shadowTouchPoint.y *= DRAG_SCALE;
		}
		
		@Override
		public void onDrawShadow(Canvas canvas) {
			canvas.scale(DRAG_SCALE, DRAG_SCALE);
			getView().draw(canvas);
			
		}
	}


}
