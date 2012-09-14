package com.example.words.activity;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuItem;

import com.example.words.GameUpdateListener;
import com.example.words.R;
import com.example.words.Utils;
import com.example.words.adapter.GameAdpater;
import com.example.words.adapter.GameRowData;
import com.example.words.listener.ShakeEventListener;
import com.example.words.network.ParseUtils;
import com.parse.ParseQuery.CachePolicy;
import com.parse.ParseUser;
import com.viewpagerindicator.TitlePageIndicator;
import com.viewpagerindicator.TitlePageIndicator.IndicatorStyle;

public class GameActivity extends BaseActivity implements OnPageChangeListener, GameUpdateListener {

	
	private GameAdpater adapter;
	
	private SensorManager mSensorManager;
	private ShakeEventListener mSensorListener;

	public ViewPager pager;

	private TitlePageIndicator indicator;
	
	
	
	public ParseUser currentUser;

	public ArrayList<GameRowData> games;

	public String[] fragTags;

	public boolean isFirstFragmentLoad = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_game);
		
		currentUser = ParseUser.getCurrentUser();
		
		getAppController().registerGameUpdateListener(this);
		
		games = getIntent().getParcelableArrayListExtra("games");
		if(games == null){
			final ProgressDialog waiting = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
			waiting.setTitle("Please Wait");
			waiting.setMessage("Loading Games");
			waiting.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					finish();
				}
			});
			waiting.show();
			
			Runnable callback = new Runnable() {
				
				@Override
				public void run() {
					finishInitAfterDataReturned(null);
				}
			};
			
			games = ParseUtils.getGamesLists(this, waiting, currentUser, CachePolicy.NETWORK_ONLY, callback);
		} else {
			finishInitAfterDataReturned(savedInstanceState);
		}

	}

	private void finishInitAfterDataReturned(Bundle savedInstanceState) {
		String mainId = getIntent().getParcelableExtra("GameId");
		for(int z = 0; z < games.size(); z++){
			String id = games.get(z).id;
			if(id == null || id.equals(mainId)){
				pager.setCurrentItem(z);
				break;
			}
		}
		
		adapter = new GameAdpater(this, getSupportFragmentManager(), getIntent().getBooleanExtra("NewGame", false));
		
		if(savedInstanceState != null){
			adapter.setFragmentTags(savedInstanceState.getStringArray("fragTags"));
		}
		
		pager = (ViewPager) findViewById(R.id.viewpager);
		pager.setAdapter(adapter);
		
		final float density = getResources().getDisplayMetrics().density;
		indicator = (TitlePageIndicator)findViewById(R.id.titles);
		indicator.setViewPager(pager);
		//indicator.setBackgroundColor(0x18FF0000);
        indicator.setFooterColor(0xee772525);
        indicator.setFooterLineHeight(1 * density); //1dp
        indicator.setFooterIndicatorHeight(3 * density); //3dp
        indicator.setFooterIndicatorStyle(IndicatorStyle.Underline);
        //indicator.setTextColor(0xAA000000);
        //indicator.setSelectedColor(0xFF000000);
        indicator.setSelectedBold(true);
        
        indicator.setOnPageChangeListener(this);
        
        setupShakeListener();
	}

	private GameFragment getCurrentFragment(){
		return (GameFragment)adapter.getItem(pager.getCurrentItem());
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(Build.VERSION.SDK_INT >= 11)
			hideActionBar();
		mSensorManager.registerListener(mSensorListener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_UI);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		appController.unregisterGameUpdateListener(this);
	}

	@TargetApi(11)
	public void hideActionBar() {
		if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
			getActionBar().hide();
	}

	@Override
	protected void onPause() {
		mSensorManager.unregisterListener(mSensorListener);
		super.onStop();
	}

	@Override
	protected void onRestoreInstanceState(Bundle bundle) {
		games = bundle.getParcelableArrayList("games");
		fragTags = bundle.getStringArray("fragTags");
		isFirstFragmentLoad = bundle.getBoolean("isFirstFragmentLoad");
		super.onRestoreInstanceState(bundle);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putParcelableArrayList("games", games);
		outState.putStringArray("fragTags", adapter.getFragmentTags());
		outState.putBoolean("isFirstFragmentLoad", isFirstFragmentLoad);
		super.onSaveInstanceState(outState);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_game, menu);

		MenuItem refresh = menu.findItem(R.id.chat);


		refresh.setOnMenuItemClickListener(
				new MenuItem.OnMenuItemClickListener () { 
					public boolean onMenuItemClick(MenuItem item) { 
						chat();
						return true;
					}
				} 
				); 

		return true;
	}
	
	protected void chat() {
		Intent intent = new Intent();
		intent.setClass(this, ChatActivity.class);
		GameRowData data = getCurrentFragment().gameData;
		intent.putExtra("receivingUser", data.opponentId);
		intent.putExtra("receivingUserName", data.opponentUserName);
		intent.putExtra("receivingName", data.opponent);
		startActivity(intent);
	}
	
	public void setupShakeListener() {
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensorListener = new ShakeEventListener();   

		mSensorListener.setOnShakeListener(new ShakeEventListener.OnShakeListener() {

			public void onShake() {
				if(Utils.getSharedPrefBool(GameActivity.this, "shakeShuffle", true))
					getCurrentFragment().shuffleTiles();
			}
		});
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		if(ViewPager.SCROLL_STATE_IDLE == state){
			getCurrentFragment().onFragmentShown();
		}
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {}

	@Override
	public void onPageSelected(int index) {}

	@Override
	public void refresh(String id) {
		if(getCurrentFragment().game != null && getCurrentFragment().game.id.equals(id))
			getCurrentFragment().game.fullRefresh();
	}
}
