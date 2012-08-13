package com.example.words.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class StarWarsScrollerLayout extends LinearLayout {

	public StarWarsScrollerLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@TargetApi(11)
	public void relayout(int t, int height) {
		int top = t;
		
		for(int z = 0; z < getChildCount(); z++){
			LinearLayout child = (LinearLayout)getChildAt(z);
			
			int childBottom = child.getBottom();
			
			float scale = .3f + ((((float)childBottom - top) / height));
			
			//child.setTextSize(Constants.getPreviousWordSize((Activity)getContext()));
			child.setScaleX(scale);
			child.setScaleY(scale);
			
		}
	}
	
	
}
