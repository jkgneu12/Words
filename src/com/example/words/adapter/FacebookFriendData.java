package com.example.words.adapter;

import org.json.JSONException;
import org.json.JSONObject;

public class FacebookFriendData extends Data {

	public String id;
	public String name;
	public boolean active;
	
	public String user;
	public String userName;
	public String userId;

	public FacebookFriendData(JSONObject friend) throws JSONException {
		this.id = (String)friend.get("id");
		this.name = (String)friend.get("name");
		this.active = false;
	}

	@Override
	public String getFilterableName() {
		return name.toLowerCase();
	}
}
