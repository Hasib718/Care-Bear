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
import android.view.LayoutInflater;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.hasib.carebear.R;
import com.hasib.carebear.doctor.container.Doctor;

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

    private Doctor currentDoctor;

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

        //Firebase Realtime Database reference

        Intent intent = getIntent();
        currentDoctor = intent.getParcelableExtra("user");

        settingValuesIntoViews();

        doctorImageView.setOnClickListener(this);
        updateButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.doctorImageViewId :
                showImageChooser();
                break;
            case R.id.updateButtonId :
                updateData();
                break;
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

    private void initLoadingDialog(String message) {
        LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_progress, null);

        MaterialTextView textView = view.findViewById(R.id.loadingText);
        textView.setText(message);
        builder = new AlertDialog.Builder(DoctorProfileEditActivity.this)
                .setTitle("Please Wait")
                .setCancelable(false)
                .setView(view)
                .create();
        builder.show();
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
        doctorNameEditText.setText(currentDoctor.getFullName());
        specialistEditText.setText(currentDoctor.getSpecialist());
        bmdcRegNoEditText.setText(currentDoctor.getRegistrationInfo());
        presentAddressEditText.setText(currentDoctor.getPresentAddressInfo());
        commonChamberEditText.setText(currentDoctor.getMedicalInfo());
        mobileNoEditText.setText(currentDoctor.getMobile());
        emailEditText.setText(currentDoctor.getEmail());
        passwordEditText.setText(currentDoctor.getPassword());

        Glide.with(DoctorProfileEditActivity.this)
                .load(currentDoctor.getDoctorImageUrl())
                .override(600, 600)
                .into(doctorImageView);

        String str = currentDoctor.getCheckBoxInfo();
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
            Log.d(TAG, "onActivityResult: "+data.getData().toString());

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());

                doctorImageView.setImageBitmap(bitmap);
                uploadImageToFirebaseStorage(data.getData());
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
    public void uploadImageToFirebaseStorage(Uri uriProfileImage) {
        if (uriProfileImage != null) {
            initLoadingDialog("Uploading you image.....");

            //Doctor's Profile Images directory
            StorageReference reference = FirebaseStorage
                    .getInstance()
                    .getReference("doctors_profile_images")
                    .child(System.currentTimeMillis()+"."+getExtension(uriProfileImage));

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

                            deletePreviousImage(uriTask.getResult().toString());

                            Log.d(TAG, "onSuccess: doctorImageUrl "+uriTask.getResult().toString());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: Image upload failed");
//                            imageProgressBar.setVisibility(View.GONE);

                            Toast.makeText(DoctorProfileEditActivity.this, "Image upload Failed", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            Uri sessionUri = taskSnapshot.getUploadSessionUri();
                        }
                    });
        }
    }

    private void deletePreviousImage(final String newUploadedImageUri) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(currentDoctor.getDoctorImageUrl());
        Log.d(TAG, "deletePreviousImage: prev uri "+currentDoctor.getDoctorImageUrl());

        storageReference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: previous image deleted successfully");
                        Log.d(TAG, "onSuccess: new uri "+newUploadedImageUri);
                        currentDoctor.setDoctorImageUrl(newUploadedImageUri);
                        builder.dismiss();
                        Toast.makeText(DoctorProfileEditActivity.this, "Image uploaded successfully", Toast.LENGTH_LONG).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Previous image deletion failed");
                        builder.dismiss();
                        Toast.makeText(DoctorProfileEditActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void updateData() {
        initLoadingDialog("Updating your data......");
        FirebaseUser user = mAuth.getCurrentUser();

        if(!collectEditedData()) {
            builder.dismiss();
            return;
        }

        Log.d(TAG, "updateData: entering to profile builder");
        if (collectEditedData() && user != null) {
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(currentDoctor.getFullName())
                    .setPhotoUri(Uri.parse(currentDoctor.getDoctorImageUrl()))
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

                            builder.dismiss();

                            finish();
                            startActivity(new Intent(DoctorProfileEditActivity.this, DoctorProfileActivity.class));
                        }
                    });
        }
    }

    private void updateUserDataBaseInfo(){
        DatabaseReference databaseReference = FirebaseDatabase
                .getInstance()
                .getReference("doctors_profile_info")
                .child(currentDoctor.getId());

        databaseReference.keepSynced(true);
        databaseReference
                .setValue(currentDoctor)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Doctor's profile updated successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Doctor's profile updated failed");
                    }
                });
    }

    private boolean collectEditedData() {
        Log.d(TAG, "getInformationFromUser: getting user data");

        currentDoctor.setFullName(doctorNameEditText.getText().toString());
        currentDoctor.setMobile(mobileNoEditText.getText().toString());
        currentDoctor.setSpecialist(specialistEditText.getText().toString());

        currentDoctor.setCheckBoxInfoEmpty();
        for(int i=0; i<checkBoxLinearLayout.getChildCount(); i++) {
            if (((CheckBox) checkBoxLinearLayout.getChildAt(i)).isChecked()) {
                currentDoctor.setCheckBoxInfo(((CheckBox) checkBoxLinearLayout.getChildAt(i)).getText().toString());
            }
        }
        Log.d(TAG, "getInformationFromUser: getting user data 2");
        currentDoctor.setRegistrationInfo(bmdcRegNoEditText.getText().toString());
        currentDoctor.setPresentAddressInfo(presentAddressEditText.getText().toString());
        currentDoctor.setMedicalInfo(commonChamberEditText.getText().toString());
        currentDoctor.setEmail(emailEditText.getText().toString());
        currentDoctor.setPassword(passwordEditText.getText().toString());

        if (currentDoctor.getFullName().isEmpty()) {
            doctorNameEditText.setError("Name required");
            doctorNameEditText.requestFocus();
            return false;
        }
        /**
         * Will be added these verification after implementing email verification and forget password facilities
         * // TODO: 22-Apr-20 Have to implement forget password and email, mobile number verification
         */
        if(currentDoctor.getEmail().isEmpty()) {
            emailEditText.setError("Enter an Email Address");
            emailEditText.requestFocus();
            return false;
        }
        //Checking email validity
        if (!Patterns.EMAIL_ADDRESS.matcher(currentDoctor.getEmail()).matches()) {
            emailEditText.setError("Enter a valid email address");
            emailEditText.requestFocus();
            return false;
        }

        //checking the validity of password
        if (currentDoctor.getPassword().length() < 8) {
            passwordEditText.setError("Minimum length of a password should be 8");
            passwordEditText.requestFocus();
            return false;
        }

        if (currentDoctor.getPassword().isEmpty()) {
            passwordEditText.setError("Enter a password");
            passwordEditText.requestFocus();
            return false;
        }
        if (currentDoctor.getMobile().isEmpty()) {
            mobileNoEditText.setError("Mobile Number Required");
            mobileNoEditText.requestFocus();
            return false;
        }
        if (currentDoctor.getMobile().length() != 11) {
            mobileNoEditText.setError("Number must be 11 digits");
            mobileNoEditText.requestFocus();
            return false;
        }
        if (currentDoctor.getRegistrationInfo().isEmpty()) {
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
