package com.example.words.activity;

import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.example.words.R;
import com.example.words.network.PushManager;
import com.parse.ParseUser;

public class SettingsActivity extends BaseActivity implements OnCheckedChangeListener {

	private CheckBox hideTicker;
	private CheckBox leftHanded;
	private CheckBox chatPush;
	private CheckBox gamePush;
	private ParseUser currentUser;
	private String currentUserName;
	private CheckBox shakeShuffle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_settings);
		
		currentUser = ParseUser.getCurrentUser();
		currentUserName = currentUser.getUsername();

		hideTicker = (CheckBox)findViewById(R.id.hide_ticker);
		hideTicker.setOnCheckedChangeListener(this);
		hideTicker.setChecked(getSharedPreferences("SETTINGS", MODE_PRIVATE).getBoolean("hideTicker", false));

		leftHanded = (CheckBox)findViewById(R.id.left_handed);
		leftHanded.setOnCheckedChangeListener(this);
		leftHanded.setChecked(getSharedPreferences("SETTINGS", MODE_PRIVATE).getBoolean("leftHanded", false));
		
		chatPush = (CheckBox)findViewById(R.id.chat_push);
		chatPush.setOnCheckedChangeListener(this);
		chatPush.setChecked(getSharedPreferences("SETTINGS", MODE_PRIVATE).getBoolean("chatPush", true));
		
		gamePush = (CheckBox)findViewById(R.id.game_push);
		gamePush.setOnCheckedChangeListener(this);
		gamePush.setChecked(getSharedPreferences("SETTINGS", MODE_PRIVATE).getBoolean("gamePush", true));
		
		shakeShuffle = (CheckBox)findViewById(R.id.shake_to_shuffle);
		shakeShuffle.setOnCheckedChangeListener(this);
		shakeShuffle.setChecked(getSharedPreferences("SETTINGS", MODE_PRIVATE).getBoolean("shakeShuffle", true));
	}

	@Override
	public void onCheckedChanged(CompoundButton button, boolean isChecked) {
		if(button == hideTicker){
			Editor editor = getSharedPreferences("SETTINGS", MODE_PRIVATE).edit();
			editor.putBoolean("hideTicker", isChecked);
			editor.commit();
		} else if (button == leftHanded){
			Editor editor = getSharedPreferences("SETTINGS", MODE_PRIVATE).edit();
			editor.putBoolean("leftHanded", isChecked);
			editor.commit();
		} else if (button == chatPush){
			Editor editor = getSharedPreferences("SETTINGS", MODE_PRIVATE).edit();
			editor.putBoolean("chatPush", isChecked);
			editor.commit();
			
			if(isChecked)
				PushManager.pushSubscribeChat(this, currentUserName);
			else
				PushManager.pushUnsubscribeChat(this, currentUserName);
		} else if (button == gamePush){
			Editor editor = getSharedPreferences("SETTINGS", MODE_PRIVATE).edit();
			editor.putBoolean("gamePush", isChecked);
			editor.commit();
			
			if(isChecked)
				PushManager.pushSubscribeGame(this, currentUserName);
			else
				PushManager.pushUnsubscribeGame(this, currentUserName);
		} else if (button == shakeShuffle){
			Editor editor = getSharedPreferences("SETTINGS", MODE_PRIVATE).edit();
			editor.putBoolean("shakeShuffle", isChecked);
			editor.commit();
		} 
	}

}
