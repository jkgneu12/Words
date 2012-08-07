package com.example.words.view;

import android.view.View;
import android.view.ViewGroup;

import com.example.words.activity.GameActivity;

public class GameBoardTileHolder extends TileHolder {

	public GameBoardTileHolder(GameActivity activity, int index) {
		super(activity, index);
	}
	
	@Override
	protected void dragExited(Tile tile) {
		containsDragable = false;
        unhighlight();
	}

	@Override
	protected void dragEntered(Tile tile) {
		if(tile.isPartOfLastWord() && ((LastWordTile)tile).getIndex() == index)
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

	

}
