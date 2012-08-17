package com.example.words.network;

import android.util.Log;

import com.example.words.Constants;
import com.parse.ParseException;
import com.parse.ParsePush;
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
}
