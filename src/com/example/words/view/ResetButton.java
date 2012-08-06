package com.example.words.view;

import com.example.words.activity.GameActivity;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ResetButton extends Button implements OnClickListener {

	public ResetButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		setText("RESET");
		
		setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		((GameActivity)getContext()).reset();
	}

}
