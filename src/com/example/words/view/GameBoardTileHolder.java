package com.example.words.view;

import android.view.DragEvent;
import android.view.ViewGroup;

import com.example.words.activity.GameActivity;

public class GameBoardTileHolder extends TileHolder {

	public GameBoardTileHolder(GameActivity activity, int index) {
		super(activity, index);
	}

	@Override
	public void dragDropped(Tile tile, DragEvent dragEvent) {
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

	@Override
	public boolean isGameBoardHolder() {
		return true;
	}

	@Override
	protected boolean canDrop(Tile active) {
		return true;
	}

	

}
