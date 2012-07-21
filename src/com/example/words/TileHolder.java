package com.example.words;

import android.graphics.Color;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class TileHolder extends FrameLayout implements OnDragListener {

	private MainActivity activity;
	
	private boolean containsDragable;
	
	public TileHolder(MainActivity activity) {
		super(activity);
		this.activity = activity;
		
		initLayoutParams();
		setBackgroundColor(Color.GREEN);
		setOnDragListener(this);
	}
	
	public void initLayoutParams() {
		int dim = Constants.getTileDimensions(activity);
		
		LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(dim, dim);
		int margin = Constants.getTileMargin(activity, Constants.NUM_TILE_HOLDERS);
		p.setMargins(margin, margin, margin, margin);
		setLayoutParams(p);
	}

	@Override
    public boolean onDrag(View view, DragEvent dragEvent) {
		int dragAction = dragEvent.getAction();
        View dragView = (View) dragEvent.getLocalState();
        if (dragAction == DragEvent.ACTION_DRAG_EXITED) {
            containsDragable = false;
        } else if (dragAction == DragEvent.ACTION_DRAG_ENTERED) {
            containsDragable = true;
        } else if (dragAction == DragEvent.ACTION_DRAG_ENDED) {
            dragView.setVisibility(View.VISIBLE);
        } else if (dragAction == DragEvent.ACTION_DROP && containsDragable && getChildCount() == 0) {
        	ViewGroup owner = (ViewGroup) dragView.getParent();
            owner.removeView(dragView);
            addView(dragView);
            
        }
        return true;
    }

}
