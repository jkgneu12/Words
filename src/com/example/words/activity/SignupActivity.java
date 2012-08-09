package com.example.words.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.words.R;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.SignUpCallback;

public class SignupActivity extends Activity implements OnClickListener {

	private EditText name;
	private EditText password;
	private EditText email;
	private Button signup;
	private ParseUser currentUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Parse.initialize(this, "VhnMRCE8J0r9fJLuXvGWMQvdNEw6GSxoAQCApqf2", "r4BwcVVLoX7wo92garHMfPa10O6xdmlVIS57ymt8"); 
		
		currentUser = ParseUser.getCurrentUser();
		
		
        if (currentUser != null) {
        	navigate(currentUser.getUsername());
        } else {
		
			setContentView(R.layout.activity_signup);
			
			name = (EditText)findViewById(R.id.name);
			password = (EditText)findViewById(R.id.password);
			email = (EditText)findViewById(R.id.email);
			signup = (Button)findViewById(R.id.signup_button);
			
			signup.setOnClickListener(this);
		}
        
        
		
	}

	private void navigate(String userName) {
		PushService.subscribe(this, "User" + userName.replaceAll("\\s", ""), SignupActivity.class);
		Intent intent = new Intent();
		intent.setClass(this, HomeActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	public void onClick(View button) {
		ParseUser user = new ParseUser();
		user.setUsername(name.getEditableText().toString());
		user.setPassword(password.getEditableText().toString());
		user.setEmail(email.getEditableText().toString());
		
		final String userName = user.getUsername();
		
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
}
