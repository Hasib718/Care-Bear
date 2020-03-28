package com.hasib.carebear;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";

    //fields for Database Tables COMMON attribute
    private static final String DATABASE_NAME = "usersdetails.db";
    private static final String LOCAL_TABLE_NAME = "usersdetails";
    private static final String ID = "_id";
    private static final String FIRST_NAME = "First_Name";
    private static final String LAST_NAME = "Last_Name";
    private static final String EMAIL = "Email";
    private static final String USERNAME = "Username";
    private static final String PASSWORD = "Password";
    private static final int VERSION_NUMBER = 1;

    //fields for DOCTORS information
    private static final String DOCTOR_TABLE_NAME = "doctordetails";

    //fields for PATIENTS information
    private static final String PATIENT_TABLE_NAME = "patientdetails";

    //Database Control COMMAND
    private static final String CREATE_LOCAL_TABLE = "CREATE TABLE "+LOCAL_TABLE_NAME+" ("+ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                                                        +FIRST_NAME+" VARCHAR(255) NOT NULL, "+LAST_NAME+" VARCHAR(255) NOT NULL, "
                                                        +EMAIL+" TEXT NOT NULL, "+USERNAME+" TEXT NOT NULL, "+PASSWORD+" TEXT NOT NULL)";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS "+LOCAL_TABLE_NAME;

    private Context mContext;


    //Constructor for this helper class
    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION_NUMBER);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_LOCAL_TABLE);

            Log.d(TAG, "onCreate: Database Created");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            Log.d(TAG, "onUpgrade: is called");

            db.execSQL(DROP_TABLE);
            onCreate(db);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public long insertData(UserDetails userDetails) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(FIRST_NAME, userDetails.getFirstName());
        contentValues.put(LAST_NAME, userDetails.getLastName());
        contentValues.put(EMAIL, userDetails.getEmail());
        contentValues.put(USERNAME, userDetails.getUsername());
        contentValues.put(PASSWORD, userDetails.getPassword());

        long rowId = db.insert(LOCAL_TABLE_NAME, null, contentValues);
        return rowId;
    }

    public Boolean verifyingSigningIn(String us, String pw) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+LOCAL_TABLE_NAME, null);

        if (cursor.getCount() == 0) {
            Log.d(TAG, "verifyingSigningIn: No Data Found");
        } else {
            while(cursor.moveToNext()) {
                String username = cursor.getString(4);
                String password = cursor.getString(5);

                if (us.equals(username) && us.equals(password)) {
                    Log.d(TAG, "verifyingSigningIn: verified "+
                            "Username: "+username+
                            "Password: "+password);

                    return true;
                }
            }
        }

        return false;
    }
}
