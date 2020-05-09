package com.hasib.carebear.patient.authentication;

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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hasib.carebear.R;
import com.hasib.carebear.patient.PatientMapActivity;
import com.hasib.carebear.patient.container.PatientUserDetails;
import com.hasib.carebear.support.CareBear;

public class SignUpActivityForPatient extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SignUpActivityForPatient";

    private Button signUpButton;
    private TextView signInText;
    private EditText namePatient, presentAddressPatient, mobileNoPatient, emailPatient,
            passwordPatient;
    private TextInputLayout namePatientLayout, presentAddressPatientLayout, mobileNoPatientLayout,
            emailPatientLayout, passwordPatientLayout;
    private CheckBox maleCheckBox, femaleCheckBox;
    private PatientUserDetails patientUserDetails;
    private ImageButton imageButton;

    //Please Declare Firebase Code
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private FirebaseOptions options = new FirebaseOptions.Builder()
            .setApplicationId("1:875629263565:android:cbca279d8a1354b9974784")
            .setApiKey("AIzaSyCTNXgzMm8mME8EbLRj7IR-ClJtUJR3_i0")
            .setDatabaseUrl("https://project-875629263565.firebaseio.com/")
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_for_patient);
        this.setTitle("Sign Up For Patient");

        getSupportActionBar().hide();

        //Finding EditText for Patient

        //For EditText
        namePatient = findViewById(R.id.patientFullNameId);
        namePatientLayout = findViewById(R.id.patientFullNameLayout);

        presentAddressPatient = findViewById(R.id.patientPresentAddressId);
        presentAddressPatientLayout = findViewById(R.id.patientPresentAddressLayout);

        mobileNoPatient = findViewById(R.id.patientMobileNoTextId);
        mobileNoPatientLayout = findViewById(R.id.patientMobileNoTextLayout);

        emailPatient = findViewById(R.id.idPatientEmail);
        emailPatientLayout = findViewById(R.id.idPatientEmailLayout);

        passwordPatient = findViewById(R.id.idPatientPassword);
        passwordPatientLayout = findViewById(R.id.idPatientPasswordLayout);


        //For Button
        signUpButton = findViewById(R.id.signUpButtonForPatientId);
        signInText = findViewById(R.id.signInTextForPatientId);
        imageButton = findViewById(R.id.backToMainFromPatientSignUp);

        //For CheckBox
        maleCheckBox = findViewById(R.id.idCheckMale);
        femaleCheckBox = findViewById(R.id.idCheckFemale);

        //Firebase
        mAuth = FirebaseAuth.getInstance(CareBear.getPatientFirebaseApp());
        databaseReference = FirebaseDatabase.getInstance(CareBear.getPatientFirebaseApp()).getReference("patient_profile_info");

        signInText.setOnClickListener(this);
        signUpButton.setOnClickListener(this);
        imageButton.setOnClickListener(this);

        patientUserDetails = new PatientUserDetails();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.signInTextForPatientId:
                Intent intent = new Intent(SignUpActivityForPatient.this, SignInActivityForPatient.class);
                startActivity(intent);
                break;

            case R.id.signUpButtonForPatientId:
                if(!getPatientInfo()) {
                return;
                }
                patientRegister();
                break;

            case R.id.backToMainFromPatientSignUp:
                onBackPressed();
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
            namePatientLayout.setError("Name required");
            namePatient.requestFocus();
            return false;
        }
        if (!patientUserDetails.getName().isEmpty()) {
            namePatientLayout.setError(null);
        }
        if(maleCheckBox.isChecked() && femaleCheckBox.isChecked()) {
            maleCheckBox.setError("You can choose only one");
            maleCheckBox.requestFocus();
            return false;
        }
        if(patientUserDetails.getSex()!=false && patientUserDetails.getSex()!=true){
            Toast.makeText(getApplicationContext(),"Must choose Male or Female",Toast.LENGTH_SHORT).show();
        }
        if (patientUserDetails.getMobileNum().isEmpty()) {
            mobileNoPatientLayout.setError("Please Enter Your Mobile NO");
            mobileNoPatient.requestFocus();
            return false;
        }
        if (!patientUserDetails.getMobileNum().isEmpty()) {
            mobileNoPatientLayout.setError(null);
        }
        if (patientUserDetails.getMobileNum().length()<11 ) {
            mobileNoPatientLayout.setError("11 digit mobile number required");
            mobileNoPatient.requestFocus();
            return false;
        }
        if (!(patientUserDetails.getMobileNum().length()<11) ) {
            mobileNoPatientLayout.setError(null);
        }
        if (patientUserDetails.getAddress().isEmpty()) {
            presentAddressPatientLayout.setError("Address required");
            presentAddressPatient.requestFocus();
            return false;
        }
        if (!patientUserDetails.getAddress().isEmpty()) {
            presentAddressPatientLayout.setError(null);
        }
        if (patientUserDetails.getEmail().isEmpty()) {
            presentAddressPatientLayout.setError("Email required");
            presentAddressPatient.requestFocus();
            return false;
        }
        if (!patientUserDetails.getEmail().isEmpty()) {
            presentAddressPatientLayout.setError(null);
        }
        if (patientUserDetails.getPassword().isEmpty()) {
            presentAddressPatientLayout.setError("Password required");
            presentAddressPatient.requestFocus();
            return false;
        }
        if (!patientUserDetails.getPassword().isEmpty()) {
            presentAddressPatient.setError(null);
        }

        return true;
    }


    private void patientRegister() {
        String email = emailPatient.getText().toString().trim();
        String password = passwordPatient.getText().toString().trim();

        if(email.isEmpty()){
            emailPatientLayout.setError("Enter an Email Address");
            emailPatient.requestFocus();
            return;
        }
        if(!email.isEmpty()){
            emailPatientLayout.setError(null);
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailPatientLayout.setError("Enter a valid email");
            emailPatient.requestFocus();
            return;
        }
        if(Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailPatientLayout.setError(null);
        }

        if(password.isEmpty()){
            passwordPatientLayout.setError("Enter Password");
            passwordPatient.requestFocus();
            return;
        }
        if(!password.isEmpty()){
            passwordPatientLayout.setError(null);
        }

        if(password.length()<6){
            passwordPatientLayout.setError("Minimum Length 6 Chars");
            passwordPatient.requestFocus();
            return;
        }
        if(!(password.length()<6)){
            passwordPatient.setError(null);
        }

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Account Created",Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    if(task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(getApplicationContext(), "Email is Already Registered", Toast.LENGTH_SHORT).show();
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
