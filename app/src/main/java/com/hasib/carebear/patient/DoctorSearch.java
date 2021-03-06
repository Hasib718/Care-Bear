package com.hasib.carebear.patient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.SearchView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hasib.carebear.R;
import com.hasib.carebear.doctor.container.Doctor;

import java.util.ArrayList;
import java.util.List;

public class DoctorSearch extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DoctorSearchAdapter adapter;
    private List<Doctor> doctorList;
    private SearchView searchView;
    private String searchViewStr;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_search);

        recyclerView= findViewById(R.id.idDoctorSearchRecView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        doctorList = new ArrayList<>();
        adapter = new DoctorSearchAdapter(this,doctorList);
        recyclerView.setAdapter(adapter);
        searchView = findViewById(R.id.idDoctorSearchBar);


        databaseReference = FirebaseDatabase.getInstance().getReference("doctors_profile_info");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                searchViewStr = searchView.getQuery().toString();

                Query query = FirebaseDatabase.getInstance().getReference("doctors_profile_info")
                        .orderByChild("fullName").equalTo(searchViewStr);

                query.addValueEventListener(valueEventListener);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

    }
    

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            doctorList.clear();
            if(dataSnapshot.exists()){
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Doctor doctor = snapshot.getValue(Doctor.class);
                    doctorList.add(doctor);
                }
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
}