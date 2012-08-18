package com.example.words.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.example.words.Constants;
import com.example.words.R;
import com.example.words.view.Ticker;

public class BaseActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		View ticker = findViewById(R.id.ticker);
		if(ticker != null){
			if(Constants.getSharedPrefBool(this, "hideTicker", false)){
				((Ticker)ticker).pauseScroll();
				((Ticker)ticker).setVisibility(View.GONE);
			} else {
				((Ticker)ticker).resumeScroll();
				((Ticker)ticker).setVisibility(View.VISIBLE);
			}
		}
	}
	
}
