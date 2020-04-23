package com.hasib.carebear;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hasib.carebear.doctor.container.Chamber;

import java.util.ArrayList;
import java.util.List;

public class SplashScreenActivity extends AppCompatActivity {

    private static final String TAG = "SplashScreenActivity";

    private ProgressBar progressBar;

    private List<String> chamberKeys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //For hiding title Screen....
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //For Hiding Actionbar...
        getSupportActionBar().hide();

        setContentView(R.layout.activity_splash_screen);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        FirebaseDatabase
                .getInstance()
                .getReference("doctors_profile_info")
                .child(mAuth.getCurrentUser().getUid())
                .child("chamber")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        chamberKeys = (ArrayList) dataSnapshot.getValue();

                        Log.d(TAG, "onDataChange: "+chamberKeys.toString());

                        testing(chamberKeys.get(0));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

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

    private void testing(String s) {
        FirebaseDatabase
                .getInstance()
                .getReference("doctors_chamber_info")
                .child(s)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Chamber chamber = (Chamber) dataSnapshot.getValue();

                        Log.d(TAG, "onDataChange: "+chamber.toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public void doWork() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        Log.d(TAG, "doWork: "+mAuth.getCurrentUser().getEmail());

        int progress;
        for(progress = 1; progress <=100; progress += 1) {
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
        startActivity(intent);
        finish();
    }
}
