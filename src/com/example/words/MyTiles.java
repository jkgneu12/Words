package com.example.words;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class MyTiles extends LinearLayout implements OnDragListener {
	
	private boolean containsDragable;

	public MyTiles(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		setOrientation(HORIZONTAL);
		setGravity(Gravity.CENTER);
		setBackgroundColor(Color.WHITE);
		
		setMinimumHeight(Constants.getTileDimensions((Activity)context) + 2 * Constants.getTileMargin((Activity)context, Constants.NUM_MY_TILES));
		
		setOnDragListener(this);
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
	
	@Override
    public boolean onDrag(View view, DragEvent dragEvent) {
		int dragAction = dragEvent.getAction();
        Tile dragView = (Tile) dragEvent.getLocalState();
        if (dragAction == DragEvent.ACTION_DRAG_EXITED) {
            containsDragable = false;
        } else if (dragAction == DragEvent.ACTION_DRAG_ENTERED) {
            containsDragable = true;
        } else if (dragAction == DragEvent.ACTION_DRAG_ENDED) {
            dragView.setVisibility(View.VISIBLE);
        } else if (dragAction == DragEvent.ACTION_DROP && containsDragable) {
        	int insertIndex = -1;
        	int x = (int)dragEvent.getX();
        	boolean alreadyAdded = dragView.getParent() == this;
        	
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
        			boolean isRight = alreadyAdded && x > getMiddle(dragView);
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
        			if(getChildAt(z) == dragView)
        				insertIndex = z;
        		}
        	}
        	if(insertIndex == -1)
        		insertIndex = 0;
        	
        	ViewGroup owner = (ViewGroup) dragView.getParent();
            owner.removeView(dragView);
            addView(dragView, insertIndex);
        }
        return true;
    }
	
	private int getMiddle(View v){
		return (v.getLeft() + v.getRight()) / 2;
	}
	
	private boolean overlaps(View v, int x){
		return x >= v.getLeft() && x <= v.getRight();
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

	public char[] getLetters() {
		String letters = "";
		for(int z = 0; z < getChildCount(); z++){
			Tile t = getTileAt(z);
			if(t != null)
				letters += t.getLetter();
		}
		return letters.toCharArray();
	}

	private Tile getTileAt(int z) {
		return (Tile)getChildAt(z);
	}
	
	

}
