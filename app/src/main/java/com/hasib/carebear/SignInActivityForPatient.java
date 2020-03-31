package com.hasib.carebear;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class SignInActivityForPatient extends AppCompatActivity implements View.OnClickListener {

    private Button signInButtonForPatient, signUpButtonForPatient;
    private TextView signUpTextView;
    private EditText emailText;
    private EditText passwordText;
    private UserDetails userDetails;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_for_patient);
        this.setTitle("Sign In For Patient");

        //Enable back button on Menu Bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //finding views
        signInButtonForPatient = (Button) findViewById(R.id.signInButton);
        signUpTextView = (TextView) findViewById(R.id.signUpTextView);
        emailText = (EditText) findViewById(R.id.usernameText);
        passwordText = (EditText) findViewById(R.id.passwordText);
        progressBar = (ProgressBar) findViewById(R.id.progressBarIn);
        signUpButtonForPatient = (Button) findViewById(R.id.signUpButtonForDoctorId);

        //Setting button on click listener
        signInButtonForPatient.setOnClickListener(this);
        signUpButtonForPatient.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.signUpButtonForDoctorId) {
            Intent intent = new Intent(SignInActivityForPatient.this, SignInActivityForPatient.class);
            startActivity(intent);
        }
    }
}
