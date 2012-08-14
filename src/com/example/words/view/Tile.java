package com.example.words.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
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

	public static final boolean RIGHT_HANDED = true;//TODO: make prefernce

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

		setBackgroundDrawable(getBackgroundDrawable());

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
		String text = "" + points;
		scoreView.setText(text);
		if(text.length() == 2)
			scoreView.setTextSize(Constants.getDIPixels(activity, 3));
		else
			scoreView.setTextSize(Constants.getDIPixels(activity, 5));
		scoreView.setTextColor(Color.BLACK);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(ALIGN_PARENT_BOTTOM);
		params.addRule(ALIGN_PARENT_RIGHT);
		int margin = (int)Constants.getDIPixels(activity, 3);
		params.setMargins(0,0,margin*2,margin);
		scoreView.setLayoutParams(params);
		addView(scoreView);
	}

	public void initLayoutParams() {
		int dim = Constants.getTileDimensions(activity);

		LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(dim, dim);
		int margin = Constants.getTileMargin(activity, Constants.NUM_MY_TILES);
		p.setMargins(margin, 0, margin, 0);
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
			setBackgroundDrawable(getSelectedBackgroundDrawable());
		}
		else {
			setBackgroundDrawable(getBackgroundDrawable());
		}
	}

	protected abstract Drawable getSelectedBackgroundDrawable();
	protected abstract Drawable getBackgroundDrawable();
	
	@TargetApi(11)
	class TileDragShadowBuilder extends DragShadowBuilder {
		private Tile tile;
		public TileDragShadowBuilder(Tile tile) {
			super(tile);
			this.tile = tile;
		}

		@Override
		public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
			super.onProvideShadowMetrics(shadowSize, shadowTouchPoint);
			shadowSize.x *= DRAG_SCALE;
			shadowSize.y *= DRAG_SCALE;
			boolean right;
			if(activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
				right = Constants.getSharedPrefBool(activity, "leftHanded");
			} else {
				right = leftSideOfScreen(shadowTouchPoint.x);
			}
			
			if(right)
				shadowTouchPoint.x *= .5 * DRAG_SCALE;
			else 
				shadowTouchPoint.x *= 2 * DRAG_SCALE;
			shadowTouchPoint.y *= 2 * DRAG_SCALE;
		}
		
		private boolean leftSideOfScreen(int touchX){
			int[] pos = new int[2];
			tile.getLocationInWindow(pos);
			return touchX + pos[0] < Constants.getActivityWidth(activity) / 2;
		}
		
		@Override
		public void onDrawShadow(Canvas canvas) {
			canvas.scale(DRAG_SCALE, DRAG_SCALE);
			getView().draw(canvas);
			
		}
	}


}
