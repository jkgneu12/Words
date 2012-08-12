package com.example.words.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.words.Constants;
import com.example.words.R;

public class MainActivity extends Activity implements OnClickListener  {

	private Button play;
	private Button howToPlay;
	private Button review;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Constants.initParse(this); 

		play = (Button)findViewById(R.id.play);
		howToPlay = (Button)findViewById(R.id.how_to_play);
		review = (Button)findViewById(R.id.review);
		
		play.setOnClickListener(this);
		howToPlay.setOnClickListener(this);
		review.setOnClickListener(this);

	}
	
	@Override
	public void onClick(View v) {
		if(v == play)
			play();
		else if(v == howToPlay)
			howToPlay();
		else
			review();
	}

	private void play() {
		Intent intent = new Intent();
		intent.setClass(this, SignupActivity.class);
		startActivity(intent);
	}

	private void howToPlay() {
		Intent intent = new Intent();
		intent.setClass(this, HowToActivity.class);
		startActivity(intent);
	}

	private void review() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(Constants.UPDATE_SITE));
		startActivity(intent);
	}

}
