package com.hasib.carebear.patient;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hasib.carebear.R;

public class Doctor_Profile extends AppCompatActivity {
    private ImageView backToSearch;
    private TextView doctorName, doctorSpecialized, doctorBMDCNo, doctorEmail, doctorMobile;
    private ImageView mbbsImage, fcpsImage, frcsImage, mdImage, mphilImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile2);

        backToSearch = findViewById(R.id.backToSearchBar);
        doctorName = findViewById(R.id.doctorName);
        doctorSpecialized = findViewById(R.id.doctorSpecialist);
        doctorBMDCNo = findViewById(R.id.doctorBmdcRegNo);

        mbbsImage = findViewById(R.id.mbbsDegreeImage);
        fcpsImage = findViewById(R.id.fcpsDegreeImage);
        frcsImage = findViewById(R.id.frcsDegreeImage);
        mdImage = findViewById(R.id.mdDegreeImage);
        mphilImage = findViewById(R.id.mphilDegreeImage);

        doctorEmail = findViewById(R.id.doctorEmail);
        doctorMobile = findViewById(R.id.doctorMobileText);

        backToSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}