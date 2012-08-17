package com.example.words.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.example.words.view.Row;

public abstract class FilteredListAdapter<D extends Data,V extends Row<D>> extends ListAdapter<D, V> {
	
	protected String filter;
	protected List<D> filteredData;

	public FilteredListAdapter(Context context, int textViewResourceId, List<D> objects) {
		super(context, textViewResourceId, objects);
		
		filteredData = objects;
	}
	
	public void setFilter(String s) {
		this.filter = s;
		this.filteredData = new ArrayList<D>();
		for(D row : data){
			if(row.getFilterableName().contains(s.toLowerCase()))
				filteredData.add(row);
		}
		notifyDataSetChanged();
	}
	
	public D getItem(int position) {
		return filteredData.get(position);
	};
	
	@Override
	public int getCount() {
		return filteredData.size();
	}

}
