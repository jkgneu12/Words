package com.example.words.network;

import android.app.Activity;
import android.util.Log;

import com.example.words.Constants;
import com.example.words.activity.ChatActivity;
import com.example.words.activity.HomeActivity;
import com.example.words.activity.SignupActivity;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.PushService;
import com.parse.SendCallback;

public class PushManager {

	public static void sendGameUpdatePush(String myName, String opponentUserName) {
		if(opponentUserName == null)
			return;
		
		sendPush("UserGame" + Constants.sanitizeUserName(opponentUserName), "Your turn with " + myName);
	}
	
	public static void sendGameOverPush(String myName, String opponentUserName, String endScorePrefix) {
		if(opponentUserName == null)
			return;
		
		sendPush("UserGame" + Constants.sanitizeUserName(opponentUserName), "You " + endScorePrefix + " your game with " + myName);
	}
	
	private static void sendPush(String channel, String message){
		ParsePush push = new ParsePush();
		push.setChannel(channel);
		push.setExpirationTimeInterval(86400);
		push.setMessage(message);
		push.sendInBackground(new SendCallback() {
			
			@Override
			public void done(ParseException e) {
				if(e != null)
					Log.w("PUSH", e.getMessage());
			}
		});
	}
	
	public static void pushSubscribe(Activity activity, String userName){
		userName = Constants.sanitizeUserName(userName);
		PushService.subscribe(activity, "UserInit" + userName, SignupActivity.class);
		PushService.subscribe(activity, "UserGame" + userName, HomeActivity.class);	
		PushService.subscribe(activity, "UserChat" + userName, ChatActivity.class);
	}
	
	public static void pushSubscribeGame(Activity activity, String userName){
		userName = Constants.sanitizeUserName(userName);
		PushService.subscribe(activity, "UserGame" + userName, HomeActivity.class);	
	}
	
	public static void pushSubscribeChat(Activity activity, String userName){
		userName = Constants.sanitizeUserName(userName);
		PushService.subscribe(activity, "UserChat" + userName, ChatActivity.class);
	}
	
	public static void pushUnsubscribeGame(Activity activity, String userName){
		userName = Constants.sanitizeUserName(userName);
		PushService.unsubscribe(activity, "UserGame" + userName);
	}
	
	public static void pushUnsubscribeChat(Activity activity, String userName){
		userName = Constants.sanitizeUserName(userName);
		PushService.unsubscribe(activity, "UserChat" + userName);
	}
}
