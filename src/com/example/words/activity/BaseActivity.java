package com.example.words.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.example.words.R;
import com.example.words.view.Ticker;

public class BaseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		View ticker = findViewById(R.id.ticker);
		if(ticker != null){
			if(getSharedPreferences("SETTINGS", MODE_PRIVATE).getBoolean("hideTicker", false)){
				((Ticker)ticker).pauseScroll();
				((Ticker)ticker).setVisibility(View.GONE);
			} else {
				((Ticker)ticker).resumeScroll();
				((Ticker)ticker).setVisibility(View.VISIBLE);
			}
		}
	}
	
}
