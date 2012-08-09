package com.example.words.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.words.R;
import com.example.words.adapter.GameListAdpater;
import com.example.words.adapter.GameRowData;
import com.example.words.view.NewGameButton;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class HomeActivity extends Activity implements OnItemClickListener {

    private NewGameButton newGame;
	private ListView currentGameList;
	private GameListAdpater currentAdapter;
	private GameListAdpater waitingAdapter;
	private ParseUser currentUser;
	private String userId;
	private ListView waitingGameList;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        Parse.initialize(this, "VhnMRCE8J0r9fJLuXvGWMQvdNEw6GSxoAQCApqf2", "r4BwcVVLoX7wo92garHMfPa10O6xdmlVIS57ymt8"); 
        
        newGame = (NewGameButton)findViewById(R.id.new_game);
        currentGameList = (ListView)findViewById(R.id.current_game_list);
        waitingGameList = (ListView)findViewById(R.id.waiting_game_list);
        
        currentUser = ParseUser.getCurrentUser();
        userId = currentUser.getObjectId();
        
    }
	
	@Override
	protected void onResume() {
		super.onResume();
		setupGamesList();
	}

	private void setupGamesList() {
		final ArrayList<GameRowData> currentGames = new ArrayList<GameRowData>();
		final ArrayList<GameRowData> waitingGames = new ArrayList<GameRowData>();
        
        ArrayList<ParseQuery> queries = new ArrayList<ParseQuery>();
        queries.add(new ParseQuery("Game").whereEqualTo("currentPlayerId", currentUser.getObjectId()));
        queries.add(new ParseQuery("Game").whereEqualTo("waitingPlayerId", currentUser.getObjectId()));
        ParseQuery query = ParseQuery.or(queries); 
        query.findInBackground(new FindCallback() {
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				for(ParseObject obj : objects){
					String currentPlayerId = obj.getString("currentPlayerId");
					int currentPlayerScore = obj.getInt("currentPlayerScore");
					String waitingPlayerId = obj.getString("waitingPlayerId");
					int waitingPlayerScore = obj.getInt("waitingPlayerScore");
					
					
					boolean currentPlayer = currentPlayerId.equals(userId);
					if(currentPlayer)
						currentGames.add(new GameRowData(obj.getObjectId(), obj.getString("waitingPlayerName"), waitingPlayerId, waitingPlayerScore, currentPlayerScore));
					else
						waitingGames.add(new GameRowData(obj.getObjectId(), obj.getString("currentPlayerName"), currentPlayerId, currentPlayerScore, waitingPlayerScore));
					
					
				}
				
				currentAdapter = new GameListAdpater(HomeActivity.this, R.layout.game_row, currentGames);
		        currentGameList.setAdapter(currentAdapter);
		        waitingAdapter = new GameListAdpater(HomeActivity.this, R.layout.game_row, waitingGames);
		        waitingGameList.setAdapter(waitingAdapter);
			}
		});
        
        
        currentGameList.setOnItemClickListener(this);
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
	public void onItemClick(AdapterView<?> a, View v, int position, long id) {
		GameListAdpater gameListAdapter = (GameListAdpater)a.getAdapter();
			
		Intent intent = new Intent();
		intent.setClass(this, GameActivity.class);
		intent.putExtra("NewGame", false);
		
		GameRowData item = gameListAdapter.getItem(position);
		
		if(gameListAdapter == currentAdapter){
			intent.putExtra("CurrentPlayerId", currentUser.getObjectId());
			intent.putExtra("CurrentPlayerName", currentUser.getUsername());
			intent.putExtra("CurrentPlayerScore", item.yourScore);
			intent.putExtra("WaitingPlayerId", item.opponentId);
			intent.putExtra("WaitingPlayerName", item.opponent);
			intent.putExtra("WaitingPlayerScore", item.opponentScore);
		}
		else {
			intent.putExtra("CurrentPlayerId", item.opponentId);
			intent.putExtra("CurrentPlayerName", item.opponent);
			intent.putExtra("CurrentPlayerScore", item.opponentScore);
			intent.putExtra("WaitingPlayerId", currentUser.getObjectId());
			intent.putExtra("WaitingPlayerName", currentUser.getUsername());
			intent.putExtra("WaitingPlayerScore", item.yourScore);
		}
		intent.putExtra("id", item.id);
		startActivity(intent);
	}

    
}
