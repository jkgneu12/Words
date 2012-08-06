package com.example.words.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.words.activity.HomeActivity;

public class NewGameButton extends Button implements OnClickListener{

	public NewGameButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		setOnClickListener(this);
	}
	
	@Override
	public void onClick(View arg0) {
		((HomeActivity)getContext()).newGame();
	}

}
