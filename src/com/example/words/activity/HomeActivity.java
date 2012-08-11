package com.example.words.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
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

import com.example.words.Constants;
import com.example.words.R;
import com.example.words.adapter.GameRowData;
import com.example.words.view.GameRow;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQuery.CachePolicy;
import com.parse.ParseUser;

public class HomeActivity extends Activity implements OnClickListener  {

	private ParseUser currentUser;
	private String userId;
	private LinearLayout currentGamesLayout;
	private LinearLayout waitingGamesLayout;
	protected ArrayList<GameRowData> games;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        Constants.initParse(this); 
        
        currentGamesLayout = (LinearLayout)findViewById(R.id.current_games);
        waitingGamesLayout = (LinearLayout)findViewById(R.id.waiting_games);
       
        currentUser = ParseUser.getCurrentUser();
        userId = currentUser.getObjectId();
        
    }
	
	@Override
	protected void onResume() {
		super.onResume();
		setupGamesList();
	}
	
	private void setupGamesList() {
        ArrayList<ParseQuery> queries = new ArrayList<ParseQuery>();
        queries.add(new ParseQuery("Game").whereEqualTo("currentPlayerId", currentUser.getObjectId()));
        queries.add(new ParseQuery("Game").whereEqualTo("waitingPlayerId", currentUser.getObjectId()));
        ParseQuery query = ParseQuery.or(queries); 
        query.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK);
        query.findInBackground(new FindCallback() {
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				games = new ArrayList<GameRowData>();
				if(e != null){
				} else {
					for(ParseObject obj : objects){
						String currentPlayerId = obj.getString("currentPlayerId");
						int currentPlayerScore = obj.getInt("currentPlayerScore");
						String waitingPlayerId = obj.getString("waitingPlayerId");
						int waitingPlayerScore = obj.getInt("waitingPlayerScore");
						
						GameRowData data;
						
						boolean currentPlayer = currentPlayerId.equals(userId);
						if(currentPlayer){
							data = new GameRowData(obj.getObjectId(), obj.getString("waitingPlayerName"), waitingPlayerId, waitingPlayerScore, currentPlayerScore, currentPlayer);
							
						}
						else {
							data = new GameRowData(obj.getObjectId(), obj.getString("currentPlayerName"), currentPlayerId, currentPlayerScore, waitingPlayerScore, currentPlayer);
							
						}
						
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
		
		LinearLayout layout;
		boolean currentGameFound = false, waitingGameFound = false;
		
		for(GameRowData data : games){
			if(data.currentPlayer){
				layout = currentGamesLayout;
				currentGameFound = true;
				
			} else {
				layout = waitingGamesLayout;
				waitingGameFound = true;
			}
			
			GameRow row = (GameRow) ((LayoutInflater)HomeActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.game_row, layout, false);
			row.initialize(data);
			if(data.currentPlayer)
				row.setOnClickListener(HomeActivity.this);
			
			layout.addView(row);
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
        MenuItem item = menu.findItem(R.id.refresh);

        if (item == null)
            return true;

        item.setOnMenuItemClickListener(
            new MenuItem.OnMenuItemClickListener () { 
                public boolean onMenuItemClick(MenuItem item) { 
                	refresh();
                	return true;
                }
            } 
        ); 

        return true;
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
		
		GameRowData item = row.getData();
		
		intent.putExtra("CurrentPlayerId", currentUser.getObjectId());
		intent.putExtra("CurrentPlayerName", currentUser.getUsername());
		intent.putExtra("CurrentPlayerScore", item.yourScore);
		intent.putExtra("WaitingPlayerId", item.opponentId);
		intent.putExtra("WaitingPlayerName", item.opponent);
		intent.putExtra("WaitingPlayerScore", item.opponentScore);
	
		intent.putExtra("id", item.id);
		startActivity(intent);
	}

    
}
