package com.hasib.carebear;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.media.MediaTimestamp;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class SignUpActivityForDoctor extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SignUpActivityForDoctor";
    private static final int CHOOSE_IMAGE_REQUEST = 101;

    //views
    private ImageView doctorImage;
    private ProgressBar imageProgressBar;
    private EditText nameText;
    private EditText mobileNoText;
    private EditText specialistText;
    private LinearLayout checkBoxLayout;
    private EditText registrationNoText;
    private EditText presentAddressText;
    private EditText permanentAddressText;
    private EditText commonChamberText;
    private EditText emailText;
    private EditText passwordText;
    private Button signUpButton;
    private ProgressBar progressBar;

    //Info container class
    private UserDetails userDetails;

    //firebase
    private FirebaseAuth mAuth;

    //Firebase Images Storage Reference
    StorageReference imagesStorageReference;
    //Uploaded Image URL
    String doctorImageUrl;

    //image uri
    private Uri uriProfileImage;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_for_doctor);
        this.setTitle("Sign Up For Doctor");

        //Enable back button on Menu Bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        doctorImage = (ImageView) findViewById(R.id.doctorImage);
        imageProgressBar = (ProgressBar) findViewById(R.id.imageProgressBar);
        nameText = (EditText) findViewById(R.id.nameText);
        mobileNoText = (EditText) findViewById(R.id.mobileNoText);
        specialistText = (EditText) findViewById(R.id.specialistText);
        checkBoxLayout = (LinearLayout) findViewById(R.id.checkBoxLayout);
        registrationNoText = (EditText) findViewById(R.id.registrationNoText);
        presentAddressText = (EditText) findViewById(R.id.presentAddressText);
        permanentAddressText = (EditText) findViewById(R.id.permanentAddressText);
        commonChamberText = (EditText) findViewById(R.id.commonChamberText);
        emailText = (EditText) findViewById(R.id.emailText);
        passwordText = (EditText) findViewById(R.id.passwordText);
        signUpButton = (Button) findViewById(R.id.newSignUpButton);
        progressBar = (ProgressBar) findViewById(R.id.progressBarUp);

        //info container class
        userDetails = new UserDetails();

        //Firebase authenticator
        mAuth = FirebaseAuth.getInstance();

        //Firebase profile images reference
        imagesStorageReference = FirebaseStorage.getInstance()
                .getReference("profileimages/"+System.currentTimeMillis()+".jpg");

        //Setting Button on click Listener
        signUpButton.setOnClickListener(this);
        doctorImage.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        getInformationFromUser();

        switch (v.getId()) {
            case R.id.newSignUpButton : {

                //adding username & password to firebase also authenticate
                userRegister(userDetails);
            }
            break;

            case R.id.doctorImage : {
                Log.d(TAG, "onClick: intenting to sign in activity");

                showImageChooser();
            }
            break;
        }
    }

    private void getInformationFromUser() {
        userDetails.setEmail(emailText.getText().toString());
        userDetails.setPassword(passwordText.getText().toString());
    }

    private void userRegister(UserDetails userDetails) {
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

    //Method for selecting and aadding image
    public void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), CHOOSE_IMAGE_REQUEST);
    }

    //Image selecting activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            uriProfileImage = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);

                doctorImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Method for uploading image to firebase storage
    public void uploadImageToFirebaseStorage() {
        if (uriProfileImage != null) {

            //Visible the progress ber while the image is uploading
            imageProgressBar.setVisibility(View.VISIBLE);

            imagesStorageReference.putFile(uriProfileImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d(TAG, "onComplete: image uploaded to firebase storage");

                            //The progress bar is set to GONE when the uploading task is done
                            imageProgressBar.setVisibility(View.GONE);

                            doctorImageUrl = taskSnapshot.getStorage().getDownloadUrl().toString();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: Image upload failed");
                            imageProgressBar.setVisibility(View.GONE);

                            Toast.makeText(SignUpActivityForDoctor.this, "Image upload Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public void saveUserInformation() {

    }
}
