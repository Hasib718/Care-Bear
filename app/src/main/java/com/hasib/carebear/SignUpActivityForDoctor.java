package com.hasib.carebear;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class SignUpActivityForDoctor extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SignUpActivityForDoctor";

    private EditText emailET, passwordET;
    private Button newSignUpButton;
    private TextView signInTextView;
    private UserDetails userDetails;
    private ProgressBar progressBar;

    //firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_for_doctor);
        this.setTitle("Sign Up For Doctor");

        //Enable back button on Menu Bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        emailET = (EditText) findViewById(R.id.emailEditText);
        passwordET = (EditText) findViewById(R.id.passwordEditText);
        newSignUpButton = (Button) findViewById(R.id.newSignUpButton);
        signInTextView = (TextView) findViewById(R.id.signInTextView);
        progressBar = (ProgressBar) findViewById(R.id.progressBarUp);

        //info container class
        userDetails = new UserDetails();


        //firebase
        mAuth = FirebaseAuth.getInstance();

        //Setting Button on click Listener
        newSignUpButton.setOnClickListener(this);
        signInTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        userDetails.setEmail(emailET.getText().toString());
        userDetails.setPassword(passwordET.getText().toString());

        switch (v.getId()) {
            case R.id.newSignUpButton : {

                //adding username & password to firebase also authenticate
                userRegister(userDetails);
            }
            break;

            case R.id.signInTextView : {
                Log.d(TAG, "onClick: intenting to sign in activity");

                Intent intent = new Intent(SignUpActivityForDoctor.this, SignInActivityForDoctor.class);
                startActivity(intent);
            }
            break;
        }
    }

    private void userRegister(UserDetails userDetails) {
        //Checking email validity
        if(userDetails.getEmail().isEmpty()) {
            emailET.setError("Enter an Email Address");
            emailET.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(userDetails.getEmail()).matches()) {
            emailET.setError("Enter a valid email address");
            emailET.requestFocus();
            return;
        }

        //checking the validity of password
        if (userDetails.getPassword().isEmpty()) {
            passwordET.setError("Enter a password");
            passwordET.requestFocus();
            return;
        }
        if (userDetails.getPassword().length() < 8) {
            passwordET.setError("Minimum length of a password should be 8");
            passwordET.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        //Firebase authenticate
        mAuth.createUserWithEmailAndPassword(userDetails.getEmail(), userDetails.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //changed progress bar's visibility to GONE
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");

                            finish();
                            //intenting Layout....................
                            Intent intent = new Intent(SignUpActivityForDoctor.this, ProfileSelectingActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d(TAG, "createUserWithEmail:failure", task.getException());

                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(SignUpActivityForDoctor.this, "User is already registered.",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SignUpActivityForDoctor.this, "Error: "+task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}
