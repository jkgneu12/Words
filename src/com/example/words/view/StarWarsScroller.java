package com.example.words.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class StarWarsScroller extends ScrollView {

	public StarWarsScroller(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		if(Build.VERSION.SDK_INT >= 9)
			disableOverScroll();
	}
	
	@TargetApi(9)
	private void disableOverScroll() {
		setOverScrollMode(ScrollView.OVER_SCROLL_NEVER);
	}
	
	public void relayout(int t, int h){
		if(Build.VERSION.SDK_INT >= 11 && getChildCount() > 0)
			((PreviousWordsLayout)getChildAt(0)).relayout(t, h);
	}
	
	@Override
	public boolean fullScroll(int direction) {
		if(!super.fullScroll(direction)){
			relayout(0, getMeasuredHeight());
			return false;
		}
		return true;
	}

	
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		relayout(t, getMeasuredHeight());
	}
}
