package com.example.words.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.words.R;
import com.example.words.view.FacebookFriendRow;

public class FacebookFriendListAdpater extends FilteredListAdapter<FacebookFriendData, FacebookFriendRow> {

	public FacebookFriendListAdpater(Context context, int textViewResourceId, List<FacebookFriendData> objects) {
		super(context, textViewResourceId, objects);
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		FacebookFriendRow row = (FacebookFriendRow)super.getView(position, convertView, parent);
		
		TextView active = ((TextView)row.findViewById(R.id.active));
		if(getItem(position).active)
			active.setText("Active");
		else 
			active.setText("");
		return row;
	}

}
