package com.example.words.view;

import java.util.Stack;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.words.Constants;
import com.example.words.activity.GameActivity;
import com.example.words.listener.DragAndDropListener;
import com.example.words.listener.IDragAndDrop;

public class MyTiles extends FreeFormBoard {
	
	public MyTiles(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	
	
	protected boolean canDrop(Tile tile){
		return !tile.isPartOfLastWord();
	}
	
	public boolean usedAllTiles() {
		return getChildCount() == 0;
	}

	public void shuffle() {
		Stack<Tile> tiles = new Stack<Tile>();
		while(getChildCount() > 0){
			int index = (int)(Math.random() * getChildCount());
			tiles.push(getTileAt(index));
			removeViewAt(index);
		}
		while(!tiles.isEmpty()){
			addView(tiles.pop());
		}
	}

}
