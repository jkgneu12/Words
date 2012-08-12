package com.example.words.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.words.Constants;

public class StarWarsScrollerLayout extends LinearLayout {

	public StarWarsScrollerLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@TargetApi(11)
	public void onScrollChanged(int l, int t, int height) {
		int top = t;
		
		for(int z = 0; z < getChildCount(); z++){
			TextView child = (TextView)getChildAt(z);
			
			int childBottom = child.getBottom();
			
			float scale = .3f + ((((float)childBottom - top) / height));
			child.setTextSize(Constants.getPreviousWordSize((Activity)getContext()));
			child.setScaleX(scale);
			child.setScaleY(scale);
			
		}
	}
	
	
}
