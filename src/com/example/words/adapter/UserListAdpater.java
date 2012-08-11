package com.example.words.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.example.words.view.UserRow;

public class UserListAdpater extends ListAdapter<UserRowData, UserRow> {

	private String filter;
	protected List<UserRowData> filteredData;
	
	public UserListAdpater(Context context, int textViewResourceId, List<UserRowData> objects) {
		super(context, textViewResourceId, objects);
		
		filteredData = objects;
	}

	public void setFilter(String s) {
		this.filter = s;
		this.filteredData = new ArrayList<UserRowData>();
		for(UserRowData row : data){
			if(row.userName.toLowerCase().contains(s.toLowerCase()))
				filteredData.add(row);
		}
		notifyDataSetChanged();
	}
	
	public UserRowData getItem(int position) {
		return filteredData.get(position);
	};
	
	@Override
	public int getCount() {
		return filteredData.size();
	}
}
