package com.example.words.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.example.words.Constants;
import com.example.words.R;
import com.example.words.adapter.UserListAdpater;
import com.example.words.adapter.UserRowData;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class PickOpponentActivity extends Activity implements OnItemClickListener, TextWatcher {

	private ListView userList;
	private ParseUser currentUser;
	private String userId;
	private UserListAdpater adapter;
	private EditText searchBar;


	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Constants.initParse(this); 
        setContentView(R.layout.activity_pick);
        
        userList = (ListView)findViewById(R.id.user_list);
        searchBar = (EditText)findViewById(R.id.search);
        
        currentUser = ParseUser.getCurrentUser();
        userId = currentUser.getObjectId();
        
        setupUserList();
        setupSearchBar();
        
    }

	private void setupUserList() {
		final ArrayList<UserRowData> users = new ArrayList<UserRowData>();
		ParseQuery query = ParseUser.getQuery();
        query.whereNotEqualTo("objectId", userId);
        query.findInBackground(new FindCallback() {
			
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				for(ParseObject obj : objects){
					String userName = obj.getString("displayName");
					String userId = obj.getObjectId();
					users.add(new UserRowData(userId, userName));
				}
				
				adapter = new UserListAdpater(PickOpponentActivity.this, R.layout.user_row, users);
		        userList.setAdapter(adapter);
			}
		});
        
        
        userList.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> a, View v, int position, long id) {
		Intent intent = new Intent();
		intent.setClass(this, GameActivity.class);
		intent.putExtra("NewGame", true);
		
		UserRowData item = adapter.getItem(position);
		intent.putExtra("WaitingPlayerId", item.userId);
		intent.putExtra("WaitingPlayerName", item.userName);
		
		startActivity(intent);
		
		finish();
	}
	
	private void setupSearchBar() {
		searchBar.addTextChangedListener(this);
	}

	@Override
	public void afterTextChanged(Editable s) {
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int count, int after) {
		adapter.setFilter(s.toString());
	}
}
