package com.hasib.carebear;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class ProfileSelectingActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ProfileSelectingActivit";

    private Button doctorButton;
    private Button patientButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_selecting);
        this.setTitle("Choose Your Profile");

        doctorButton = (Button) findViewById(R.id.doctorButton);
        patientButton = (Button) findViewById(R.id.pstientButton);

        //Setting on click listener
        doctorButton.setOnClickListener(this);
        patientButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.doctorButton : {
                Log.d(TAG, "onClick: Doctor Button Pressed");

            }
            break;

            case R.id.pstientButton : {
                Log.d(TAG, "onClick: Patient Button Pressed");

            }
            break;
        }
    }
}
