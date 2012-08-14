package com.example.words.activity;

import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.example.words.R;

public class SettingsActivity extends BaseActivity implements OnCheckedChangeListener {

	private CheckBox hideTicker;
	private CheckBox leftHanded;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_settings);

		hideTicker = (CheckBox)findViewById(R.id.hide_ticker);
		hideTicker.setOnCheckedChangeListener(this);
		hideTicker.setChecked(getSharedPreferences("SETTINGS", MODE_PRIVATE).getBoolean("hideTicker", false));

		leftHanded = (CheckBox)findViewById(R.id.left_handed);
		leftHanded.setOnCheckedChangeListener(this);
		leftHanded.setChecked(getSharedPreferences("SETTINGS", MODE_PRIVATE).getBoolean("leftHanded", false));
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
		}
	}

}
