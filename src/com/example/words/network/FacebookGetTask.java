package com.example.words.network;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.facebook.Util;

public class FacebookGetTask extends AsyncTask<String, Object, JSONObject>{
	
	private ParseUser user;

	public FacebookGetTask(ParseUser user) {
		this.user = user;
	}

	@Override
	protected JSONObject doInBackground(String... url) {
		try {
			String response = ParseFacebookUtils.getFacebook().request(url[0]);
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
		try {
			user.put("displayName", result.get("name"));
			user.saveInBackground();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}

}
