package com.example.words.activity;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.words.Constants;
import com.example.words.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class NewGameActivity extends Activity implements OnClickListener {

	private Button facebook;
	private Button username;
	private Button random;
	private ParseUser currentUser;
	private String userId;

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
	}

	@Override
	public void onClick(View v) {
		if(v == facebook){
			Intent intent = new Intent();
			intent.setClass(this, PickOpponentActivity.class);
			startActivity(intent);
		} else if (v == username) {
			Intent intent = new Intent();
			intent.setClass(this, PickOpponentActivity.class);
			startActivity(intent);
		} else if (v == random) {
			
			ParseQuery query = ParseUser.getQuery();
	        query.whereNotEqualTo("objectId", userId);
	        query.findInBackground(new FindCallback() {
				
				@Override
				public void done(List<ParseObject> objects, ParseException e) {
					int index = (int)(Math.random() * objects.size());
					ParseObject user = objects.get(index);
					
					Intent intent = new Intent();
					intent.setClass(NewGameActivity.this, GameActivity.class);
					intent.putExtra("NewGame", true);
					
					intent.putExtra("WaitingPlayerId", user.getObjectId());
					intent.putExtra("WaitingPlayerName", user.getString("username"));
					
					startActivity(intent);
					
					finish();
				}
			});
		}
	}
}