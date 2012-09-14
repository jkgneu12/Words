package com.example.words.network;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;

import com.example.words.Utils;
import com.example.words.activity.ChatActivity;
import com.example.words.activity.HomeActivity;
import com.example.words.activity.SignupActivity;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.PushService;
import com.parse.SendCallback;

public class PushManager {

	public static void sendGameUpdatePush(String myName, String opponentUserName, String id) {
		if(opponentUserName == null)
			return;
		
		sendPush("UserGame" + Utils.sanitizeUserName(opponentUserName), "Your turn with " + myName, id);
	}
	
	public static void sendGameOverPush(String myName, String opponentUserName, String endScorePrefix, String id) {
		if(opponentUserName == null)
			return;
		
		sendPush("UserGame" + Utils.sanitizeUserName(opponentUserName), "You " + endScorePrefix + " your game with " + myName, id);
	}
	
	private static void sendPush(String channel, String message, String id){
		ParsePush push = new ParsePush();
		push.setChannel(channel);
		push.setExpirationTimeInterval(86400);
		push.setMessage(message);
		try {
			JSONObject data = new JSONObject();
			data.put("action", "com.example.GAME");
			data.put("id", id);
			push.setData(data);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		push.sendInBackground(new SendCallback() {
			
			@Override
			public void done(ParseException e) {
				if(e != null)
					Log.w("PUSH", e.getMessage());
			}
		});
	}
	
	public static void pushSubscribe(Activity activity, String userName){
		userName = Utils.sanitizeUserName(userName);
		PushService.subscribe(activity, "UserInit" + userName, SignupActivity.class);
		PushService.subscribe(activity, "UserGame" + userName, HomeActivity.class);	
		PushService.subscribe(activity, "UserChat" + userName, ChatActivity.class);
	}
	
	public static void pushSubscribeGame(Activity activity, String userName){
		userName = Utils.sanitizeUserName(userName);
		PushService.subscribe(activity, "UserGame" + userName, HomeActivity.class);	
	}
	
	public static void pushSubscribeChat(Activity activity, String userName){
		userName = Utils.sanitizeUserName(userName);
		PushService.subscribe(activity, "UserChat" + userName, ChatActivity.class);
	}
	
	public static void pushUnsubscribeGame(Activity activity, String userName){
		userName = Utils.sanitizeUserName(userName);
		PushService.unsubscribe(activity, "UserGame" + userName);
	}
	
	public static void pushUnsubscribeChat(Activity activity, String userName){
		userName = Utils.sanitizeUserName(userName);
		PushService.unsubscribe(activity, "UserChat" + userName);
	}
}
