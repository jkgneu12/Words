package com.example.words.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.words.Constants;
import com.example.words.R;
import com.example.words.adapter.GameRowData;
import com.example.words.network.ParseUtils;
import com.example.words.view.GameRow;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQuery.CachePolicy;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class HomeActivity extends BaseActivity implements OnClickListener  {

	private ParseUser currentUser;
	private String userId;
	private LinearLayout currentGamesLayout;
	private LinearLayout waitingGamesLayout;
	private LinearLayout finishedGamesLayout;
	protected ArrayList<GameRowData> currentGames;
	protected ArrayList<GameRowData> waitingGames;
	protected ArrayList<GameRowData> finishedGames;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.e("onCreate", "HomeActivity");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		Constants.initParse(this); 

		currentGamesLayout = (LinearLayout)findViewById(R.id.current_games);
		waitingGamesLayout = (LinearLayout)findViewById(R.id.waiting_games);
		finishedGamesLayout = (LinearLayout)findViewById(R.id.finished_games);

		currentUser = ParseUser.getCurrentUser();
		userId = currentUser.getObjectId();
		
		setupGamesList(savedInstanceState != null ? CachePolicy.CACHE_ONLY : CachePolicy.NETWORK_ONLY);
		Constants.checkVersion(this, true);

	}
	
	@Override
	protected void onRestart() {
		Log.e("onRestart", "HomeActivity");
		super.onRestart();
		resetGamesList();
		setupGamesList(CachePolicy.NETWORK_ONLY);
	}

	private void resetGamesList() {
		currentGames = new ArrayList<GameRowData>();
		waitingGames = new ArrayList<GameRowData>();
		finishedGames = new ArrayList<GameRowData>();
		buildGamesList();
	}

	private void setupGamesList(final CachePolicy cachePolicy) {

		currentGames = new ArrayList<GameRowData>();
		waitingGames = new ArrayList<GameRowData>();
		finishedGames = new ArrayList<GameRowData>();
		
		final ProgressDialog waiting = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
		if(cachePolicy.equals(CachePolicy.NETWORK_ONLY)){
			waiting.setTitle("Please Wait");
			waiting.setMessage("Loading Games");
			waiting.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					finish();
				}
			});
			waiting.show();
		}
		
		ParseUtils.getGamesLists(this, currentGames, waitingGames, finishedGames, waiting, currentUser, cachePolicy, new Runnable() {
			
			@Override
			public void run() {
				buildGamesList();
			}
		});

	}

	protected void buildGamesList() {
		currentGamesLayout.removeAllViews();
		waitingGamesLayout.removeAllViews();
		finishedGamesLayout.removeAllViews();

		boolean currentGameFound = false, waitingGameFound = false, finishedGameFound = false;

		for(GameRowData data : currentGames){
			addRowToList(data, currentGamesLayout);
			currentGameFound = true;
		}
		
		for(GameRowData data : waitingGames){
			addRowToList(data, waitingGamesLayout);
			waitingGameFound = true;
		}
		
		for(GameRowData data : finishedGames){
			addRowToList(data, finishedGamesLayout);
			finishedGameFound = true;
		}
		
		if(!finishedGameFound){
			TextView noGames = new TextView(HomeActivity.this);
			noGames.setText("No Games");
			noGames.setGravity(Gravity.CENTER_HORIZONTAL);
			finishedGamesLayout.addView(noGames);
		}

		if(!currentGameFound){
			TextView noGames = new TextView(HomeActivity.this);
			noGames.setText("No Games");
			noGames.setGravity(Gravity.CENTER_HORIZONTAL);
			currentGamesLayout.addView(noGames);
		}

		if(!waitingGameFound){
			TextView noGames = new TextView(HomeActivity.this);
			noGames.setText("No Games");
			noGames.setGravity(Gravity.CENTER_HORIZONTAL);
			waitingGamesLayout.addView(noGames);
		}
	}

	private void addRowToList(GameRowData data, LinearLayout layout) {
		GameRow row = (GameRow) ((LayoutInflater)HomeActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.game_row, layout, false);
		row.initialize(data);
		row.setOnClickListener(HomeActivity.this);

		layout.addView(row);
	}

	public void refresh() {
		setupGamesList(CachePolicy.NETWORK_ONLY);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_home, menu);
		MenuItem refresh = menu.findItem(R.id.refresh);


		refresh.setOnMenuItemClickListener(
			new MenuItem.OnMenuItemClickListener () { 
				public boolean onMenuItemClick(MenuItem item) { 
					refresh();
					return true;
				}
			} 
		); 
		
		MenuItem settings = menu.findItem(R.id.settings);


		settings.setOnMenuItemClickListener(
			new MenuItem.OnMenuItemClickListener () { 
				public boolean onMenuItemClick(MenuItem item) { 
					settings();
					return true;
				}
			} 
		);
		
		if(!ParseFacebookUtils.isLinked(currentUser)){
			MenuItem link = menu.findItem(R.id.link_facebook);

			link.setOnMenuItemClickListener(
				new MenuItem.OnMenuItemClickListener () { 
					public boolean onMenuItemClick(MenuItem item) { 
						linkFacebook();
						return true;
					}
				} 
			); 
		} else {
			menu.removeItem(R.id.link_facebook);
		}
		
		if(!ParseTwitterUtils.isLinked(currentUser)){
			MenuItem link = menu.findItem(R.id.link_twitter);

			link.setOnMenuItemClickListener(
				new MenuItem.OnMenuItemClickListener () { 
					public boolean onMenuItemClick(MenuItem item) { 
						linkTwitter();
						return true;
					}
				} 
			); 
		} else {
			menu.removeItem(R.id.link_twitter);
		}

		return true;
	}

	private void settings() {
		Intent intent = new Intent();
		intent.setClass(this, SettingsActivity.class);
		startActivity(intent);
	}

	private void linkFacebook() {
		ParseFacebookUtils.link(currentUser, this, new SaveCallback() {

			@Override
			public void done(ParseException e) {
				if(e == null)
					Toast.makeText(HomeActivity.this, "Facebook Account Linked ", Toast.LENGTH_LONG).show();
				else
					Toast.makeText(HomeActivity.this, "Link Failed " + e.getMessage(), Toast.LENGTH_LONG).show();
			}
		});
	}
	
	private void linkTwitter() {
		ParseTwitterUtils.link(currentUser, this, new SaveCallback() {

			@Override
			public void done(ParseException e) {
				if(e == null)
					Toast.makeText(HomeActivity.this, "Twitter Account Linked ", Toast.LENGTH_LONG).show();
				else
					Toast.makeText(HomeActivity.this, "Link Failed " + e.getMessage(), Toast.LENGTH_LONG).show();
			}
		});
	}

	public void newGame() {	
		Intent intent = new Intent();
		intent.setClass(this, NewGameActivity.class);
		intent.putExtra("games", compileAllGames());
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		GameRow row = (GameRow)v;

		Intent intent = new Intent();
		intent.setClass(this, GameActivity.class);

		GameRowData item = row.getData();
		
		intent.putExtra("item", item);
		intent.putExtra("games", compileAllGames());
		
		startActivity(intent);
	}

	public ArrayList<GameRowData> compileAllGames() {
		ArrayList<GameRowData> games = new ArrayList<GameRowData>(currentGames);
		games.addAll(waitingGames);
		games.addAll(finishedGames);
		return games;
	}


}
