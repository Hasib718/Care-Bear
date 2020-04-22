package com.hasib.carebear.doctor.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.hasib.carebear.R;
import com.hasib.carebear.doctor.container.UserDetails;

import java.io.IOException;

public class DoctorProfileEditActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "DoctorProfileEditActivity";
    private static final int CHOOSE_IMAGE_REQUEST = 1001;
    //declaring editText....
    private EditText doctorNameEditText, specialistEditText, bmdcRegNoEditText, presentAddressEditText,
            commonChamberEditText, passwordEditText, mobileNoEditText, emailEditText;
    private LinearLayout checkBoxLinearLayout;
    private ImageView doctorImageView;
    private CheckBox checkMBBS, checkFCPS, checkMPhil, checkMDorMS;
    private ProgressBar imageProgressBar;
    private Button updateButton;

    private UserDetails currentUserDetails;
    private Uri uriProfileImage;
    private String newUploadedImageUri = "";

    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;

    private AlertDialog builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile_edit);
        this.setTitle("Edit your Profile");

        //Enable back button on Menu Bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initViews();

        //Firebase Authenticator
        mAuth = FirebaseAuth.getInstance();

        //Firebase Storage reference
        storageReference = FirebaseStorage.getInstance().getReference("doctors_profile_images");

        //Firebase Realtime Database reference

        Intent intent = getIntent();
        currentUserDetails = intent.getParcelableExtra("user");

        settingValuesIntoViews();

        MaterialTextView textView = findViewById(R.id.loadingText);
        textView.setText("Updating your data......");
        builder = new AlertDialog.Builder(DoctorProfileEditActivity.this)
                .setTitle("Please Wait")
                .setCancelable(false)
                .setView(R.layout.dialog_progress)
                .create();

        doctorImageView.setOnClickListener(this);
        updateButton.setOnClickListener(this);
    }

    private void initViews() {
        //initializing editText by finding Id....
        doctorNameEditText = findViewById(R.id.doctorNameEditTextId);
        specialistEditText = findViewById(R.id.specialistEditTextId);
        bmdcRegNoEditText = findViewById(R.id.regNoOfBMDCEditTextId);
        presentAddressEditText = findViewById(R.id.presentAddressEditTextId);
        commonChamberEditText = findViewById(R.id.commonChamberEditTextId);
        mobileNoEditText = findViewById(R.id.mobileNoEditTextId);
        emailEditText = findViewById(R.id.emailEditTextId);
        passwordEditText = findViewById(R.id.passwordEditTextId);

        doctorImageView = findViewById(R.id.doctorImageViewId);
        imageProgressBar = findViewById(R.id.loadingProgressBar);

        checkBoxLinearLayout = findViewById(R.id.checkBoxLayout);
        checkMBBS = findViewById(R.id.checkMBBSId);
        checkFCPS = findViewById(R.id.checkFCPSId);
        checkMPhil = findViewById(R.id.checkMPhilId);
        checkMDorMS = findViewById(R.id.checkMDId);

        updateButton = findViewById(R.id.updateButtonId);
    }

    private void settingValuesIntoViews() {
        doctorNameEditText.setText(currentUserDetails.getFullName());
        specialistEditText.setText(currentUserDetails.getSpecialist());
        bmdcRegNoEditText.setText(currentUserDetails.getRegistrationInfo());
        presentAddressEditText.setText(currentUserDetails.getPresentAddressInfo());
        commonChamberEditText.setText(currentUserDetails.getCommonChamberInfo());
        mobileNoEditText.setText(currentUserDetails.getMobile());
        emailEditText.setText(currentUserDetails.getEmail());
        passwordEditText.setText(currentUserDetails.getPassword());

        Glide.with(DoctorProfileEditActivity.this)
                .load(currentUserDetails.getDoctorImageUrl())
                .override(600, 600)
                .into(doctorImageView);

        String str = currentUserDetails.getCheckBoxInfo();
        if (str.matches("(.*)MBBS(.*)")) {
            checkMBBS.setChecked(true);
        }
        if (str.matches("(.*)FCPS(.*)")) {
            checkFCPS.setChecked(true);
        }
        if (str.matches("(.*)MD/MS(.*)")) {
            checkMDorMS.setChecked(true);
        }
        if (str.matches("(.*)MPhil(.*)")) {
            checkMPhil.setChecked(true);
        }
    }

    //Method for selecting and adding image
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

        if (requestCode == CHOOSE_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriProfileImage = data.getData();
            Log.d(TAG, "onActivityResult: "+uriProfileImage.toString());

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);

                doctorImageView.setImageBitmap(bitmap);
                uploadImageToFirebaseStorage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //For getting Image extension
    public String getExtension(Uri imageUri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }

    //Method for uploading image to firebase storage
    public void uploadImageToFirebaseStorage() {
        if (uriProfileImage != null) {

            //Doctor's Profile Images directory
            StorageReference reference = storageReference.child(System.currentTimeMillis()+"."+getExtension(uriProfileImage));

            Log.d(TAG, "uploadImageToFirebaseStorage: method called");
            //Visible the progress ber while the image is uploading
//            imageProgressBar.setVisibility(View.VISIBLE);

            //Task for uploading images to firebase directory
            reference.putFile(uriProfileImage)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d(TAG, "onComplete: image uploaded to firebase storage");

                            //The progress bar is set to GONE when the uploading task is done
//                            imageProgressBar.setVisibility(View.GONE);

                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());

                            newUploadedImageUri = uriTask.getResult().toString();

                            Log.d(TAG, "onSuccess: doctorImageUrl "+newUploadedImageUri);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: Image upload failed");
//                            imageProgressBar.setVisibility(View.GONE);

                            Toast.makeText(DoctorProfileEditActivity.this, "Image upload Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    //This Function is needed for back button.. Without this function
    //back button wouldn't work properly..
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.doctorImageViewId :
                showImageChooser();
                break;
            case R.id.updateButtonId :
                updateData();

        }
    }

    private void updateData() {
        FirebaseUser user = mAuth.getCurrentUser();

        collectEditedData();

        if (!newUploadedImageUri.isEmpty()) {
            deletePreviousImage();
        }

        Log.d(TAG, "updateData: entering to profile builder");
        if (collectEditedData() && user != null) {
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(currentUserDetails.getFullName())
                    .setPhotoUri(Uri.parse(currentUserDetails.getDoctorImageUrl()))
                    .build();

            Log.d(TAG, "updateData: profile build");
            user.updateProfile(profile)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "onComplete: Everything is ok");

                                updateUserDataBaseInfo();
                            }
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            finish();
                            builder.dismiss();
                            startActivity(new Intent(DoctorProfileEditActivity.this, DoctorProfileActivity.class));
                        }
                    });
        }
    }

    private void deletePreviousImage() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(currentUserDetails.getDoctorImageUrl());

        storageReference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: previous image deleted successfully");
                        currentUserDetails.setDoctorImageUrl(newUploadedImageUri);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Previous image deletion failed");
                    }
                });
    }

    private void updateUserDataBaseInfo(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("doctors_profile_info").child(currentUserDetails.getId());
        databaseReference.setValue(currentUserDetails);
    }

    private boolean collectEditedData() {
        Log.d(TAG, "getInformationFromUser: getting user data");

        currentUserDetails.setFullName(doctorNameEditText.getText().toString());
        currentUserDetails.setMobile(mobileNoEditText.getText().toString());
        currentUserDetails.setSpecialist(specialistEditText.getText().toString());

        currentUserDetails.setCheckBoxInfoEmpty();
        for(int i=0; i<checkBoxLinearLayout.getChildCount(); i++) {
            if (((CheckBox) checkBoxLinearLayout.getChildAt(i)).isChecked()) {
                currentUserDetails.setCheckBoxInfo(((CheckBox) checkBoxLinearLayout.getChildAt(i)).getText().toString());
            }
        }
        Log.d(TAG, "getInformationFromUser: getting user data 2");
        currentUserDetails.setRegistrationInfo(bmdcRegNoEditText.getText().toString());
        currentUserDetails.setPresentAddressInfo(presentAddressEditText.getText().toString());
        currentUserDetails.setCommonChamberInfo(commonChamberEditText.getText().toString());
        currentUserDetails.setEmail(emailEditText.getText().toString());
        currentUserDetails.setPassword(passwordEditText.getText().toString());

        if (currentUserDetails.getFullName().isEmpty()) {
            doctorNameEditText.setError("Name required");
            doctorNameEditText.requestFocus();
            return false;
        }
        /**
         * Will be added these verification after implementing email verification and forget password facilities
         * // TODO: 22-Apr-20 Have to implement forget password and email, mobile number verification
         */
        if(currentUserDetails.getEmail().isEmpty()) {
            emailEditText.setError("Enter an Email Address");
            emailEditText.requestFocus();
            return false;
        }
        //Checking email validity
        if (!Patterns.EMAIL_ADDRESS.matcher(currentUserDetails.getEmail()).matches()) {
            emailEditText.setError("Enter a valid email address");
            emailEditText.requestFocus();
            return false;
        }

        //checking the validity of password
        if (currentUserDetails.getPassword().length() < 8) {
            passwordEditText.setError("Minimum length of a password should be 8");
            passwordEditText.requestFocus();
            return false;
        }

        if (currentUserDetails.getPassword().isEmpty()) {
            passwordEditText.setError("Enter a password");
            passwordEditText.requestFocus();
            return false;
        }
        if (currentUserDetails.getMobile().isEmpty()) {
            mobileNoEditText.setError("Mobile Number Required");
            mobileNoEditText.requestFocus();
            return false;
        }
        if (currentUserDetails.getMobile().length() != 11) {
            mobileNoEditText.setError("Number must be 11 digits");
            mobileNoEditText.requestFocus();
            return false;
        }
        if (currentUserDetails.getRegistrationInfo().isEmpty()) {
            bmdcRegNoEditText.setError("Registration Number Required");
            bmdcRegNoEditText.requestFocus();
            return false;
        }
        if (!((CheckBox) checkBoxLinearLayout.getChildAt(0)).isChecked()) {
            ((CheckBox) checkBoxLinearLayout.getChildAt(0)).setError("Minimum MBBS Degree Required");
            checkBoxLinearLayout.getChildAt(0).requestFocus();
            return false;
        }
        return true;
    }
}
