package com.hasib.carebear.support;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class Database extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
