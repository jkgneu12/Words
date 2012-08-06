package com.example.words.view;

import com.example.words.activity.GameActivity;

import android.view.View;
import android.view.ViewGroup;

public class LastWordTileHolder extends TileHolder {
	
	private Tile myTile;

	public LastWordTileHolder(GameActivity activity, int index, Tile tile) {
		super(activity, index);
		this.myTile = tile;
		addView(tile);
	}
	
	@Override
	protected void dragExited(Tile tile) {
		containsDragable = false;
        unhighlight();
	}

	@Override
	protected void dragEntered(Tile tile) {
		if(tile != myTile)
    		badHighlight();
    	else {
    		containsDragable = true;
    		goodHighlight();
    	}
	}

	@Override
	protected void dragEnded(Tile tile) {
		tile.setVisibility(View.VISIBLE);
        unhighlight();
	}

	@Override
	protected void dragDropped(Tile tile) {
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
