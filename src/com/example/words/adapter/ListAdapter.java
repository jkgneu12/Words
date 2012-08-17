package com.example.words.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.words.view.Row;

public class ListAdapter<D extends Data, V extends Row<D>> extends ArrayAdapter<D> {
	
	protected LayoutInflater mLayoutInflater;
	protected int resourceId;
	protected List<D> data;
	
	public ListAdapter(Context context, int textViewResourceId, List<D> objects) {
		super(context, textViewResourceId, objects);
		resourceId = textViewResourceId;
		mLayoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		data = objects;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		V row;
		if(convertView == null)
			row = (V)mLayoutInflater.inflate(resourceId, parent, false);
		else
			row = (V)convertView;
		
		row.initialize(getItem(position));
		
		return row;
	}
	
	
}
