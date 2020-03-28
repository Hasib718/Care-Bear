package com.hasib.carebear;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SignInActivity";
    
    private Button signInButton;
    private Button signUpButton;
    private EditText usernameText;
    private EditText passwordText;
    private DatabaseHelper appLocalDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        Log.d(TAG, "onCreate: Sign in Activity");

        //Enable back button on Menu Bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //finding views
        signInButton = (Button) findViewById(R.id.signInButton);
        signUpButton = (Button) findViewById(R.id.signUpButton);
        usernameText = (EditText) findViewById(R.id.usernameText);
        passwordText = (EditText) findViewById(R.id.passwordText);
        appLocalDatabase = new DatabaseHelper(this);

        //Setting button on click listener
        signUpButton.setOnClickListener(this);
        signInButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();

        switch (v.getId()) {
            case R.id.signInButton : {
                Log.d(TAG, "onClick: Sign In Button Clicked");

                //verifying username and password for signing in
                Boolean result = appLocalDatabase.verifyingSigningIn(username, password);
                if (result) {
                    Toast.makeText(this, "Sign in successful", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Username or password is incorrect", Toast.LENGTH_SHORT).show();
                }
            }
            break;

            case R.id.signUpButton : {
                Log.d(TAG, "onClick: Sign Up Button clicked");

                //Intenting to Sign Up Activity
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
            break;
        }
    }
}
