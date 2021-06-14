package com.hasib.carebear.doctor;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hasib.carebear.R;
import com.hasib.carebear.doctor.container.Doctor;

public class DoctorProfileActivity extends AppCompatActivity {

    private static final String TAG = "DoctorProfileActivity";

    private ImageView profileImage, dMBBS, dFCPS, dFRCS, dMDMS, dMPhil;
    private TextView profileName, specialist, registration, mobile, email, presentAddress;
    private Button editButton;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    private Doctor doctorForPassing;

    private AlertDialog builder;

    private ImageButton imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile);
        this.setTitle("Profile Of Doctor");
        getSupportActionBar().hide();

        initViews();

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("doctors_profile_info");
        databaseReference.keepSynced(true);


        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkConnection()) {
                    Intent intent = new Intent(DoctorProfileActivity.this, DoctorProfileEditActivity.class);
                    intent.putExtra("user", doctorForPassing);
                    startActivity(intent);
                } else {
                    new AlertDialog.Builder(DoctorProfileActivity.this)
                            .setTitle("Caution")
                            .setCancelable(true)
                            .setMessage("You must have a internet connection to update you profile.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .create()
                            .show();
                }
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private boolean checkConnection() {
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
        if (activeNetwork !=null) {
            switch (activeNetwork.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    return true;
                case ConnectivityManager.TYPE_MOBILE: {
                    return true;
                }
                default:
                    return false;
            }
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();

        builder = new AlertDialog.Builder(this)
                .setTitle("Please Wait")
                .setCancelable(false)
                .setView(R.layout.dialog_progress)
                .create();

        builder.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                initInfo();
            }
        }).start();

    }

    private void initViews() {
        editButton = findViewById(R.id.editButtonId);
        profileImage = findViewById(R.id.imageViewId);
        profileName = findViewById(R.id.nameTextViewId);
        specialist = findViewById(R.id.specialistId);
        registration = findViewById(R.id.bmdcRegNoId);
        mobile = findViewById(R.id.mobileText);
        email = findViewById(R.id.emailId);
        presentAddress = findViewById(R.id.presentAddressId);
        imageButton = findViewById(R.id.backToDashBoard);

        dMBBS = findViewById(R.id.degreeImage1);
        dFCPS = findViewById(R.id.degreeImage2);
        dFRCS = findViewById(R.id.degreeImage3);
        dMDMS = findViewById(R.id.degreeImage4);
        dMPhil = findViewById(R.id.degreeImage5);
    }

    private void initInfo() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Doctor doctor = snapshot.getValue(Doctor.class);

                    if (mAuth.getCurrentUser().getEmail().equals(doctor.getEmail())) {
                        doctorForPassing = doctor;

                        Glide.with(DoctorProfileActivity.this)
                                .load(doctor.getDoctorImageUrl())
                                .diskCacheStrategy(DiskCacheStrategy.DATA)
                                .override(600, 600)
                                .into(profileImage);

                        profileName.setText(doctor.getFullName());
                        specialist.setText(doctor.getSpecialist());
                        registration.setText(doctor.getRegistrationInfo());
                        mobile.setText(doctor.getMobile());
                        email.setText(doctor.getEmail());
                        presentAddress.setText(doctor.getPresentAddressInfo());

                        String str = doctor.getCheckBoxInfo();
                        if (str.matches("(.*)MBBS(.*)")) {
                            dMBBS.setImageDrawable(getDrawable(R.drawable.right));
                        }
                        if (str.matches("(.*)FCPS(.*)")) {
                            dFCPS.setImageDrawable(getDrawable(R.drawable.right));
                        }
                        if (str.matches("(.*)FRCS(.*)")) {
                            dFRCS.setImageDrawable(getDrawable(R.drawable.right));
                        }
                        if (str.matches("(.*)MD/MS(.*)")) {
                            dMDMS.setImageDrawable(getDrawable(R.drawable.right));
                        }
                        if (str.matches("(.*)MPhil(.*)")) {
                            dMPhil.setImageDrawable(getDrawable(R.drawable.right));
                        }

                        builder.dismiss();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onBackPressed();
            }
        });
    }
}
