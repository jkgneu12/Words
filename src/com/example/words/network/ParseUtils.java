package com.example.words.network;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.widget.Toast;

import com.example.words.adapter.GameRowData;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQuery.CachePolicy;
import com.parse.ParseUser;

public class ParseUtils {

	public static void getGamesLists(final Activity activity, final ArrayList<GameRowData> currentGames, 
			final ArrayList<GameRowData> waitingGames, final ArrayList<GameRowData> finishedGames, 
			final ProgressDialog waiting, final ParseUser currentUser, final CachePolicy cachePolicy, final Runnable callback){
		
		ArrayList<ParseQuery> queries = new ArrayList<ParseQuery>();
		queries.add(new ParseQuery("Game").whereEqualTo("currentPlayerId", currentUser.getObjectId()));
		queries.add(new ParseQuery("Game").whereEqualTo("waitingPlayerId", currentUser.getObjectId()));
		ParseQuery query = ParseQuery.or(queries); 
		query.orderByDescending("updatedAt");
		query.setCachePolicy(cachePolicy);
		
		query.findInBackground(new FindCallback() {
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				if(e != null){
					if(activity.hasWindowFocus()){
						Toast.makeText(activity, "Could not connect to Server. Please try again.", Toast.LENGTH_LONG).show();
						activity.finish();
					}
				} else {
					for(ParseObject obj : objects){
						boolean gameOver = obj.getBoolean("gameOver");
						String currentPlayerId = obj.getString("currentPlayerId");
						int currentPlayerScore = obj.getInt("currentPlayerScore");
						String waitingPlayerId = obj.getString("waitingPlayerId");
						int waitingPlayerScore = obj.getInt("waitingPlayerScore");

						GameRowData data;

						boolean currentPlayer = currentPlayerId.equals(currentUser.getObjectId());
						if(currentPlayer)
							data = new GameRowData(obj.getObjectId(), obj.getString("waitingPlayerName"), obj.getString("waitingPlayerUserName"), waitingPlayerId, waitingPlayerScore, currentPlayerScore, currentPlayer, gameOver);
						else
							data = new GameRowData(obj.getObjectId(), obj.getString("currentPlayerName"), obj.getString("currentPlayerUserName"), currentPlayerId, currentPlayerScore, waitingPlayerScore, currentPlayer, gameOver);

						if(data.isGameOver)
							finishedGames.add(data);
						else if(data.isCurrentPlayer)
							currentGames.add(data);
						else
							waitingGames.add(data);
					}
				}
				
				callback.run();
				
				if(waiting != null && cachePolicy.equals(CachePolicy.NETWORK_ONLY) && waiting.isShowing())
					waiting.dismiss();
			}
		});
	}
}
