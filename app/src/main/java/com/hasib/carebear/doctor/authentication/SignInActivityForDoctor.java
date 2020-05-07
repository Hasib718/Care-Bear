package com.hasib.carebear.doctor.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.hasib.carebear.R;
import com.hasib.carebear.doctor.DoctorDashBoardActivity;
import com.hasib.carebear.doctor.container.UserDetails;

public class SignInActivityForDoctor extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SignInActivityForDoctor";

    private TextInputLayout emailTextLayout, passwordTextLayout;
    private EditText emailText, passwordText;
    private TextView signUpText;
    private ImageButton imageButton;
    private Button signInButton;
    private UserDetails userDetails;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_for_doctor);
        getSupportActionBar().hide();

        emailTextLayout = findViewById(R.id.usernameTextLayout);
        passwordTextLayout = findViewById(R.id.passwordTextLayout);
        emailText = findViewById(R.id.usernameText);
        passwordText = findViewById(R.id.passwordText);
        signUpText = findViewById(R.id.signUpButtonForDoctorId);
        imageButton = findViewById(R.id.backToMain);
        signInButton = findViewById(R.id.signInButtonForDoctorId);
        progressBar = findViewById(R.id.progressBarIn);

        //Firebase
        mAuth = FirebaseAuth.getInstance();

        //Info container class
        userDetails = new UserDetails();

        signInButton.setOnClickListener(this);
        signUpText.setOnClickListener(this);
        imageButton.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {
            Log.d(TAG, "onStart: current user: "+mAuth.getCurrentUser().getEmail());

            finish();

            Intent intent = new Intent(SignInActivityForDoctor.this, DoctorDashBoardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
        userDetails.setEmail(emailText.getText().toString());
        userDetails.setPassword(passwordText.getText().toString());

        if (v.getId() == R.id.signUpButtonForDoctorId) {
            Log.d(TAG, "onClick: Sign Up Button clicked");

            //Intenting to Sign Up Activity
            Intent intent = new Intent(SignInActivityForDoctor.this, SignUpActivityForDoctor.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        if (v.getId() == R.id.signInButtonForDoctorId) {
            Log.d(TAG, "onClick: Sign In Button Clicked");

            userLogin(userDetails);
        }

        if(v.getId() == R.id.backToMain) {
            onBackPressed();
        }
    }

    private void userLogin(UserDetails userDetails) {
        //Checking email validity
        if(userDetails.getEmail().isEmpty()) {
            emailTextLayout.setError("Enter an Email Address");
            emailText.requestFocus();
            return;
        }
        if(!userDetails.getEmail().isEmpty()) {
            emailTextLayout.setError(null);
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(userDetails.getEmail()).matches()) {
            emailTextLayout.setError("Enter a valid email address");
            emailText.requestFocus();
            return;
        }
        if (Patterns.EMAIL_ADDRESS.matcher(userDetails.getEmail()).matches()) {
            emailTextLayout.setError(null);
        }

        //checking the validity of password
        if (userDetails.getPassword().isEmpty()) {
            passwordTextLayout.setError("Enter a password");
            passwordText.requestFocus();
            return;
        }
        if (!userDetails.getPassword().isEmpty()) {
            passwordTextLayout.setError(null);
        }
        if (userDetails.getPassword().length() < 8) {
            passwordTextLayout.setError("Minimum length of a password should be 8");
            passwordText.requestFocus();
            return;
        }
        if ( !(userDetails.getPassword().length() < 8) ) {
            passwordTextLayout.setError(null);
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

                            progressBar.setVisibility(View.GONE);

                            finish();

                            //Intenting layout...................
                            Intent intent = new Intent(SignInActivityForDoctor.this, DoctorDashBoardActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onComplete: log in unsuccessful" + e.getMessage());

                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(SignInActivityForDoctor.this, "Invaid Login "+e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
