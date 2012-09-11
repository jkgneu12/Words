package com.example.words.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import com.example.words.AppController;
import com.example.words.R;
import com.example.words.Utils;
import com.example.words.listener.DragAndDropListener;
import com.example.words.listener.IDragAndDrop;

public class LastWord extends TileHolderSet implements IDragAndDrop {
	
	int[] tiles;
	private String lastWord;
	private boolean containsDragable;

	public LastWord(Context context, AttributeSet attrs) {
		super(context, attrs);
		tiles = new int[AppController.NUM_GAMEBOARD_TILES];
		initListeners();
		
		setMinimumHeight((int)(Utils.getTileDimensions((Activity)context) + Utils.getDIPixels(activity, 10)));
		setGravity(Gravity.CENTER);
	}
	
	@TargetApi(11)
	private void initListeners() {
		if(Build.VERSION.SDK_INT >= 11)
			setOnDragListener(new DragAndDropListener(this));
	}
	
	public void setCompleteLastWord(String[] lastWordArray){
		lastWord = Utils.arrayToString(lastWordArray);
		removeAllViews();
		for(int z = 0; z < lastWord.length(); z++){
			addView(new TileHolder(activity, fragment, z));
		}
	}
	
	public void setCurrentLastWord(String[] lastWordArray) {
		for(int z = 0; z < lastWordArray.length; z++){
			String c = lastWordArray[z];
			if(!Utils.isNullOrEmpty(c)){
				Tile tile = new LastWordTile(activity, fragment, lastWordArray[z], z);
				tiles[z] = tile.hashCode();
				
				getTileHolderAt(z).removeAllViews();
				getTileHolderAt(z).addView(tile);
			}
		}
	}

	public String getLastWord(){
		if(lastWord == null)
			return null;
		return lastWord.toLowerCase();
	}
	
	public String[] getLetters() {
		String[] letters = new String[getChildCount()];
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
		if(Utils.isNullOrEmpty(lastWord)) return true;
		for(int z = 0; z < lastWord.length(); z++){
			if(getTileAt(z) == null)
				return true;
		}
		return false;
	}
	
	public boolean usedAllTiles(){
		if(Utils.isNullOrEmpty(lastWord)) return false;
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
	

	protected void goodHighlight() {
		setBackgroundResource(R.drawable.last_word_higlight_backround);
	}

	protected void unhighlight() {
		setBackgroundResource(R.drawable.last_word_backround);
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

	@Override
	@TargetApi(11)
	public void dragMoved(Tile tile, DragEvent dragEvent) {
	}

	public void refreshUI(String[] currentLetters, String[] completeLetters, boolean force) {
		if(force || getTileCount() != Utils.countNonNulls(currentLetters)){
			setCompleteLastWord(completeLetters);
			setCurrentLastWord(currentLetters);
		}
	}

	

	

}
