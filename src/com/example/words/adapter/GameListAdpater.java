package com.example.words.adapter;

import java.util.List;

import android.content.Context;

import com.example.words.view.GameRow;

public class GameListAdpater extends ListAdapter<GameRowData, GameRow> {

	public GameListAdpater(Context context, int textViewResourceId, List<GameRowData> objects) {
		super(context, textViewResourceId, objects);
	}
}
