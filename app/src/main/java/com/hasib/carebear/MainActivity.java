package com.hasib.carebear;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    private Button signUpoRInButtonForDoctor, signUpoRInButtonForPatient;
    private Button emergencyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signUpoRInButtonForDoctor = (Button) findViewById(R.id.signInOrUpButtonForDoctorId);
        signUpoRInButtonForPatient = (Button) findViewById(R.id.signInOrUpButtonForPatientId);
        emergencyButton = (Button) findViewById(R.id.emergencyButtonId);


        //Set in onClickListener
        signUpoRInButtonForPatient.setOnClickListener(this);
        signUpoRInButtonForDoctor.setOnClickListener(this);
        emergencyButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.signInOrUpButtonForDoctorId) {
            Intent intent1 = new Intent(MainActivity.this, SignInActivityForDoctor.class);
            startActivity(intent1);
        }
        if(v.getId() == R.id.signInOrUpButtonForPatientId) {
            Intent intent2 = new Intent(MainActivity.this, SignInActivityForPatient.class);
            startActivity((intent2));
        }

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder;
        alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

        alertDialogBuilder.setIcon(R.drawable.bear16px);
        alertDialogBuilder.setTitle(R.string.alertTitle);
        alertDialogBuilder.setMessage(R.string.alertMassage);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}

