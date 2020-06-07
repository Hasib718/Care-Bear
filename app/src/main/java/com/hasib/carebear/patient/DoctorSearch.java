package com.hasib.carebear.patient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hasib.carebear.R;
import com.hasib.carebear.doctor.container.UserDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

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

        databaseReference = FirebaseDatabase.getInstance().getReference("doctors_profile_info");


        Query query = FirebaseDatabase.getInstance().getReference("doctors_profile_info")
                .orderByChild("id").equalTo("0emALksmxsTCTFKRef70qtvRpXr2");

        query.addValueEventListener(valueEventListener);
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            doctorList.clear();
            if(dataSnapshot.exists()){
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    UserDetails userDetails = snapshot.getValue(UserDetails.class);
                    doctorList.add(userDetails);
                }
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
}