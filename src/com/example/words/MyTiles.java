package com.example.words;

import android.graphics.Color;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class MyTiles extends LinearLayout implements OnDragListener {
	
	private MainActivity activity;
	
	private boolean containsDragable;

	public MyTiles(MainActivity activity, AttributeSet attrs) {
		super(activity, attrs);
		this.activity = activity;
		
		setOrientation(HORIZONTAL);
		setGravity(Gravity.CENTER);
		setBackgroundColor(Color.WHITE);
		
		setOnDragListener(this);
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
        	ViewGroup owner = (ViewGroup) dragView.getParent();
            owner.removeView(dragView);
            addView(dragView);
            dragView.initLayoutParams();
        }
        return true;
    }

}
