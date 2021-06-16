package com.hasib.carebear.patient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hasib.carebear.R;
import com.hasib.carebear.databinding.ActivityPatientAppointmentBinding;
import com.hasib.carebear.patient.container.Appointment;
import com.hasib.carebear.patient.container.PatientAppointmentAdapter;
import com.hasib.carebear.support.CareBear;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PatientAppointmentActivity extends AppCompatActivity {

    private static final String TAG = PatientAppointmentAdapter.class.getSimpleName();

    ActivityPatientAppointmentBinding binding;

    private FirebaseAuth auth;
    private DatabaseReference patientReference;

    private PatientAppointmentAdapter adapter;
    private List<Appointment> appointments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPatientAppointmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance(CareBear.getPatientFirebaseApp());

        patientReference = FirebaseDatabase.getInstance(CareBear.getPatientFirebaseApp())
                .getReference("patient_profile_info")
                .child(auth.getCurrentUser().getUid());

        adapter = new PatientAppointmentAdapter(this, appointments);
        binding.idPatientAppointment.setAdapter(adapter);
        binding.idPatientAppointment.setLayoutManager(new LinearLayoutManager(this));

        appointments.clear();
        adapter.notifyDataSetChanged();
        patientReference.child("appointments")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Appointment appointment = snapshot.getValue(Appointment.class);
                                Log.d(TAG, "onDataChange: "+appointment.toString());

                                appointments.add(appointment);
                                adapter.notifyDataSetChanged();

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {

                    }
                });

    }
}