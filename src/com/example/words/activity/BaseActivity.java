package com.example.words.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.example.words.AppController;
import com.example.words.R;
import com.example.words.Utils;
import com.example.words.view.Ticker;

public class BaseActivity extends FragmentActivity {

	protected AppController appController;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appController = (AppController)getApplication();

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		View ticker = findViewById(R.id.ticker);
		if(ticker != null){
			if(Utils.getSharedPrefBool(this, "hideTicker", false)){
				((Ticker)ticker).pauseScroll();
				((Ticker)ticker).setVisibility(View.GONE);
			} else {
				((Ticker)ticker).resumeScroll();
				((Ticker)ticker).setVisibility(View.VISIBLE);
			}
		}
	}
	
	public AppController getAppController(){
		return ((AppController)getApplication());
	}
	
}
