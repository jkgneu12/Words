package com.example.words.network;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.words.AppController;

public class PushReceiver extends BroadcastReceiver {
	private static final String TAG = "MyCustomReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			String action = intent.getAction();
			String channel = intent.getExtras().getString("com.parse.Channel");
			JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));

			String id = json.getString("id");

			if(action.equals(PushManager.GAME_ACTION))
				((AppController)context.getApplicationContext()).addGameToRefresh(id);
			else if(action.equals(PushManager.CHAT_ACTION))
				((AppController)context.getApplicationContext()).refreshChat(id);

		} catch (JSONException e) {
			Log.d(TAG, "JSONException: " + e.getMessage());
		}
	}
}
