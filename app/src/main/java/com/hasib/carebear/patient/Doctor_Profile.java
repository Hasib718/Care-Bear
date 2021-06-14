package com.hasib.carebear.patient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hasib.carebear.R;
import com.hasib.carebear.databinding.ActivityDoctorProfile2Binding;
import com.hasib.carebear.doctor.container.Doctor;

public class Doctor_Profile extends AppCompatActivity {

    private ActivityDoctorProfile2Binding binding;

    private ImageView backToSearch;
    private TextView doctorName, doctorSpecialized, doctorBMDCNo, doctorEmail, doctorMobile;
    private ImageView mbbsImage, fcpsImage, frcsImage, mdImage, mphilImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDoctorProfile2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        populateUi(getIntent());

        binding.backToSearchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void populateUi(Intent intent) {
        Doctor doctor = intent.getParcelableExtra("doctor_clicked");

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
}