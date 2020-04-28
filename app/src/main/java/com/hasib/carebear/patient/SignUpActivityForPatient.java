package com.hasib.carebear.patient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.hasib.carebear.R;

public class SignUpActivityForPatient extends AppCompatActivity implements View.OnClickListener {

    private Button signUpButton, signInButton;
    private EditText namePatient, presentAddressPatient, mobileNoPatient, emailPatient,
            passwordPatient;
    private CheckBox maleCheckBox, femaleCheckBox;
    private FirebaseAuth mAuth;

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
                patientRegister();
                break;
        }
    }

    private void patientRegister() {
        String email = emailPatient.getText().toString().trim();
        String password = passwordPatient.getText().toString().trim();

        if(email.isEmpty()){
            emailPatient.setError("enter an email address");
            emailPatient.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailPatient.setError("enter a valid email");
            emailPatient.requestFocus();
            return;
        }

        if(password.isEmpty()){
            passwordPatient.setError("enter an email address");
            passwordPatient.requestFocus();
            return;
        }

        if(password.length()<6){
            passwordPatient.setError("minimum length 6 chars");
            passwordPatient.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "accnount created",Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "SHIT "+ task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }


            }
        });
    }


}

