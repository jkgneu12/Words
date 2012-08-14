package com.example.words.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.words.Constants;
import com.example.words.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseQuery.CachePolicy;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SendCallback;

public class ChatActivity extends BaseActivity implements OnClickListener {
	
	private LinearLayout chatWindow;
	private EditText input;
	private Button submit;
	private ParseUser currentUser;
	private String userId;
	private String opponentId;
	private String opponentName;
	private ScrollView chatWindowScroll;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_chat); 
		
		Constants.initParse(this); 
		
		chatWindowScroll = (ScrollView)findViewById(R.id.chat_window_scroll);
		chatWindow = (LinearLayout)findViewById(R.id.chat_window);
		input = (EditText)findViewById(R.id.input);
		submit = (Button)findViewById(R.id.submit);
		
		submit.setOnClickListener(this);
		
		currentUser = ParseUser.getCurrentUser();
		userId = currentUser.getObjectId();
		
		opponentId = getIntent().getExtras().getString("receivingUser");
		opponentName = getIntent().getExtras().getString("receivingUserName");
		
		populateChatWindow();
	}

	public void populateChatWindow() {
		ParseQuery query1 = new ParseQuery("Chat");
		query1.whereEqualTo("receivingUser", opponentId);
		query1.whereEqualTo("sendingUser", userId);
		
		ParseQuery query2 = new ParseQuery("Chat");
		query2.whereEqualTo("receivingUser", userId);
		query2.whereEqualTo("sendingUser", opponentId);
		
		List<ParseQuery> queries = new ArrayList<ParseQuery>();
		queries.add(query1);
		queries.add(query2);
		
		ParseQuery query = ParseQuery.or(queries);
		query.orderByAscending("createdAt");
		query.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK);
		query.findInBackground(new FindCallback() {
			@Override
			public void done(List<ParseObject> objs, ParseException e) {
				if(e == null){
					chatWindow.removeAllViews();
					for(ParseObject obj : objs){
						addText(obj.getString("text"), obj.getString("sendingUser").equals(userId));
					}
					scrollToBottom();
				}
			}
		});
	}
	
	protected void addText(String text, boolean thisUser) {
		RelativeLayout bubble = new RelativeLayout(ChatActivity.this);
		bubble.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		bubble.setPadding(25, 5, 25, 5);
		
		TextView bubbleText = new TextView(ChatActivity.this);
		bubbleText.setText(text);
		bubbleText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		if(thisUser){
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		} else {
			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		}
		
		bubbleText.setLayoutParams(params);
		
		bubble.addView(bubbleText);
		chatWindow.addView(bubble);
	}

	@Override
	public void onClick(View button) {
		final String text = input.getText().toString();
		
		if(text == null || text.length() <= 0)
			return;
		
		ParseObject parseObject = new ParseObject("Chat");
		parseObject.put("sendingUser", userId);
		parseObject.put("receivingUser", opponentId);
		parseObject.put("text", text);
		parseObject.saveInBackground(new SaveCallback() {
			
			@Override
			public void done(ParseException e) {
				if(e != null){
					Log.e("Parse", "Could not send message");
					Toast.makeText(ChatActivity.this, "Could not send message", Toast.LENGTH_LONG).show();
				} else {
					addText(text, true);
					scrollToBottom();
					
					sendPush(text);
				}
			}
			
		});
		
		input.setText("");
	}

	public void scrollToBottom() {
		chatWindowScroll.post(new Runnable() {
		    @Override
		    public void run() {
		    	chatWindowScroll.fullScroll(ScrollView.FOCUS_DOWN);
		    }
		});
	}
	
	public void sendPush(final String text) {
		try {
			ParsePush push = new ParsePush();
			push.setChannel("UserChat" + Constants.sanitizeUserName(opponentName));
			push.setExpirationTimeInterval(86400);
			
			JSONObject json = new JSONObject();
			json.put("title", currentUser.getString("displayName") + " sent you a message");
			json.put("alert", text);
			json.put("action", "com.example.words.activity.ChatActivity");
			json.put("opponentId", currentUser.getObjectId());
			json.put("opponentName", currentUser.getString("displayName"));
			
			push.setData(json);
			
			push.sendInBackground(new SendCallback() {
				
				@Override
				public void done(ParseException e) {
					if(e != null)
						Log.w("PUSH", e.getMessage());
				}
			});
		
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	

}
