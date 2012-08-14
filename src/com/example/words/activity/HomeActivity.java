package com.example.words.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
	protected ArrayList<GameRowData> games;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		Constants.initParse(this); 

		currentGamesLayout = (LinearLayout)findViewById(R.id.current_games);
		waitingGamesLayout = (LinearLayout)findViewById(R.id.waiting_games);
		finishedGamesLayout = (LinearLayout)findViewById(R.id.finished_games);

		currentUser = ParseUser.getCurrentUser();
		userId = currentUser.getObjectId();

	}

	@Override
	protected void onResume() {
		super.onResume();
		setupGamesList();
		Constants.checkVersion(this, true);
	}

	private void setupGamesList() {
		ArrayList<ParseQuery> queries = new ArrayList<ParseQuery>();
		queries.add(new ParseQuery("Game").whereEqualTo("currentPlayerId", currentUser.getObjectId()));
		queries.add(new ParseQuery("Game").whereEqualTo("waitingPlayerId", currentUser.getObjectId()));
		ParseQuery query = ParseQuery.or(queries); 
		query.orderByDescending("updatedAt");
		query.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK);
		query.findInBackground(new FindCallback() {
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				games = new ArrayList<GameRowData>();
				if(e != null){
					if(HomeActivity.this.hasWindowFocus()){
						Toast.makeText(HomeActivity.this, "Could not connect to Server. Please try again.", Toast.LENGTH_LONG).show();
						finish();
					}
				} else {
					for(ParseObject obj : objects){
						boolean gameOver = obj.getBoolean("gameOver");
						String currentPlayerId = obj.getString("currentPlayerId");
						int currentPlayerScore = obj.getInt("currentPlayerScore");
						String waitingPlayerId = obj.getString("waitingPlayerId");
						int waitingPlayerScore = obj.getInt("waitingPlayerScore");

						GameRowData data;

						boolean currentPlayer = currentPlayerId.equals(userId);
						if(currentPlayer)
							data = new GameRowData(obj.getObjectId(), obj.getString("waitingPlayerName"), waitingPlayerId, waitingPlayerScore, currentPlayerScore, currentPlayer, gameOver);
						else
							data = new GameRowData(obj.getObjectId(), obj.getString("currentPlayerName"), currentPlayerId, currentPlayerScore, waitingPlayerScore, currentPlayer, gameOver);

						games.add(data);
					}
				}

				buildGamesList();
			}
		});
	}

	protected void buildGamesList() {
		currentGamesLayout.removeAllViews();
		waitingGamesLayout.removeAllViews();
		finishedGamesLayout.removeAllViews();

		LinearLayout layout;
		boolean currentGameFound = false, waitingGameFound = false, finishedGameFound = false;

		for(GameRowData data : games){
			if(data.gameOver){
				layout = finishedGamesLayout;
				finishedGameFound = true;
			}
			else if(data.currentPlayer){
				layout = currentGamesLayout;
				currentGameFound = true;

			} else {
				layout = waitingGamesLayout;
				waitingGameFound = true;
			}

			GameRow row = (GameRow) ((LayoutInflater)HomeActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.game_row, layout, false);
			row.initialize(data);
			//if(data.currentPlayer)
				row.setOnClickListener(HomeActivity.this);

			layout.addView(row);
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

	public void refresh() {
		setupGamesList();
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
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		GameRow row = (GameRow)v;

		Intent intent = new Intent();
		intent.setClass(this, GameActivity.class);
		intent.putExtra("NewGame", false);
		intent.putExtra("MyTurn", row.getParent() == currentGamesLayout);
		intent.putExtra("GameOver", row.getParent() == finishedGamesLayout);

		GameRowData item = row.getData();

		intent.putExtra("CurrentPlayerId", currentUser.getObjectId());
		intent.putExtra("CurrentPlayerName", currentUser.getString("displayName"));
		intent.putExtra("CurrentPlayerScore", item.yourScore);
		intent.putExtra("WaitingPlayerId", item.opponentId);
		intent.putExtra("WaitingPlayerName", item.opponent);
		intent.putExtra("WaitingPlayerScore", item.opponentScore);

		intent.putExtra("id", item.id);
		startActivity(intent);
	}


}
