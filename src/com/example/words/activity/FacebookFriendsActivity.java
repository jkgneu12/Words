package com.example.words.activity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.example.words.adapter.FacebookFriendData;
import com.example.words.adapter.FacebookFriendListAdpater;
import com.example.words.adapter.UserRowData;
import com.example.words.network.FacebookFriendsGetTask;
import com.example.words.network.PushManager;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class FacebookFriendsActivity extends BaseActivity implements TextWatcher, OnItemClickListener {

	private ListView facebookList;
	private EditText searchBar;
	private ParseUser currentUser;
	private String userId;
	private FacebookFriendListAdpater adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Constants.initParse(this); 
		setContentView(R.layout.activity_facebook);

		facebookList = (ListView)findViewById(R.id.facebook_list);
		searchBar = (EditText)findViewById(R.id.search);

		currentUser = ParseUser.getCurrentUser();
		userId = currentUser.getObjectId();
		
		FacebookFriendsGetTask task = new FacebookFriendsGetTask(this);
		task.execute();
		
		setupSearchBar();
		facebookList.setOnItemClickListener(this);
	}

	public void setupList(JSONObject result) {
		
		try {
			final HashMap<String, FacebookFriendData> table = new HashMap<String, FacebookFriendData>();
			ArrayList<String> allIds = new ArrayList<String>();
			JSONArray friends = result.getJSONArray("data");
			for(int z = 0; z < friends.length(); z++){
				JSONObject friend = friends.getJSONObject(z);
				FacebookFriendData d = new FacebookFriendData(friend);
				table.put(friend.getString("id"), d);
				allIds.add(friend.getString("id"));
			}
			final ArrayList<FacebookFriendData> sorted = new ArrayList<FacebookFriendData>();
			
			ParseQuery query = ParseUser.getQuery();
			query.whereContainedIn("facebookId", allIds);
			query.findInBackground(new FindCallback() {
				@Override
				public void done(List<ParseObject> objs, ParseException e) {
					if(e == null){
						for(int z = 0; z < objs.size(); z++){
							ParseUser friend = (ParseUser)objs.get(z);
							String id = friend.getString("facebookId");
							FacebookFriendData d = table.get(id);
							d.active = true;
							d.userName = friend.getUsername();
							d.user = friend.getString("displayName");
							d.userId = friend.getObjectId();
							sorted.add(d);
							table.remove(id);
						}
						sorted.addAll(table.values());
						adapter = new FacebookFriendListAdpater(FacebookFriendsActivity.this, R.layout.facebook_friend_row, sorted);
						facebookList.setAdapter(adapter);
					}
				}
			});
			
			
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private ArrayList<FacebookFriendData> sort(ArrayList<FacebookFriendData> users) {
		ArrayList<FacebookFriendData> sorted = new ArrayList<FacebookFriendData>();
		for(int z = 0; z < users.size(); z++){
			if(users.get(z).active)
				sorted.add(users.get(z));
		}
		sorted.addAll(users);
		return sorted;
	}

	@Override
	public void onItemClick(AdapterView<?> a, View v, int position, long id) {
		FacebookFriendData item = adapter.getItem(position);
		if(item.active){
			startGameWithUser(item);
		} else {
			inviteToSignup(item);
		}
	}

	public void startGameWithUser(FacebookFriendData item) {
		Intent intent = new Intent();
		intent.setClass(this, GameActivity.class);
		intent.putExtra("NewGame", true);

		intent.putExtra("CurrentPlayerId", currentUser.getObjectId());
		intent.putExtra("CurrentPlayerName", currentUser.getString("displayName"));
		intent.putExtra("CurrentPlayerUserName", currentUser.getUsername());
		
		
		intent.putExtra("WaitingPlayerId", item.userId);
		intent.putExtra("WaitingPlayerName", item.user);
		intent.putExtra("WaitingPlayerUserName", item.userName);
		
		startActivity(intent);
		
		finish();
	}
	
	private void inviteToSignup(FacebookFriendData item) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(item.name + " does not play words. Do you want to invite them")
		       .setCancelable(false)
		       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	 	//TODO: send requst
		           }
		       })
		       .setNegativeButton("No", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		builder.show();
	}

	private void setupSearchBar() {
		searchBar.addTextChangedListener(this);
	}
	
	@Override
	public void onTextChanged(CharSequence s, int start, int count, int after) {
		if(adapter != null)
			adapter.setFilter(s.toString());
	}

	@Override
	public void afterTextChanged(Editable arg0) {}
	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,int arg3) {}

	
}
