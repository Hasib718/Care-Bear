package com.hasib.carebear.patient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hasib.carebear.R;

public class SignInActivityForPatient extends AppCompatActivity implements View.OnClickListener {

    private ImageView signInButtonForPatient;
    private TextView signUpTextForPatient;
    private EditText emailText, passwordText;
    private ProgressBar progressBar;
    private ImageButton back;
    //private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_for_patient);
        this.setTitle("Sign In For Patient");

        //Enable back button on Menu Bar
        getSupportActionBar().hide();

        //finding views
        signInButtonForPatient = (ImageView) findViewById(R.id.signInButtonForPatientId);
        signUpTextForPatient = (TextView) findViewById(R.id.signUpTextForPatientId);
        progressBar = (ProgressBar) findViewById(R.id.progressBarIn);
        back = findViewById(R.id.backToMainFromPatient);
        emailText = (EditText) findViewById(R.id.usernameText);
        passwordText = (EditText) findViewById(R.id.passwordText);

        //Setting button on click listener
        signInButtonForPatient.setOnClickListener(this);
        signUpTextForPatient.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.signUpTextForPatientId) {
            Intent intent = new Intent(SignInActivityForPatient.this, SignUpActivityForPatient.class);
            startActivity(intent);
        }
        if (v.getId() == R.id.signInButtonForPatientId) {

        }
        if (v.getId() == R.id.backToMainFromPatient) {
            onBackPressed();
        }
    }
}
