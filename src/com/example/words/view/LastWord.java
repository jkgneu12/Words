package com.example.words.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;

import com.example.words.Constants;
import com.example.words.R;
import com.example.words.listener.DragAndDropListener;
import com.example.words.listener.IDragAndDrop;

public class LastWord extends TileHolderSet implements IDragAndDrop {
	
	int[] tiles;
	private String lastWord;
	private boolean containsDragable;

	public LastWord(Context context, AttributeSet attrs) {
		super(context, attrs);
		tiles = new int[Constants.NUM_TILE_HOLDERS];
		initListeners();
	}
	
	@TargetApi(11)
	private void initListeners() {
		if(Build.VERSION.SDK_INT >= 11)
			setOnDragListener(new DragAndDropListener(this));
	}
	
	public void setCompleteLastWord(String[] lastWordArray){
		if(lastWord == null)
			lastWord = Constants.arrayToString(lastWordArray);
		removeAllViews();
		for(int z = 0; z < lastWord.length(); z++){
			addView(new TileHolder(activity, fragment, z));
		}
	}
	
	public void setCurrentLastWord(String[] lastWordArray) {
		for(int z = 0; z < lastWordArray.length; z++){
			String c = lastWordArray[z];
			if(!Constants.isNullOrEmpty(c)){
				Tile tile = new LastWordTile(activity, fragment, lastWordArray[z], z);
				tiles[z] = tile.hashCode();
				getTileHolderAt(z).removeAllViews();
				getTileHolderAt(z).addView(tile);
			}
		}
	}
	
	public String getLastWord(){
		return lastWord.toLowerCase();
	}
	
	public String[] getLetters() {
		String[] letters = new String[Constants.NUM_TILE_HOLDERS];
		for(int z = 0; z < getChildCount(); z++){
			Tile t = getTileAt(z);
			if(t != null)
				letters[((TileHolder)t.getParent()).getIndex()] = t.getLetter();
		}
		return letters;
	}

	public void replaceTile(Tile oldChild, Tile newChild) {
		((TileHolder)getChildAt(indexOf(newChild))).addView(newChild);
	}
	
	private int indexOf(Tile newChild) {
		int id = newChild.hashCode();
		for(int z = 0; z < getChildCount(); z++){
			if(tiles[z] == id)
				return z;
		}
		return 0;
	}

	public void returnTile(LastWordTile tile) {
		getTileHolderAt(tile.getIndex()).addView(tile);
	}
	
	public boolean usedAtLeastOneTile(){
		if(Constants.isNullOrEmpty(lastWord)) return true;
		for(int z = 0; z < lastWord.length(); z++){
			if(getTileAt(z) == null)
				return true;
		}
		return false;
	}
	
	public boolean usedAllTiles(){
		if(Constants.isNullOrEmpty(lastWord)) return false;
		for(int z = 0; z < lastWord.length(); z++){
			if(getTileAt(z) != null)
				return false;
		}
		return true;
	}

	@Override
	public void dragEntered(Tile tile) {
    	containsDragable = true;
    	if(tile.isPartOfLastWord() && tile.getParent() instanceof GameBoard)
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

	@TargetApi(11)
	@Override
	public void dragDropped(Tile tile, DragEvent dragEvent) {
		if(tile.isPartOfLastWord())
			getTileHolderAt(((LastWordTile)tile).getIndex()).dragDropped(tile, dragEvent);
		unhighlight();
	}

	@Override
	public boolean containsDragable() {
		return containsDragable;
	}
	

	private void goodHighlight() {
		setBackgroundResource(R.drawable.last_word_higlight_backround);
		
	}
	private void unhighlight() {
		setBackgroundDrawable(null);
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_UP){
			Tile active = fragment.getActiveTile();
			if(active != null && canDrop(active)){
				dragDropped(active, null);
			}
		}
		return true;
	}

	private boolean canDrop(Tile tile) {
		return tile.isPartOfLastWord();
	}

	public int getTileCount() {
		int sum = 0;
		for(int z = 0; z < getChildCount(); z++){
			if(getTileAt(z) != null)
				sum++;
		}
		return sum;
	}

	

	

}
