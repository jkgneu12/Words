package com.example.words.view;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.example.words.Constants;
import com.example.words.R;
import com.example.words.activity.GameActivity;
import com.example.words.listener.DragAndDropListener;
import com.example.words.listener.IDragAndDrop;

public class TileHolder extends FrameLayout implements IDragAndDrop {

	protected GameActivity activity;
	
	protected boolean containsDragable;

	protected int index;
	
	public TileHolder(GameActivity activity, int index) {
		super(activity);
		this.activity = activity;
		this.index = index;
		
		initLayoutParams();
		
		initListeners();
	}
	
	@TargetApi(11)
	private void initListeners() {
		if(Build.VERSION.SDK_INT >= 11)
			setOnDragListener(new DragAndDropListener(this));
	}

	public void initLayoutParams() {
		int dim = Constants.getTileDimensions(activity);
		
		LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(dim, dim);
		int margin = Constants.getTileMargin(activity, Constants.NUM_TILE_HOLDERS);
		p.setMargins(margin, margin, margin, margin);
		setLayoutParams(p);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_UP){
			((LastWord)getParent()).onTouchEvent(event);
		}
		return true;
	}
	

	protected boolean canDrop(Tile tile){
		return tile.isPartOfLastWord() && ((LastWordTile)tile).getIndex() == index;
	}

	@TargetApi(11)
	@Override
	public void dragDropped(Tile tile, DragEvent dragEvent) {
		if(canDrop(tile)) {
			if(getChildCount() > 0){
	    		Tile oldChild = (Tile)getChildAt(0);
	    		removeView(oldChild);
	    		if(tile.getParent() instanceof TileHolder && tile.getParent().getParent() instanceof GameBoard)
	    			activity.addTileToGameBoard(oldChild);
	    		else if(oldChild.isPartOfLastWord())
	    			activity.replaceLastWordTile(tile, oldChild);
	    		else
	        		activity.replaceMyTile(tile, oldChild);
	    	}
	    	ViewGroup owner = (ViewGroup) tile.getParent();
	        owner.removeView(tile);
	        addView(tile);
	        activity.update();
		} else 
			((LastWord)getParent()).dragDropped(tile, dragEvent);
		
	}
	
	@Override
	public void dragEntered(Tile tile) {
    	containsDragable = true;
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

	public void goodHighlight() {
		setBackgroundColor(activity.getResources().getColor(R.color.base));
	}
	
	public void badHighlight() {
		setBackgroundColor(Color.RED);
	}

	public void unhighlight() {
		setBackgroundDrawable(null);
	}

	public Tile getTile() {
		return getChildCount() > 0 ? (Tile)getChildAt(0) : null;
	}
	
	protected int getIndex() {
		return index;
	}
	
	public boolean containsDragable(){
		return containsDragable;
	}

}
