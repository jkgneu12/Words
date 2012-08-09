package com.example.words.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public abstract class Row<D> extends RelativeLayout {
	
	protected D data;

	public Row(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void initialize(D data){
		this.data = data;
	}
	
	public D getData() {
		return data;
	}

}
