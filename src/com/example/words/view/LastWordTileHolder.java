package com.example.words.view;

import android.annotation.TargetApi;
import android.view.DragEvent;
import android.view.ViewGroup;

import com.example.words.activity.GameActivity;

public class LastWordTileHolder extends TileHolder {
	
	private Tile myTile;

	public LastWordTileHolder(GameActivity activity, int index, Tile tile) {
		super(activity, index);
		this.myTile = tile;
		addView(tile);
	}
	
	protected boolean canDrop(Tile tile){
		return tile == myTile;
	}

	@TargetApi(11)
	@Override
	public void dragDropped(Tile tile, DragEvent dragEvent) {
		if(tile == myTile) {
			if(getChildCount() > 0){
	    		Tile oldChild = (Tile)getChildAt(0);
	    		removeView(oldChild);
	    		if(tile.getParent() instanceof TileHolder && tile.getParent().getParent() instanceof GameBoard)
	    			activity.addTileToGameBoard(oldChild, ((TileHolder)tile.getParent()).getIndex());
	    		else if(oldChild.isPartOfLastWord())
	    			activity.replaceLastWordTile(tile, oldChild);
	    		else
	        		activity.replaceMyTile(tile, oldChild);
	    	}
	    	ViewGroup owner = (ViewGroup) tile.getParent();
	        owner.removeView(tile);
	        addView(tile);
	        activity.update();
		}
	}

	@Override
	public boolean isGameBoardHolder() {
		return false;
	}

	

}
