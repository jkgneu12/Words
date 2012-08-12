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
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		//if(Build.VERSION.SDK_INT >= 11 && getChildCount() > 0)
		//	((StarWarsScrollerLayout)getChildAt(0)).onScrollChanged(l, t, b - t);
			
	}

	
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		
		if(Build.VERSION.SDK_INT >= 11 && getChildCount() > 0)
			((StarWarsScrollerLayout)getChildAt(0)).onScrollChanged(l, t, getMeasuredHeight());
	}
}
