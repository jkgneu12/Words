package com.example.words.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.words.Constants;
import com.example.words.activity.GameActivity;
import com.example.words.listener.DragAndDropListener;
import com.example.words.listener.IDragAndDrop;

public abstract class FreeFormBoard extends LinearLayout implements IDragAndDrop {

	protected boolean containsDragable;
	protected GameActivity activity;

	public FreeFormBoard(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.activity = (GameActivity)context;

		setOrientation(HORIZONTAL);
		setGravity(Gravity.CENTER);

		setMinimumHeight((int)(Constants.getTileDimensions((Activity)context) + Constants.getDIPixels(activity, 10)));

		initListeners();
	}

	@TargetApi(11)
	private void initListeners() {
		if(Build.VERSION.SDK_INT >= 11)
			setOnDragListener(new DragAndDropListener(this));
	}

	@Override
	public void dragEntered(Tile tile) {
    	containsDragable = true;
    	if(canDrop(tile) && tile.getParent() != this)
    		goodHighlight();
	}

	@Override
	public void dragExited(Tile tile) {
		containsDragable = false;
        unhighlight();
	}

	@Override
	public void dragEnded(Tile tile) {
		tile.setVisibility(View.VISIBLE);
        unhighlight();
	}
	
	protected abstract void goodHighlight();
	protected abstract void unhighlight();

	@TargetApi(11)
	public void dragDropped(Tile tile, DragEvent dragEvent) {
		if(canDrop(tile)){
			int x = (int)dragEvent.getX();
			placeTile(tile, x);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_UP){
			Tile active = activity.getActiveTile();
			if(active != null && canDrop(active)){
				placeTile(active, (int)event.getX());
				activity.setActiveTile(null);
			}
		}
		return true;
	}

	protected abstract boolean canDrop(Tile tile);

	private void placeTile(Tile tile, int x) {

		int insertIndex = -1;
		boolean alreadyAdded = tile.getParent() == this;

		if(alreadyAdded){
			for(int z = 0; z < getChildCount(); z++){
				if(overlaps(getChildAt(z), x))
					insertIndex = z;
			}
		}

		if(getChildCount() > 0){

			if(insertIndex == -1 && x < getMiddle(getChildAt(0)))
				insertIndex = 0;
			if(insertIndex == -1) {
				boolean isRight = alreadyAdded && x > getMiddle(tile);
				for(int z = 1; z < getChildCount(); z++){
					if(x <= getMiddle(getChildAt(z)) && x >= getMiddle(getChildAt(z - 1))){
						insertIndex = z;
						if(isRight)
							insertIndex--;
					}
				}
			}
			if(insertIndex == -1 && x > getMiddle(getChildAt(getChildCount() - 1))){
				insertIndex = getChildCount();
				if(alreadyAdded)
					insertIndex--;
			}
		}

		if(insertIndex == -1){
			for(int z = 0; z < getChildCount(); z++){
				if(getChildAt(z) == tile)
					insertIndex = z;
			}
		}
		if(insertIndex == -1)
			insertIndex = 0;

		ViewGroup owner = (ViewGroup) tile.getParent();
		owner.removeView(tile);
		addView(tile, insertIndex);
		((GameActivity)getContext()).update();
	}

	private int getMiddle(View v){
		return (v.getLeft() + v.getRight()) / 2;
	}

	private boolean overlaps(View v, int x){
		return x >= v.getLeft() && x <= v.getRight();
	}

	@Override
	public void addView(View child) {
		super.addView(child);
		((Tile)child).initLayoutParams();
	}

	@Override
	public void addView(View child, int index) {
		super.addView(child, index);
		((Tile)child).initLayoutParams();
	}

	public void replaceTile(View oldChild, View newChild) {
		addView(newChild, indexOf(oldChild));
	}

	private int indexOf(View oldChild) {
		for(int z = 0; z < getChildCount(); z++){
			if(getChildAt(z) == oldChild)
				return z;
		}
		return 0;
	}

	public String[] getLetters() {
		String[] letters = new String[Constants.NUM_MY_TILES];
		for(int z = 0; z < getChildCount(); z++){
			Tile t = getTileAt(z);
			if(t != null)
				letters[z] = t.getLetter();
		}
		return letters;
	}

	protected Tile getTileAt(int z) {
		return (Tile)getChildAt(z);
	}

	@Override
	public boolean containsDragable() {
		return containsDragable;
	}


}
