package com.hasib.carebear.doctor;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hasib.carebear.R;
import com.hasib.carebear.UserDetails;

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

    //Firebase Authenticator
    private FirebaseAuth mAuth;

    //Firebase Storage Reference
    private StorageReference storageReference;
    //Uploaded Image URL
    private String doctorImageUrl;

    //Firebase Realtime Database Reference
    private DatabaseReference databaseReference;

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

        //Firebase Authenticator
        mAuth = FirebaseAuth.getInstance();

        //Firebase Storage reference
        storageReference = FirebaseStorage.getInstance().getReference("doctors_profile_images");

        //Firebase Realtime Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("doctors_profile_info");

        //Setting Button on click Listener
        signUpButton.setOnClickListener(this);
        doctorImage.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.newSignUpButton : {
                Log.d(TAG, "onClick: New Doctor Sign up button clicked");
                progressBar.setVisibility(View.VISIBLE);
                //Getting data from user
                if (!getInformationFromUser()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    return;
                }
                Log.d(TAG, "onClick: going to registration");
                //adding username & password to firebase also authenticate
                userRegister();
            }
            break;

            case R.id.doctorImage : {
                Log.d(TAG, "onClick: selecting profile picture");

                //Image chooser method
                showImageChooser();
            }
            break;
        }
    }

    private boolean getInformationFromUser() {
        Log.d(TAG, "getInformationFromUser: getting user data");

        userDetails.setFullName(nameText.getText().toString());
        userDetails.setMobile(mobileNoText.getText().toString());
        userDetails.setSpecialist(specialistText.getText().toString());
        for(int i=0; i<checkBoxLayout.getChildCount(); i++) {
            if (((CheckBox) checkBoxLayout.getChildAt(i)).isChecked()) {
                userDetails.setCheckBoxInfo(((CheckBox) checkBoxLayout.getChildAt(i)).getText().toString());
            }
        }
        Log.d(TAG, "getInformationFromUser: getting user data 2");
        userDetails.setRegistrationInfo(registrationNoText.getText().toString());
        userDetails.setPresentAddressInfo(presentAddressText.getText().toString());
        userDetails.setPermanentAddressInfo(permanentAddressText.getText().toString());
        userDetails.setCommonChamberInfo(commonChamberText.getText().toString());
        userDetails.setEmail(emailText.getText().toString());
        userDetails.setPassword(passwordText.getText().toString());

        if (userDetails.getFullName().isEmpty()) {
            nameText.setError("Name required");
            nameText.requestFocus();
            return false;
        }
        if(userDetails.getEmail().isEmpty()) {
            emailText.setError("Enter an Email Address");
            emailText.requestFocus();
            return false;
        }
        if (userDetails.getPassword().isEmpty()) {
            passwordText.setError("Enter a password");
            passwordText.requestFocus();
            return false;
        }
        if (userDetails.getMobile().isEmpty()) {
            mobileNoText.setError("Mobile Number Required");
            mobileNoText.requestFocus();
            return false;
        }
        if (userDetails.getMobile().length() != 11) {
            mobileNoText.setError("Number must be 11 digits");
            mobileNoText.requestFocus();
            return false;
        }
        if (userDetails.getRegistrationInfo().isEmpty()) {
            registrationNoText.setError("Registration Number Required");
            registrationNoText.requestFocus();
            return false;
        }
        if (!((CheckBox) checkBoxLayout.getChildAt(0)).isChecked()) {
            ((CheckBox) checkBoxLayout.getChildAt(0)).setError("Minimum MBBS Degree Required");
            ((CheckBox) checkBoxLayout.getChildAt(0)).requestFocus();
            return false;
        }
        return true;
    }

    //Email & Password Registration
    private void userRegister() {
        //Checking email validity
        if (!Patterns.EMAIL_ADDRESS.matcher(userDetails.getEmail()).matches()) {
            emailText.setError("Enter a valid email address");
            emailText.requestFocus();
            return;
        }

        //checking the validity of password
        if (userDetails.getPassword().length() < 8) {
            passwordText.setError("Minimum length of a password should be 8");
            passwordText.requestFocus();
            return;
        }

        Log.d(TAG, "userRegister: on firebase authenticator");
        //Firebase authenticate
        mAuth.createUserWithEmailAndPassword(userDetails.getEmail(), userDetails.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //changed progress bar's visibility to GONE
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail: success");

                            //Image uploading method
                            uploadImageToFirebaseStorage();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d(TAG, "createUserWithEmail: failure", task.getException());

                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(SignUpActivityForDoctor.this, "User is already registered.",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SignUpActivityForDoctor.this, "Error: "+task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Log.d(TAG, "onSuccess: current user "+authResult.getUser().toString());
                        
                    }
                });
    }

    private void saveDoctorInto() {
        String key = databaseReference.push().getKey();
        
        databaseReference.child(userDetails.getMobile()).setValue(userDetails);
        Log.d(TAG, "saveDoctorInto: data saved into database");
    }

    //Method for selecting and adding image
    public void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), CHOOSE_IMAGE_REQUEST);
    }

    //For getting Image extension
    public String getExtension(Uri imageUri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }

    //Image selecting activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriProfileImage = data.getData();
            Log.d(TAG, "onActivityResult: "+uriProfileImage.toString());

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);

                doctorImage.setImageBitmap(bitmap);

                //uploadImageToFirebaseStorage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Method for uploading image to firebase storage
    public void uploadImageToFirebaseStorage() {
        if (uriProfileImage != null) {

            //Doctor's Profile Images directory
            StorageReference reference = storageReference.child(System.currentTimeMillis()+"."+getExtension(uriProfileImage));

            Log.d(TAG, "uploadImageToFirebaseStorage: method called");
            //Visible the progress ber while the image is uploading
            imageProgressBar.setVisibility(View.VISIBLE);

            //Task for uploading images to firebase directory
            reference.putFile(uriProfileImage)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            saveUserInformation();
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d(TAG, "onComplete: image uploaded to firebase storage");

                            //The progress bar is set to GONE when the uploading task is done
                            imageProgressBar.setVisibility(View.GONE);

                            userDetails.setDoctorImageUrl(taskSnapshot.getStorage().getDownloadUrl().toString());
                            Log.d(TAG, "onSuccess: doctorImageUrl "+userDetails.getDoctorImageUrl());
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

        FirebaseUser user = mAuth.getCurrentUser();

        Log.d(TAG, "saveUserInformation: entering to profile build");
        if (user != null) {
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(userDetails.getFullName())
                    .setPhotoUri(Uri.parse(userDetails.getDoctorImageUrl()))
                    .build();
            Log.d(TAG, "saveUserInformation: profile built");

            user.updateProfile(profile)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "onComplete: Everythings OK");
                                Toast.makeText(SignUpActivityForDoctor.this, "Profile Updated", Toast.LENGTH_SHORT).show();

                                progressBar.setVisibility(View.GONE);

                                //Doctor Info saving method
                                saveDoctorInto();
                            }
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Log.d(TAG, "onSuccess: Going to Doctor's Dash Board");
                            //Intenting to doctor's dash board

                            finish();

                            Intent intent = new Intent(SignUpActivityForDoctor.this, DoctorDashBoardActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    });
        }
    }
}
