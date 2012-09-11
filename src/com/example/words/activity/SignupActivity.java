package com.example.words.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.words.R;
import com.example.words.Utils;
import com.example.words.network.FacebookNameGetTask;
import com.example.words.network.PushManager;
import com.example.words.network.TwitterGetTask;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignupActivity extends BaseActivity implements OnClickListener {

	private EditText name;
	private EditText password;
	private Button signup;
	private Button login;

	private ParseUser currentUser;
	private Button facebook;
	private Button twitter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Utils.initParse(this); 

		currentUser = ParseUser.getCurrentUser();


		if (currentUser != null) {
			navigate(currentUser.getUsername());
		} else {

			setContentView(R.layout.activity_signup);

			name = (EditText)findViewById(R.id.name);
			password = (EditText)findViewById(R.id.password);
			signup = (Button)findViewById(R.id.signup_button);
			login = (Button)findViewById(R.id.login_button);
			facebook = (Button)findViewById(R.id.facebook);
			twitter = (Button)findViewById(R.id.twitter);

			signup.setOnClickListener(this);
			login.setOnClickListener(this);
			facebook.setOnClickListener(this);
			twitter.setOnClickListener(this);
		}



	}

	private void navigate(String userName) {
		PushManager.pushSubscribe(this, userName);
		Intent intent = new Intent();
		intent.setClass(this, HomeActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	public void onClick(View button) {
		if(button == signup)
			signup();
		else if (button == login)
			login();
		else if (button == facebook)
			facebook();
		else
			twitter();
	}

	private void signup(){
		final String userName = name.getEditableText().toString();
		String passwordString = password.getEditableText().toString();


		if(Utils.isNullOrEmpty(userName)){
			Toast.makeText(SignupActivity.this, "Enter a User Name", Toast.LENGTH_LONG).show();
			return;
		}

		if(Utils.isNullOrEmpty(passwordString)){
			Toast.makeText(SignupActivity.this, "Enter a Password", Toast.LENGTH_LONG).show();
			return;
		}

		ParseUser user = new ParseUser();
		user.setUsername(userName);
		user.setPassword(passwordString);
		user.put("displayName", userName);

		user.signUpInBackground(new SignUpCallback() {
			public void done(ParseException e) {
				if (e == null) {
					navigate(userName);
				} else {
					Toast.makeText(SignupActivity.this, "Signup Failed " + e.getMessage(), Toast.LENGTH_LONG).show();
				} 
			}
		});
	}

	private void login(){
		String userName = name.getEditableText().toString();
		String passwordString = password.getEditableText().toString();


		if(Utils.isNullOrEmpty(userName)){
			Toast.makeText(SignupActivity.this, "Enter a User Name", Toast.LENGTH_LONG).show();
			return;
		}

		if(Utils.isNullOrEmpty(passwordString)){
			Toast.makeText(SignupActivity.this, "Enter a Password", Toast.LENGTH_LONG).show();
			return;
		}

		ParseUser.logInInBackground(userName, passwordString, new LogInCallback() {

			@Override
			public void done(ParseUser user, ParseException e) {
				if(e == null){
					currentUser = user;
					navigate(currentUser.getUsername());
				} else 
					Toast.makeText(SignupActivity.this, "Could not login " + e.getMessage(), Toast.LENGTH_LONG).show();
			}
		});
	}

	private void facebook() {
		ParseFacebookUtils.logIn(this, new LogInCallback() {
			@Override
			public void done(ParseUser user, ParseException e) {
				if (user != null) {
					if (ParseFacebookUtils.isLinked(user)) {
						new FacebookNameGetTask(user).execute();
						currentUser = user;
						navigate(currentUser.getUsername());

					} else {
						Toast.makeText(SignupActivity.this, "Facebook Linking Failed " + e.getMessage(), Toast.LENGTH_LONG).show();
					}
				} else {
					Toast.makeText(SignupActivity.this, "Facebook Linking Failed " + e.getMessage(), Toast.LENGTH_LONG).show();
				}
			}
		});
	}
	
	private void twitter() {
		ParseTwitterUtils.logIn(this, new LogInCallback() {
			@Override
			public void done(ParseUser user, ParseException e) {
				if (user != null) {
					if (ParseTwitterUtils.isLinked(user)) {
						new TwitterGetTask(user).execute();
						currentUser = user;
						navigate(currentUser.getUsername());

					} else {
						Toast.makeText(SignupActivity.this, "Twitter Linking Failed " + e.getMessage(), Toast.LENGTH_LONG).show();
					}
				} else {
					Toast.makeText(SignupActivity.this, "Twitter Linking Failed " + e.getMessage(), Toast.LENGTH_LONG).show();
				}
			}
		});
	}
}
