package com.hasib.carebear;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    private Button signUpoRInButton;
    private Button emergencyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signUpoRInButton = (Button) findViewById(R.id.signInOrUpButton);
        emergencyButton = (Button) findViewById(R.id.emergencyButton);


        //Set in onClickListener
        signUpoRInButton.setOnClickListener(this);
        emergencyButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signInOrUpButton : {
                Log.d(TAG, "onClick: Sign In or Up Button Clicked");

                //Intenting to Sign In Activity
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);
            }
            break;
        }
    }
}
