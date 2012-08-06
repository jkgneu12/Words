package com.example.words.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.words.view.GameRow;

public class GameListAdpater extends ArrayAdapter<GameRowData> {

	private LayoutInflater mLayoutInflater;
	private int resourceId;

	public GameListAdpater(Context context, int textViewResourceId, List<GameRowData> objects) {
		super(context, textViewResourceId, objects);
		resourceId = textViewResourceId;
		mLayoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return super.getCount();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		GameRow row;
		if(convertView == null)
			row = (GameRow)mLayoutInflater.inflate(resourceId, parent, false);
		else
			row = (GameRow)convertView;
		
		row.initialize(getItem(position));
		
		return row;
	}
}
