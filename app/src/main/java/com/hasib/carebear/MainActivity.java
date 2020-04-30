package com.hasib.carebear;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.hasib.carebear.doctor.authentication.SignInActivityForDoctor;
import com.hasib.carebear.patient.PatientMapActivity;
import com.hasib.carebear.patient.SignInActivityForPatient;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    private Button signUpoRInButtonForDoctor, signUpoRInButtonForPatient;
    private Button emergencyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signUpoRInButtonForDoctor = findViewById(R.id.signInOrUpButtonForDoctorId);
        signUpoRInButtonForPatient = findViewById(R.id.signInOrUpButtonForPatientId);
        emergencyButton = findViewById(R.id.emergencyButtonId);

        getSupportActionBar().hide();

        //Set in onClickListener
        signUpoRInButtonForPatient.setOnClickListener(this);
        signUpoRInButtonForDoctor.setOnClickListener(this);
        emergencyButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signInOrUpButtonForDoctorId: {

                Intent intent1 = new Intent(MainActivity.this, SignInActivityForDoctor.class);
                startActivity(intent1);
            }
            break;

            case R.id.signInOrUpButtonForPatientId: {
                Log.d(TAG, "onClick: tapped");
                Intent intent2 = new Intent(MainActivity.this, SignInActivityForPatient.class);
                startActivity(intent2);
            }
            break;

            case R.id.emergencyButtonId : {
                // TODO: 10-Apr-20 have to implement emergency ambulance service.
            }
            break;
        }
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder alertDialogBuilder;
        alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

        alertDialogBuilder.setIcon(resize(getDrawable(R.drawable.icon_red)));
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

    private Drawable resize(Drawable image) {
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 600, 600, false);
        return new BitmapDrawable(getResources(), bitmapResized);
    }
}

