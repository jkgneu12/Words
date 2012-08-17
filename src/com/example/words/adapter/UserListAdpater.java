package com.example.words.adapter;

import java.util.List;

import android.content.Context;

import com.example.words.view.UserRow;

public class UserListAdpater extends FilteredListAdapter<UserRowData, UserRow> {

	public UserListAdpater(Context context, int textViewResourceId, List<UserRowData> objects) {
		super(context, textViewResourceId, objects);
		
		
	}
	
}
