package com.tinvodai.carebear.patient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.tinvodai.carebear.R;

public class SignUpActivityForPatient extends AppCompatActivity implements View.OnClickListener {

    private Button signUpButton, signInButton;
    private EditText namePatient, presentAddressPatient, mobileNoPatient, emailPatient,
            passwordPatient;
    private CheckBox maleCheckBox, femaleCheckBox;

    //Please Declare Firebase Code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_for_patient);
        this.setTitle("Sign Up For Patient");

        //Enable back button on Menu Bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Finding EditText for Patient

        //For EditText
        namePatient = findViewById(R.id.patientFullNameId);
        presentAddressPatient = findViewById(R.id.patientPresentAddressId);
        mobileNoPatient = findViewById(R.id.patientMobileNoTextId);
        emailPatient = findViewById(R.id.idPatientEmail);
        passwordPatient = findViewById(R.id.idPatientPassword);

        //For Button
        signInButton = findViewById(R.id.signInButtonForPatientId);
        signUpButton = findViewById(R.id.signUpButtonForPatientId);

        //For CheckBox
        maleCheckBox = findViewById(R.id.idCheckMale);
        femaleCheckBox = findViewById(R.id.idCheckFemale);

        //Please Initialize FireBase

        signInButton.setOnClickListener(this);
        signUpButton.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.signInButtonForPatientId:
                Intent intent = new Intent(SignUpActivityForPatient.this, SignInActivityForPatient.class);
                startActivity(intent);
                break;

            case R.id.signUpButtonForPatientId:

                break;
        }
    }
}
