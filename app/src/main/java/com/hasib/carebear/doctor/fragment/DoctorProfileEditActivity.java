package com.hasib.carebear.doctor.fragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.hasib.carebear.R;

public class DoctorProfileEditActivity extends AppCompatActivity {

    //declaring editText....
    private EditText doctorNameEditText, specialistEditText, bmdcRegNoEditText, presentAddressEditText,
            commonChamberEditText, mobileNoEditText, emailEditText, passwordEditText;

    private ImageView doctorImageView;
    private CheckBox checkMBBS, checkFCPS, checkMPhil, checkMDorMS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile_edit);
        this.setTitle("Edit your Profile");

        //Enable back button on Menu Bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //initializing editText by finding Id....
        doctorNameEditText = (EditText) findViewById(R.id.doctorNameEditTextId);
        specialistEditText = (EditText) findViewById(R.id.specialistEditTextId);
        bmdcRegNoEditText = (EditText) findViewById(R.id.regNoOfBMDCEditTextId);
        presentAddressEditText = (EditText) findViewById(R.id.presentAddressEditTextId);
        commonChamberEditText = (EditText) findViewById(R.id.commonChamberEditTextId);
        mobileNoEditText = (EditText) findViewById(R.id.mobileNoEditTextId);
        emailEditText = (EditText) findViewById(R.id.emailEditTextId);
        passwordEditText = (EditText) findViewById(R.id.passwordEditTextId);

        doctorImageView = (ImageView) findViewById(R.id.doctorImageViewId);

        checkMBBS = findViewById(R.id.checkMBBSId);
        checkFCPS = findViewById(R.id.checkFCPSId);
        checkMPhil = findViewById(R.id.checkMPhilId);
        checkMDorMS = findViewById(R.id.checkMDId);
    }
    //This Function is needed for back button.. Without this function
    //back button wouldn't work properly..
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
