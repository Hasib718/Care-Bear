package com.hasib.carebear.support;

import android.app.Application;
import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.FirebaseDatabase;

public class CareBear extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        if (!FirebaseApp.getApps(this).isEmpty()) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }

        //Initialize FireBase
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setProjectId("care-bear-patient")
                .setApplicationId("1:875629263565:android:cbca279d8a1354b9974784")
                .setApiKey("AIzaSyCTNXgzMm8mME8EbLRj7IR-ClJtUJR3_i0")
                .setDatabaseUrl("https://care-bear-patient.firebaseio.com/")
                //.setStorageBucket("care-bear-patient.appspot.com")
                .build();

        //Firebase instantiate
        try {
            FirebaseApp.initializeApp(this, options, "secondary");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static FirebaseApp getPatientFirebaseApp() {
        return FirebaseApp.getInstance("secondary");
    }
}
