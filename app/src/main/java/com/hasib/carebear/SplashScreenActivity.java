package com.hasib.carebear;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;

public class SplashScreenActivity extends AppCompatActivity {

    private static final String TAG = "SplashScreenActivity";

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //For hiding title Screen....
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //For Hiding Actionbar...
        getSupportActionBar().hide();

        setContentView(R.layout.activity_splash_screen);

        //finding....
        progressBar = findViewById(R.id.progressBarId);

        //Progress bar will work by thread
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                doWork();
                startApp();
            }
        });

        thread.start();
    }

    public void doWork() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        try {
            Log.d(TAG, "doWork: "+mAuth.getCurrentUser().getEmail());
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "doWork: user not signed in");
        }

        for(int progress = 1; progress <=100; progress++) {
            try {
                Thread.sleep(20);
                progressBar.setProgress(progress);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void startApp() {
        Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}