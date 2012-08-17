package com.example.words.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.words.R;
import com.example.words.adapter.FacebookFriendData;

public class FacebookFriendRow extends Row<FacebookFriendData> {

	private TextView name;

	public FacebookFriendRow(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		this.name = (TextView)findViewById(R.id.name);
	}
	
	public void initialize(FacebookFriendData data){
		super.initialize(data);
		name.setText(data.name);
	
	}

}
