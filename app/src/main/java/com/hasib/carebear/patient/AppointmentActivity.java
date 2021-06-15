package com.hasib.carebear.patient;

import android.app.DatePickerDialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hasib.carebear.databinding.ActivityAppointmentBinding;
import com.hasib.carebear.doctor.container.Chamber;
import com.hasib.carebear.doctor.container.Doctor;
import com.hasib.carebear.patient.container.Appointment;
import com.hasib.carebear.support.CareBear;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class AppointmentActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = AppointmentActivity.class.getSimpleName();

    private ActivityAppointmentBinding binding;
    private Chamber chamber;
    private Doctor doctor;
    private Calendar calendar;

    private DatabaseReference reference, chamberReference, patientReference;
    private FirebaseAuth auth;

    private long serial;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAppointmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        chamber = getIntent().getParcelableExtra("clicked_chamber");
        doctor = getIntent().getParcelableExtra("clicked_chamber_doctor");
        Log.d(TAG, "onCreate: " + chamber.toString());

        calendar = Calendar.getInstance();

        auth = FirebaseAuth.getInstance(CareBear.getPatientFirebaseApp());
        reference = FirebaseDatabase.getInstance().getReference("appointments");
        chamberReference = FirebaseDatabase.getInstance().getReference("doctors_chamber_info").child(chamber.getChamberDatabaseId()).child("appointments");
        patientReference = FirebaseDatabase.getInstance(CareBear.getPatientFirebaseApp()).getReference("patient_profile_info");

        binding.timepicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        binding.confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAppointment(reference.push().getKey(), chamberReference.push().getKey(), patientReference.push().getKey());
            }
        });
    }

    private void addAppointment(String appointmentsDBKey, String appointmentKeyInChamberDB, String appointmentKeyInPatientDB) {
        DateFormat format = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        Appointment appointment = new Appointment(appointmentsDBKey, appointmentKeyInChamberDB, chamber, doctor, format.format(calendar.getTime()), serial);

        reference.child(date)
                .child(appointmentsDBKey)
                .setValue(appointment)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(AppointmentActivity.this, "Appointment Taken!", Toast.LENGTH_SHORT).show();


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(AppointmentActivity.this, "Task failed!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onFailure: " + e);
                    }
                });

        chamberReference.child(date)
                .child(appointmentKeyInChamberDB)
                .setValue(appointmentsDBKey)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(AppointmentActivity.this, "Appointment Taken!", Toast.LENGTH_SHORT).show();


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(AppointmentActivity.this, "Task failed!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onFailure: " + e);
                    }
                });

        patientReference.child(auth.getCurrentUser().getUid())
                .child("appointments")
                .child(appointmentKeyInPatientDB)
                .setValue(appointment)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(AppointmentActivity.this, "Appointment Taken!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(AppointmentActivity.this, "Task failed!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onFailure: " + e);
                    }
                });

    }

    private void showDatePickerDialog() {
        DatePickerDialog pickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );

        pickerDialog.getDatePicker().setMinDate(calendar.getTime().getTime());
        pickerDialog.getDatePicker().setMaxDate(calendar.getTime().getTime() + 51_84_00_000L);
        pickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        date = String.format("%s-%s-%s", dayOfMonth, month, year);
        binding.setDate.setText(date);

        checkSerialCount(date);
    }

    private void checkSerialCount(String date) {
        chamberReference.child(date).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    updateCount(dataSnapshot.getChildrenCount());
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {

            }
        });
    }

    private void updateCount(long childrenCount) {
        if (childrenCount >= 40) {
            Toast.makeText(this, "Appointment Slot is Full. Choose another date.", Toast.LENGTH_SHORT).show();
        } else {
            binding.currentSerial.setText(String.valueOf(childrenCount));
            binding.yourSerial.setText(String.valueOf(childrenCount + 1));
            binding.confirm.setClickable(true);

            serial = childrenCount;
        }
    }
}