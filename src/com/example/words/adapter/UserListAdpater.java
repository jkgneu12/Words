package com.example.words.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.words.view.UserRow;

public class UserListAdpater extends ArrayAdapter<UserRowData> {

	private int resourceId;
	private LayoutInflater mLayoutInflater;

	public UserListAdpater(Context context, int textViewResourceId, List<UserRowData> objects) {
		super(context, textViewResourceId, objects);
		resourceId = textViewResourceId;
		mLayoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		UserRow row;
		if(convertView == null)
			row = (UserRow)mLayoutInflater.inflate(resourceId, parent, false);
		else
			row = (UserRow)convertView;
		
		row.initialize(getItem(position));
		
		return row;
	}

}
