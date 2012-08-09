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

import com.example.words.R;
import com.example.words.adapter.GameRowData;
import com.example.words.view.GameRow;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class HomeActivity extends Activity implements OnClickListener  {

	private ParseUser currentUser;
	private String userId;
	private LinearLayout currentGamesLayout;
	private LinearLayout waitingGamesLayout;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        Parse.initialize(this, "VhnMRCE8J0r9fJLuXvGWMQvdNEw6GSxoAQCApqf2", "r4BwcVVLoX7wo92garHMfPa10O6xdmlVIS57ymt8"); 
        
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
        query.findInBackground(new FindCallback() {
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				currentGamesLayout.removeAllViews();
				waitingGamesLayout.removeAllViews();
				
				boolean currentGameFound = false, waitingGameFound = false;
				
				for(ParseObject obj : objects){
					String currentPlayerId = obj.getString("currentPlayerId");
					int currentPlayerScore = obj.getInt("currentPlayerScore");
					String waitingPlayerId = obj.getString("waitingPlayerId");
					int waitingPlayerScore = obj.getInt("waitingPlayerScore");
					
					GameRowData data;
					LinearLayout layout;
					
					boolean currentPlayer = currentPlayerId.equals(userId);
					if(currentPlayer){
						data = new GameRowData(obj.getObjectId(), obj.getString("waitingPlayerName"), waitingPlayerId, waitingPlayerScore, currentPlayerScore);
						layout = currentGamesLayout;
						currentGameFound = true;
					}
					else {
						data = new GameRowData(obj.getObjectId(), obj.getString("currentPlayerName"), currentPlayerId, currentPlayerScore, waitingPlayerScore);
						layout = waitingGamesLayout;
						waitingGameFound = true;
					}
					
					GameRow row = (GameRow) ((LayoutInflater)HomeActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.game_row, layout, false);
					row.initialize(data);
					if(currentPlayer)
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
		});
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
		intent.setClass(this, PickOpponentActivity.class);
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
