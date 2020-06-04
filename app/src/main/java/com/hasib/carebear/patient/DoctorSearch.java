package com.hasib.carebear.patient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hasib.carebear.R;
import com.hasib.carebear.doctor.container.UserDetails;

import java.util.ArrayList;
import java.util.List;

public class DoctorSearch extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DoctorSearchAdapter adapter;
    private List<UserDetails> doctorList;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_search);

        recyclerView=(RecyclerView)findViewById(R.id.idDoctorSearchRecView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        doctorList = new ArrayList<>();
        adapter = new DoctorSearchAdapter(this,doctorList);
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("doctor_profile_info");
    }
}