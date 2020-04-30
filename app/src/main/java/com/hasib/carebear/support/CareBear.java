package com.hasib.carebear.support;

import android.app.Application;
import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.hasib.carebear.R;

public class CareBear extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        if (!FirebaseApp.getApps(this).isEmpty()) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }

        //Initialize FireBase
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setProjectId(getString(R.string.firebase_projectID))
                .setApplicationId(getString(R.string.firebase_applicationID))
                .setApiKey(getString(R.string.firebase_api_key))
                .setDatabaseUrl(getString(R.string.firebase_databaseURL))
                .setStorageBucket(getString(R.string.firebase_storageBucket))
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
