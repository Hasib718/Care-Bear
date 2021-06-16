package com.hasib.carebear.patient;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hasib.carebear.R;
import com.hasib.carebear.databinding.ActivityDoctorProfile2Binding;
import com.hasib.carebear.doctor.adapter.ChamberRecyclerViewAdapter;
import com.hasib.carebear.doctor.container.Chamber;
import com.hasib.carebear.doctor.container.Doctor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Doctor_Profile extends AppCompatActivity {

    private static final String TAG = Doctor_Profile.class.getSimpleName();

    private ActivityDoctorProfile2Binding binding;
    private final List<Chamber> chambers = new ArrayList<>();
    private Doctor doctor;
    private ChamberRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDoctorProfile2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        doctor = getIntent().getParcelableExtra("doctor_clicked");

        //For Displaying Image in Doctor Profile From Patient;
        CircleImageView doctorImage = findViewById(R.id.doctorImageInPatient);
        Bundle bundle = getIntent().getExtras();
        String docPicUrl1 = bundle.getString("doctor_Image_Uri");
        Glide.with(getApplicationContext())
                .load(docPicUrl1)
                .placeholder(R.drawable.icon_red)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(doctorImage);

        initViews();
        fetchingChamberDatabasekeys(doctor.getId());

        populateUi();

        binding.backToSearchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.doctorCallId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobileNumber = binding.doctorMobileText.getText().toString();
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+mobileNumber)));
            }
        });
    }

    private void initViews() {
        adapter = new ChamberRecyclerViewAdapter(this)
                .setChamberList(chambers)
                .setComingClass(Doctor_Profile.class.getSimpleName())
                .setDoctor(doctor);
        binding.chamberRecyclerView.setAdapter(adapter);
        binding.chamberRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void populateUi() {
        binding.doctorName.setText(doctor.getFullName());
        binding.doctorSpecialist.setText(doctor.getSpecialist());
        binding.doctorBmdcRegNo.setText(doctor.getRegistrationInfo());

        String str = doctor.getCheckBoxInfo();
        if (str.matches("(.*)FCPS(.*)")) {
            binding.cardFCPS.setVisibility(View.VISIBLE);
            binding.fcpsDegreeImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.right));
        }
        if (str.matches("(.*)FRCS(.*)")) {
            binding.cardFRCS.setVisibility(View.VISIBLE);
            binding.frcsDegreeImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.right));
        }
        if (str.matches("(.*)MD/MS(.*)")) {
            binding.secondRowOfDegree.setVisibility(View.VISIBLE);
            binding.cardMD.setVisibility(View.VISIBLE);
            binding.mdDegreeImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.right));
        }
        if (str.matches("(.*)MPhil(.*)")) {
            binding.secondRowOfDegree.setVisibility(View.VISIBLE);
            binding.cardMPhil.setVisibility(View.VISIBLE);
            binding.mphilDegreeImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.right));
        }

        binding.doctorEmail.setText(doctor.getEmail());
        binding.doctorMobileText.setText(doctor.getMobile());
    }

    private void fetchingChamberDatabasekeys(String id) {
        DatabaseReference reference = FirebaseDatabase
                .getInstance()
                .getReference("doctors_profile_info")
                .child(id)
                .child("chamber");
        reference.keepSynced(true);

        adapter.notifyDataSetChanged();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, String> hashMap = (HashMap<String, String>) dataSnapshot.getValue();

                try {
                    Log.d(TAG, "onDataChange: " + hashMap.values().toString());

                    for (Map.Entry<String, String> data : hashMap.entrySet()) {
                        fetchingChamberData(data.getValue());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + databaseError.getMessage());
            }
        });
    }


    private void fetchingChamberData(String s) {
        DatabaseReference reference = FirebaseDatabase
                .getInstance()
                .getReference("doctors_chamber_info")
                .child(s);

        reference.keepSynced(true);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Chamber chamber = dataSnapshot.getValue(Chamber.class);

                Log.d(TAG, "onDataChange: " + chamber.toString());

                chambers.add(chamber);
                adapter.notifyDataSetChanged();

                Log.d(TAG, "onDataChange: chamber " + chamber.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}