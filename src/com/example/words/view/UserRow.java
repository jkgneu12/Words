package com.example.words.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.words.R;
import com.example.words.adapter.UserRowData;

public class UserRow extends LinearLayout{
	
	TextView name;

	public UserRow(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		this.name = (TextView)findViewById(R.id.name);
	}
	
	public void initialize(UserRowData data){
		name.setText(data.userName);
	
	}

}
