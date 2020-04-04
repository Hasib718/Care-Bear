package com.hasib.carebear.patient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hasib.carebear.R;
import com.hasib.carebear.UserDetails;

public class SignInActivityForPatient extends AppCompatActivity implements View.OnClickListener {

    private Button signInButtonForPatient, signUpButtonForPatient;
    private TextView signUpTextView;
    private EditText emailText;
    private EditText passwordText;
    private UserDetails userDetails;
    private ProgressBar progressBar;
    //private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_for_patient);
        this.setTitle("Sign In For Patient");

        //Enable back button on Menu Bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //finding views
        signInButtonForPatient = (Button) findViewById(R.id.signInButtonForPatientId);
        signUpTextView = (TextView) findViewById(R.id.signUpTextView);
        emailText = (EditText) findViewById(R.id.usernameText);
        passwordText = (EditText) findViewById(R.id.passwordText);
        progressBar = (ProgressBar) findViewById(R.id.progressBarIn);
        signUpButtonForPatient = (Button) findViewById(R.id.signUpButtonForPatientId);

        //Setting button on click listener
        signInButtonForPatient.setOnClickListener(this);
        signUpButtonForPatient.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.signUpButtonForPatientId) {
            Intent intent = new Intent(SignInActivityForPatient.this, SignUpActivityForPatient.class);
            startActivity(intent);
        } else if (v.getId() == R.id.signInOrUpButtonForPatientId) {

        }
    }
}
