package com.example.words.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.words.activity.GameActivity;

public class SubmitButton extends Button implements OnClickListener {

	public SubmitButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		setText("SUBMIT");
		
		setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		((GameActivity)getContext()).submit();
	}

}
