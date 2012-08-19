package com.example.words.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.words.Constants;
import com.example.words.R;
import com.example.words.adapter.GameRowData;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class NewGameActivity extends BaseActivity implements OnClickListener {

	private Button facebook;
	private Button username;
	private Button random;
	private ParseUser currentUser;
	private String userId;
	private ArrayList<GameRowData> games;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Constants.initParse(this);
		setContentView(R.layout.activity_new);
		
		facebook = (Button)findViewById(R.id.facebook);
		username = (Button)findViewById(R.id.username);
		random = (Button)findViewById(R.id.random);
		
		facebook.setOnClickListener(this);
		username.setOnClickListener(this);
		random.setOnClickListener(this);
		
		currentUser = ParseUser.getCurrentUser();
        userId = currentUser.getObjectId();
        
        games = getIntent().getParcelableArrayListExtra("games");
	}

	@Override
	public void onClick(View v) {
		final Intent intent = new Intent();
		intent.putExtra("games", games);
		
		if(v == facebook){
			intent.setClass(this, FacebookFriendsActivity.class);
			startActivity(intent);
			finish();
		} else if (v == username) {
			intent.setClass(this, PickOpponentActivity.class);
			startActivity(intent);
			finish();
		} else if (v == random) {
			ParseQuery query = ParseUser.getQuery();
	        query.whereNotEqualTo("objectId", userId);
	        query.findInBackground(new FindCallback() {
				
				@Override
				public void done(List<ParseObject> objects, ParseException e) {
					int index = (int)(Math.random() * objects.size());
					ParseUser user = (ParseUser)objects.get(index);
					
					intent.setClass(NewGameActivity.this, GameActivity.class);
					intent.putExtra("NewGame", true);
					
					intent.putExtra("WaitingPlayerId", user.getObjectId());
					intent.putExtra("WaitingPlayerName", user.getString("displayName"));
					intent.putExtra("WaitingPlayerUserName", user.getUsername());
					
					startActivity(intent);
					
					finish();
				}
			});
		}
	}
}
