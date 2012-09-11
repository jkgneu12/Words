package com.example.words.view;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.example.words.AppController;
import com.example.words.Utils;
import com.example.words.activity.GameActivity;
import com.example.words.activity.GameFragment;
import com.example.words.listener.DragAndDropListener;
import com.example.words.listener.IDragAndDrop;

public class TileHolder extends FrameLayout implements IDragAndDrop {

	protected GameActivity activity;
	protected GameFragment fragment;
	
	protected boolean containsDragable;

	protected int index;
	
	public TileHolder(GameActivity activity, GameFragment fragment, int index) {
		super(activity);
		this.activity = activity;
		this.fragment = fragment;
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
		int dim = Utils.getTileDimensions(activity);
		
		LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(dim, dim);
		int margin = Utils.getTileMargin(activity, AppController.NUM_GAMEBOARD_TILES);
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
	    			fragment.addTileToGameBoard(oldChild);
	    		else if(oldChild.isPartOfLastWord())
	    			fragment.replaceLastWordTile(tile, oldChild);
	    		else
	    			fragment.replaceMyTile(tile, oldChild);
	    	}
	    	ViewGroup owner = (ViewGroup) tile.getParent();
	        owner.removeView(tile);
	        addView(tile);
	        fragment.update();
	        ((LastWord)getParent()).unhighlight();
		} else 
			((LastWord)getParent()).dragDropped(tile, dragEvent);
		
	}
	
	@Override
	public void dragEntered(Tile tile) {
    	containsDragable = true;
	}
	
	@Override
	public void dragExited(Tile tile) {
		containsDragable = false;
	}
	
	@Override
	public void dragEnded(Tile tile) {
		tile.setVisibility(View.VISIBLE);
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

	@TargetApi(11)
	@Override
	public void dragMoved(Tile tile, DragEvent dragEvent) {
	}

}
