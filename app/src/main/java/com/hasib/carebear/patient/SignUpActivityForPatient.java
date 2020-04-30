package com.hasib.carebear.patient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hasib.carebear.R;
import com.hasib.carebear.doctor.DoctorDashBoardActivity;
import com.hasib.carebear.doctor.authentication.SignUpActivityForDoctor;

public class SignUpActivityForPatient extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SignUpActivityForPatien";

    private Button signUpButton, signInButton;
    private EditText namePatient, presentAddressPatient, mobileNoPatient, emailPatient,
            passwordPatient;
    private CheckBox maleCheckBox, femaleCheckBox;
    private PatientUserDetails patientUserDetails;

    //Please Declare Firebase Code
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    //Main parent drawer layout
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    //Navigation Menu
    private NavigationView navigationView;
    private View navHeader;


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

        databaseReference = FirebaseDatabase.getInstance().getReference("patient_profile_info");

        //For Button
        signInButton = findViewById(R.id.signInButtonForPatientId);
        signUpButton = findViewById(R.id.signUpButtonForPatientId);

        //For CheckBox
        maleCheckBox = findViewById(R.id.idCheckMale);
        femaleCheckBox = findViewById(R.id.idCheckFemale);

        //Please Initialize FireBase
        mAuth = FirebaseAuth.getInstance();

        signInButton.setOnClickListener(this);
        signUpButton.setOnClickListener(this);

        patientUserDetails = new PatientUserDetails();


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
                if(!getPatientInfo()) {
                return;
                }
                patientRegister();
                break;
        }
    }

    private void savePatientInfo() {
        String key = mAuth.getCurrentUser().getUid();
        databaseReference.child(key).setValue(patientUserDetails)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        finish();

                        Intent intent = new Intent(SignUpActivityForPatient.this, PatientMapActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: on data save failed");
                    }
                });

    }

    private boolean getPatientInfo() {
        patientUserDetails.setName(namePatient.getText().toString());
        patientUserDetails.setAddress(presentAddressPatient.getText().toString());
        patientUserDetails.setMobileNum(mobileNoPatient.getText().toString());
        patientUserDetails.setEmail(emailPatient.getText().toString());
        patientUserDetails.setPassword(passwordPatient.getText().toString());

        if (maleCheckBox.isChecked())patientUserDetails.setSex(true);
        else patientUserDetails.setSex(false);

        if (patientUserDetails.getName().isEmpty()) {
            namePatient.setError("Name required");
            namePatient.requestFocus();
            return false;
        }
        if(maleCheckBox.isChecked() && femaleCheckBox.isChecked()) {
            maleCheckBox.setError("You can choose only one");
            maleCheckBox.requestFocus();
            return false;
        }
        if(patientUserDetails.getSex()!=false && patientUserDetails.getSex()!=true){
            Toast.makeText(getApplicationContext(),"Must choose Male or Female",Toast.LENGTH_SHORT).show();
        }
        if (patientUserDetails.getMobileNum().isEmpty() || patientUserDetails.getMobileNum().length()<11 ) {
            mobileNoPatient.setError("11 digit mobile number required");
            mobileNoPatient.requestFocus();
            return false;
        }
        if (patientUserDetails.getAddress().isEmpty()) {
            presentAddressPatient.setError("Address required");
            presentAddressPatient.requestFocus();
            return false;
        }
        if (patientUserDetails.getEmail().isEmpty()) {
            presentAddressPatient.setError("Valid email required");
            presentAddressPatient.requestFocus();
            return false;
        }
        if (patientUserDetails.getPassword().isEmpty()) {
            presentAddressPatient.setError("Password required");
            presentAddressPatient.requestFocus();
            return false;
        }

        return true;
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

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "accnount created",Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    if(task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(getApplicationContext(), "Email is alredy registered", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }


            }
        }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        savePatientInfo();
                    }
                });
    }
}
