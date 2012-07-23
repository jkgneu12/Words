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

	private int index;
	
	public TileHolder(MainActivity activity, int index) {
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
        Tile dragView = (Tile) dragEvent.getLocalState();
        if (dragAction == DragEvent.ACTION_DRAG_EXITED) {
            containsDragable = false;
            unhighlight();
        } else if (dragAction == DragEvent.ACTION_DRAG_ENTERED) {
            containsDragable = true;
           	highlight();
        } else if (dragAction == DragEvent.ACTION_DRAG_ENDED) {
            dragView.setVisibility(View.VISIBLE);
            activity.update();
        } else if (dragAction == DragEvent.ACTION_DROP && containsDragable) {
        	if(getChildCount() > 0){
        		Tile oldChild = (Tile)getChildAt(0);
        		removeView(oldChild);
        		if(dragView.getParent() instanceof TileHolder && dragView.getParent().getParent() instanceof GameBoard)
        			activity.addTileToGameBoard(oldChild, ((TileHolder)dragView.getParent()).getIndex());
        		else if(oldChild.isPartOfLastWord())
        			activity.replaceLastWordTile(dragView, oldChild);
        		else
	        		activity.replaceMyTile(dragView, oldChild);
        	}
        	ViewGroup owner = (ViewGroup) dragView.getParent();
            owner.removeView(dragView);
            addView(dragView);
            
        }
        return true;
    }

	private int getIndex() {
		return index;
	}

	public void highlight() {
		setBackgroundColor(Color.GREEN);
	}

	public void unhighlight() {
		setBackgroundColor(Color.BLACK);
	}

	public Tile getTile() {
		return getChildCount() > 0 ? (Tile)getChildAt(0) : null;
	}

}
