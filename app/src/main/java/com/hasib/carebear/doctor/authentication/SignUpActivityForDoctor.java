package com.hasib.carebear.doctor.authentication;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textview.MaterialTextView;
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
import com.hasib.carebear.doctor.DoctorDashBoardActivity;
import com.hasib.carebear.doctor.container.UserDetails;

import java.io.IOException;

public class SignUpActivityForDoctor extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SignUpActivityForDoctor";
    private static final int CHOOSE_IMAGE_REQUEST = 101;

    //views
    private ImageView doctorImage;
    private ProgressBar imageProgressBar;
    private EditText nameText, mobileNoText, specialistText, registrationNoText, presentAddressText, commonChamberText, emailText, passwordText;
    private LinearLayout checkBoxLayout;
  
    private Button signUpButton;

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

    private AlertDialog loadingDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_for_doctor);
        this.setTitle("Sign Up For Doctor");

        //Enable back button on Menu Bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initViews();

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

    private void initLoadingDialog() {
        LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_progress, null);

        MaterialTextView textView = view.findViewById(R.id.loadingText);
        textView.setText("Signing Up......");
        loadingDialog = new AlertDialog.Builder(SignUpActivityForDoctor.this)
                .setTitle("Please wait")
                .setCancelable(false)
                .setView(view)
                .create();
        loadingDialog.show();
    }

    private void initViews() {
        doctorImage = findViewById(R.id.doctorImage);
        imageProgressBar = findViewById(R.id.imageProgressBar);
        nameText = findViewById(R.id.nameText);
        mobileNoText = findViewById(R.id.mobileNoText);
        specialistText = findViewById(R.id.specialistText);
        checkBoxLayout = findViewById(R.id.checkBoxLayout);
        registrationNoText = findViewById(R.id.registrationNoText);
        presentAddressText = findViewById(R.id.presentAddressText);
        commonChamberText = findViewById(R.id.commonChamberText);
        emailText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);
        signUpButton = findViewById(R.id.newSignUpButton);
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
        switch (v.getId()) {
            case R.id.newSignUpButton : {
                Log.d(TAG, "onClick: New Doctor Sign up button clicked");
                //Getting data from user
                if (!getInformationFromUser()) {
                    return;
                }
                Log.d(TAG, "onClick: going to registration");
                initLoadingDialog();
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

        userDetails.setCheckBoxInfoNull();
        for(int i=0; i<checkBoxLayout.getChildCount(); i++) {
            if (((CheckBox) checkBoxLayout.getChildAt(i)).isChecked()) {
                userDetails.setCheckBoxInfo(((CheckBox) checkBoxLayout.getChildAt(i)).getText().toString());
            }
        }
        Log.d(TAG, "getInformationFromUser: getting user data 2");
        userDetails.setRegistrationInfo(registrationNoText.getText().toString());
        userDetails.setPresentAddressInfo(presentAddressText.getText().toString());
        userDetails.setCommonChamberInfo(commonChamberText.getText().toString());
        userDetails.setEmail(emailText.getText().toString());
        userDetails.setPassword(passwordText.getText().toString());

        if (doctorImage.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.baseline_add_a_photo_black_48dp).getConstantState()) {
            Toast.makeText(SignUpActivityForDoctor.this, "You must select a Image\nTap the icon on the top to select an Image.", Toast.LENGTH_SHORT).show();
            return false;
        }

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
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // If sign in fails, display a message to the user.
                        Log.d(TAG, "createUserWithEmail: failure"+ e.getMessage());

                        loadingDialog.dismiss();

                        if (e instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(SignUpActivityForDoctor.this, "User is already registered.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SignUpActivityForDoctor.this, "Error: "+e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
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
        String key = mAuth.getCurrentUser().getUid();
        userDetails.setId(key);

        databaseReference
                .child(key)
                .setValue(userDetails)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "saveDoctorInto: data saved into database");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "saveDoctorInto: data saved into database failed");
                    }
                });
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

                doctorImage.setImageBitmap(bitmap);
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

                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());

                            userDetails.setDoctorImageUrl(uriTask.getResult().toString());

                            Log.d(TAG, "onSuccess: doctorImageUrl "+userDetails.getDoctorImageUrl());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: Image upload failed");
                            imageProgressBar.setVisibility(View.GONE);

                            loadingDialog.dismiss();

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

                            loadingDialog.dismiss();

                            finish();

                            Intent intent = new Intent(SignUpActivityForDoctor.this, DoctorDashBoardActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);

                            // TODO: 10-Apr-20 have to implement email verification methods
                            // TODO: 10-Apr-20 have add other authentication services like Google & Facebook
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loadingDialog.dismiss();
                        }
                    });
        }
    }
}
