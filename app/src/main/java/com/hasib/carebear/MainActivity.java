package com.hasib.carebear;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.hasib.carebear.doctor.authentication.SignInActivityForDoctor;
import com.hasib.carebear.patient.DoctorSearch;
import com.hasib.carebear.patient.authentication.SignInActivityForPatient;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.multi.SnackbarOnAnyDeniedMultiplePermissionsListener;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    private CardView signUpoRInButtonForDoctor, signUpoRInButtonForPatient;
    private CardView emergencyButton, about;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Dexter.withContext(MainActivity.this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        LayoutInflater inflater = getLayoutInflater();
                        final View view = inflater.inflate(R.layout.layout_permission, null);

                        if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                            MultiplePermissionsListener snackbarMultiplePermissionsListener =
                                    SnackbarOnAnyDeniedMultiplePermissionsListener.Builder
                                            .with(view, "Camera and audio access is needed to take pictures of your dog")
                                            .withOpenSettingsButton("Settings")
                                            .withCallback(new Snackbar.Callback() {
                                                @Override
                                                public void onShown(Snackbar snackbar) {
                                                    // Event handler for when the given Snackbar is visible
                                                }

                                                @Override
                                                public void onDismissed(Snackbar snackbar, int event) {
                                                    // Event handler for when the given Snackbar has been dismissed
                                                }
                                            })
                                            .build();

                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                });


        signUpoRInButtonForDoctor = findViewById(R.id.signInOrUpButtonForDoctorId);
        signUpoRInButtonForPatient = findViewById(R.id.signInOrUpButtonForPatientId);
        emergencyButton = findViewById(R.id.emergencyButtonId);
        about = findViewById(R.id.aboutus);

        getSupportActionBar().hide();

        //Set in onClickListener
        signUpoRInButtonForPatient.setOnClickListener(this);
        signUpoRInButtonForDoctor.setOnClickListener(this);
        emergencyButton.setOnClickListener(this);
        about.setOnClickListener(this);



        checkPermissions();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signInOrUpButtonForDoctorId: {
                checkPermissions();

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


            case R.id.emergencyButtonId: {
                startActivity(new Intent(Intent.ACTION_DIAL,Uri.parse("tel:999")));
            }
            break;

            case R.id.aboutus: {


            }
            break;


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

    private void checkPermissions() {
    }

}

