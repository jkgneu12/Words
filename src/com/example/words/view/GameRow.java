package com.example.words.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.words.R;
import com.example.words.adapter.GameRowData;

public class GameRow extends RelativeLayout{
	
	TextView opponent;
	TextView opponentScore;
	TextView yourScore;

	public GameRow(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		this.opponent = (TextView)findViewById(R.id.opponent);
		this.opponentScore = (TextView)findViewById(R.id.opponent_score);
		this.yourScore = (TextView)findViewById(R.id.your_score);
	}
	
	public void initialize(GameRowData data){
		opponent.setText(data.opponent);
		opponentScore.setText("" + data.opponentScore);
		yourScore.setText("" + data.yourScore);
	
	}

}
