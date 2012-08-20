package com.example.words.view;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

import com.example.words.activity.GameActivity;
import com.example.words.activity.GameFragment;

public class FakeTile extends Tile {

	public FakeTile(GameActivity activity, GameFragment fragment) {
		super(activity, fragment, "a");
	}

	@Override
	public boolean isPartOfLastWord() {
		return false;
	}

	@Override
	protected Drawable getSelectedBackgroundDrawable() {
		return null;
	}

	@Override
	protected Drawable getBackgroundDrawable() {
		return null;
	}

	@Override
	protected void initTextView() {}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return false;
	}
	
	@Override
	public String toString() {
		return "Fake Tile";
	}

	public String getLetter() {
		return null;
	}

}
