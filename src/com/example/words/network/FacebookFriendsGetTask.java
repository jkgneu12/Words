package com.example.words.network;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;

import android.os.AsyncTask;

import com.example.words.activity.FacebookFriendsActivity;
import com.parse.ParseFacebookUtils;
import com.parse.facebook.Util;

public class FacebookFriendsGetTask extends AsyncTask<String, Object, JSONObject>{
	
	private FacebookFriendsActivity activity;

	public FacebookFriendsGetTask(FacebookFriendsActivity activity) {
		this.activity = activity;
	}

	@Override
	protected JSONObject doInBackground(String... url) {
		try {
			String response = ParseFacebookUtils.getFacebook().request("/me/friends");
			return Util.parseJson(response);
		} catch (Exception e) {
			Logger.getAnonymousLogger().log(Level.SEVERE, e.getMessage());
		} finally { 
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(JSONObject result) {
		super.onPostExecute(result);
		activity.setupList(result);
		
	}

}
