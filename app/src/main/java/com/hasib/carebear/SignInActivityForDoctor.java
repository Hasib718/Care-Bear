package com.hasib.carebear;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
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

public class SignInActivityForDoctor extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SignInActivityForDoctor";
    
    private Button signInButton, signUpButtonForDoctor;
    private TextView signUpTextView;
    private EditText emailText;
    private EditText passwordText;
    private UserDetails userDetails;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_for_doctor);
        this.setTitle("Sign In For Doctor");
        Log.d(TAG, "onCreate: Sign in Activity For Doctor");

        //Enable back button on Menu Bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //finding views
        signInButton = (Button) findViewById(R.id.signInButton);
        signUpTextView = (TextView) findViewById(R.id.signUpTextView);
        emailText = (EditText) findViewById(R.id.usernameText);
        passwordText = (EditText) findViewById(R.id.passwordText);
        progressBar = (ProgressBar) findViewById(R.id.progressBarIn);
        signUpButtonForDoctor = (Button) findViewById(R.id.signUpButtonForDoctorId);

         //Firebase
        mAuth = FirebaseAuth.getInstance();

        //Info container class
        userDetails = new UserDetails();

        //Setting button on click listener
        signUpButtonForDoctor.setOnClickListener(this);
        signInButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        userDetails.setUsername(emailText.getText().toString());
        userDetails.setPassword(passwordText.getText().toString());

        switch (v.getId()) {
            case R.id.signInButton : {
                Log.d(TAG, "onClick: Sign In Button Clicked");

                userLogin(userDetails);
            }
            break;

            case R.id.signUpButtonForDoctorId: {
                Log.d(TAG, "onClick: Sign Up Button clicked");

                //Intenting to Sign Up Activity
                Intent intent = new Intent(SignInActivityForDoctor.this,
                        SignUpActivityForDoctor.class);
                startActivity(intent);
            }
            break;
        }
    }

    private void userLogin(UserDetails userDetails) {
        //Checking email validity
        if(userDetails.getEmail().isEmpty()) {
            emailText.setError("Enter an Email Address");
            emailText.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(userDetails.getEmail()).matches()) {
            emailText.setError("Enter a valid email address");
            emailText.requestFocus();
            return;
        }

        //checking the validity of password
        if (userDetails.getPassword().isEmpty()) {
            passwordText.setError("Enter a password");
            passwordText.requestFocus();
            return;
        }
        if (userDetails.getPassword().length() < 8) {
            passwordText.setError("Minimum length of a password should be 8");
            passwordText.requestFocus();
            return;
        }

        //making progressBar visible
        progressBar.setVisibility(View.VISIBLE);

        //Sign in using Firebase
        mAuth.signInWithEmailAndPassword(userDetails.getEmail(), userDetails.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: Log in successful");

                            finish();
                            //Intenting layout...................
                            Intent intent = new Intent(SignInActivityForDoctor.this, ProfileSelectingActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            Log.d(TAG, "onComplete: log in unsuccessful");

                            Toast.makeText(SignInActivityForDoctor.this, "Invaid Login",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
