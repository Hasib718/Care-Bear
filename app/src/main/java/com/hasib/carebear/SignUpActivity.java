package com.hasib.carebear;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SignUpActivity";

    private EditText firstNameET, lastNameET, emailET, usernameET, passwordET;
    private Button newSignUpButton;
    private UserDetails userDetails;
    private DatabaseHelper appLocalDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Enable back button on Menu Bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        firstNameET = (EditText) findViewById(R.id.firstNameEditText);
        lastNameET = (EditText) findViewById(R.id.lastNameEditText);
        emailET = (EditText) findViewById(R.id.emailEditText);
        usernameET = (EditText) findViewById(R.id.usernameEditText);
        passwordET = (EditText) findViewById(R.id.passwordEditText);
        newSignUpButton = (Button) findViewById(R.id.newSignUpButton);
        userDetails = new UserDetails();
        appLocalDatabase = new DatabaseHelper(this);

        //Setting Button on click Listener
        newSignUpButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        userDetails.setFirstName(firstNameET.getText().toString());
        userDetails.setLastName(lastNameET.getText().toString());
        userDetails.setEmail(emailET.getText().toString());
        userDetails.setUsername(usernameET.getText().toString());
        userDetails.setPassword(usernameET.getText().toString());

        switch (v.getId()) {
            case R.id.newSignUpButton : {
                long rowId = appLocalDatabase.insertData(userDetails);

                if(rowId < 0) {
                    Log.d(TAG, "onClick: insert unsuccessful");
                } else {
                    Log.d(TAG, "onClick: "+rowId+" is successfully inserted");
                }
            }
        }
    }
}
