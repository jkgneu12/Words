package com.example.words.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public abstract class Row<D> extends RelativeLayout {

	public Row(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public abstract void initialize(D data);

}
