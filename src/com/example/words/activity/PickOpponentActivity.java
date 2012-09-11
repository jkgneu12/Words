package com.example.words.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.example.words.R;
import com.example.words.Utils;
import com.example.words.adapter.GameRowData;
import com.example.words.adapter.UserListAdpater;
import com.example.words.adapter.UserRowData;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class PickOpponentActivity extends BaseActivity implements OnItemClickListener, TextWatcher {

	private ListView userList;
	private ParseUser currentUser;
	private String userId;
	private UserListAdpater adapter;
	private EditText searchBar;
	private ArrayList<GameRowData> games;


	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.initParse(this); 
        setContentView(R.layout.activity_pick);
        
        userList = (ListView)findViewById(R.id.user_list);
        searchBar = (EditText)findViewById(R.id.search);
        
        currentUser = ParseUser.getCurrentUser();
        userId = currentUser.getObjectId();
        
        setupUserList();
        setupSearchBar();
        
        games = getIntent().getParcelableArrayListExtra("games");
        
    }

	private void setupUserList() {
		final ArrayList<UserRowData> users = new ArrayList<UserRowData>();
		ParseQuery query = ParseUser.getQuery();
        query.whereNotEqualTo("objectId", userId);
        query.findInBackground(new FindCallback() {
			
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				for(ParseObject obj : objects){
					ParseUser pUser = (ParseUser)obj;
					String user = pUser.getString("displayName");
					String userName = pUser.getUsername();
					String userId = pUser.getObjectId();
					users.add(new UserRowData(userId, user, userName));
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
		
		GameRowData row = new GameRowData(null, item.user, item.userName, item.userId, 0, 0, true, false);
		
		games.add(0, row);
		
		intent.putExtra("item", row);
		intent.putExtra("games", games);
		
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
