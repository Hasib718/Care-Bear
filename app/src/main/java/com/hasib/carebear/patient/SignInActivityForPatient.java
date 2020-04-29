package com.hasib.carebear.patient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.hasib.carebear.R;
import com.hasib.carebear.doctor.container.UserDetails;

public class SignInActivityForPatient extends AppCompatActivity implements View.OnClickListener {

    private Button signInButtonForPatient, signUpButtonForPatient;
    private TextView signUpTextView;
    private EditText emailText;
    private EditText passwordText;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

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

        //Firebase instantiate
        mAuth = FirebaseAuth.getInstance();
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
        if(v.getId() == R.id.signUpButtonForPatientId) {
            Intent intent = new Intent(SignInActivityForPatient.this, SignUpActivityForPatient.class);
            startActivity(intent);
        } else if (v.getId() == R.id.signInButtonForPatientId) {
            patientLogin();
        }
    }

    private void patientLogin() {
        String email = emailText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();

        if(email.isEmpty()){
            emailText.setError("enter an email address");
            emailText.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailText.setError("enter a valid email");
            emailText.requestFocus();
            return;
        }

        if(password.isEmpty()){
            passwordText.setError("enter an email address");
            passwordText.requestFocus();
            return;
        }

        if(password.length()<6){
            passwordText.setError("minimum length 6 chars");
            passwordText.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    // TODO: 4/29/20 add activity for when the patient logs in
                }
                else{
                    Toast.makeText(getApplicationContext(),"Login unsuccessful",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
