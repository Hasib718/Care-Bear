package com.hasib.carebear.patient.authentication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.hasib.carebear.R;
import com.hasib.carebear.patient.PatientMapActivity;
import com.hasib.carebear.support.CareBear;

public class SignInActivityForPatient extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SignInActivityForPatient";

    private ImageView signInImageForPatient;
    private TextView signUpTextForPatient;
    private EditText emailText, passwordText;
    private TextInputLayout emailTextInputLayout, passwordTextInputLayout;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private ImageButton imageBackButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_for_patient);
        this.setTitle("Sign In For Patient");
        getSupportActionBar().hide();

        //finding views
        signInImageForPatient = findViewById(R.id.signInButtonForPatientId);
        signUpTextForPatient = findViewById(R.id.signUpTextForPatientId);
        progressBar = findViewById(R.id.progressBarIn);
        emailText = findViewById(R.id.usernameTextPatientId);
        passwordText = findViewById(R.id.passwordTextPatientId);
        emailTextInputLayout = findViewById(R.id.usernameTextPatientLayout);
        passwordTextInputLayout = findViewById(R.id.passwordTextPatientLayout);
        imageBackButton = findViewById(R.id.backToMainFromPatient);

        //Setting button on click listener
        signInImageForPatient.setOnClickListener(this);
        signUpTextForPatient.setOnClickListener(this);
        imageBackButton.setOnClickListener(this);

        //Initializing firebase authentication
        mAuth = FirebaseAuth.getInstance(CareBear.getPatientFirebaseApp());
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.signUpTextForPatientId) {
            Intent intent = new Intent(SignInActivityForPatient.this, SignUpActivityForPatient.class);
            startActivity(intent);
        } else if (v.getId() == R.id.signInButtonForPatientId) {
            patientLogin();
        } else if (v.getId() == R.id.backToMainFromPatient) {
            onBackPressed();
        }
    }

    private void patientLogin() {
        String email = emailText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();

        if(email.isEmpty()){
            emailTextInputLayout.setError("Enter an Email Address");
            emailText.requestFocus();
            return;
        }
        if(!email.isEmpty()){
            emailTextInputLayout.setError(null);
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailTextInputLayout.setError("enter a valid email");
            emailText.requestFocus();
            return;
        }
        if(Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailTextInputLayout.setError(null);
        }

        if(password.isEmpty()){
            passwordTextInputLayout.setError("Enter Password");
            passwordText.requestFocus();
            return;
        }

        if(!password.isEmpty()){
            passwordTextInputLayout.setError(null);
        }

        if(password.length()<6){
            passwordTextInputLayout.setError("Minimum Length 6 chars");
            passwordText.requestFocus();
            return;
        }

        if(!(password.length()<6)){
            passwordTextInputLayout.setError(null);
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressBar.setVisibility(View.GONE);

                    finish();
                    startActivity(new Intent(SignInActivityForPatient.this, PatientMapActivity.class));
                }
                else{
                    progressBar.setVisibility(View.GONE);

                    Toast.makeText(getApplicationContext(),"Login unsuccessful",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {
            Log.d(TAG, "onStart: current user: "+mAuth.getCurrentUser().getEmail());

            finish();

            Intent intent = new Intent(SignInActivityForPatient.this, PatientMapActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
}
