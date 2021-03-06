package com.example.words.network;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.example.words.activity.GameFragment;

public class ValidateTask extends AsyncTask<String, Object, JSONObject>{

	private ProgressDialog waiting;
	private GameFragment fragment;

	public ValidateTask(GameFragment fragment, ProgressDialog waiting) {
		this.fragment = fragment;
		this.waiting = waiting;
	}
	
	@Override
	protected JSONObject doInBackground(String... url) {
		try {
			HttpClient client = new DefaultHttpClient();
	        HttpGet request = new HttpGet(); 
	        request.setURI(new URI(url[0]));
	        String responseBody = client.execute(request, new BasicResponseHandler());
	        return new JSONObject(responseBody);
		} catch (Exception e) {
			Logger.getAnonymousLogger().log(Level.SEVERE, e.getMessage());
		} finally { 
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(JSONObject result) {
		super.onPostExecute(result);
		if(waiting != null && waiting.isShowing())
			waiting.dismiss();
		fragment.validated(result);
	}

}
