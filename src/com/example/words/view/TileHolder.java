package com.example.words.view;

import android.graphics.Color;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.example.words.Constants;
import com.example.words.activity.GameActivity;

public abstract class TileHolder extends FrameLayout implements OnDragListener {

	protected GameActivity activity;
	
	protected boolean containsDragable;

	protected int index;
	
	public TileHolder(GameActivity activity, int index) {
		super(activity);
		this.activity = activity;
		this.index = index;
		
		initLayoutParams();
		setBackgroundColor(Color.BLACK);
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
        Tile tile = (Tile) dragEvent.getLocalState();
        if (dragAction == DragEvent.ACTION_DRAG_EXITED) {
            dragExited(tile);
        } else if (dragAction == DragEvent.ACTION_DRAG_ENTERED ) {
        	dragEntered(tile);
        } else if (dragAction == DragEvent.ACTION_DRAG_ENDED) {
        	dragEnded(tile);
        } else if (dragAction == DragEvent.ACTION_DROP && containsDragable) {
        	dragDropped(tile);
        }
        return true;
    }

	protected abstract void dragExited(Tile tile);
	protected abstract void dragEntered(Tile tile);
	protected abstract void dragEnded(Tile tile);
	protected abstract void dragDropped(Tile tile);

	public void goodHighlight() {
		setBackgroundColor(Color.GREEN);
	}
	
	public void badHighlight() {
		setBackgroundColor(Color.RED);
	}

	public void unhighlight() {
		setBackgroundColor(Color.BLACK);
	}

	public Tile getTile() {
		return getChildCount() > 0 ? (Tile)getChildAt(0) : null;
	}
	
	protected int getIndex() {
		return index;
	}
	
	public abstract boolean isGameBoardHolder();

}
