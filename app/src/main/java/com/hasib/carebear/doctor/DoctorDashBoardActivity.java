package com.hasib.carebear.doctor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hasib.carebear.R;
import com.hasib.carebear.doctor.adapter.RecyclerViewAdapter;
import com.hasib.carebear.doctor.authentication.SignInActivityForDoctor;
import com.hasib.carebear.doctor.container.Chamber;
import com.hasib.carebear.doctor.fragment.ChamberAddingDialog;
import com.hasib.carebear.doctor.fragment.ChamberEditingDialog;
import com.hasib.carebear.doctor.fragment.DoctorProfileActivity;
import com.hasib.carebear.doctor.listener.ChamberDialogListener;
import com.hasib.carebear.doctor.listener.ChamberEventListener;
import com.hasib.carebear.doctor.listener.ChamberAddingDialogTimeSetListener;
import com.hasib.carebear.doctor.listener.TimePickerListener;
import com.hasib.carebear.support.LatLong;

import java.sql.Time;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DoctorDashBoardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        ChamberDialogListener, ChamberEventListener, TimePickerListener {
    private static final String TAG = "DoctorDashBoardActivity";

    //Floating Action "+" Bar
    private FloatingActionButton chamberAddingButton;

    //Main parent drawer layout
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    //Navigation Menu
    private NavigationView navigationView;
    private View navHeader;

    //Chamber class
    private List<Chamber> chamberList;

    //Tapped chamber position
    private int position;
    private String chamberTime;

    //For showing chamber details to user
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;

    //Firebase authenticator
    private FirebaseAuth mAuth;

    //Menu Item
    private MenuItem editButton;
    private MenuItem deleteButton;

    private AlertDialog builder;

    /**
     * Interface for setting time on ChamberAddingDialog after choosing time
     */
    private ChamberAddingDialogTimeSetListener testing;
    private Chamber invokedChamber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_dash_board);
        this.setTitle("Chambers Dashboard");

        //Enable HamBurger Action Bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Floating action button
        chamberAddingButton = findViewById(R.id.chamberAddingButtonId);

        //Navigation panel control
        navigationView = findViewById(R.id.navigationId);
        drawerLayout = findViewById(R.id.drawerId);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //Navigation View on click listener
        navigationView.setNavigationItemSelectedListener(this);
        navHeader = navigationView.getHeaderView(0);

        //Firebase authenticator initialization
        mAuth = FirebaseAuth.getInstance();

        //Chamber class initialization;
        chamberList = new ArrayList<>();

        //Floating button on click listener
        chamberAddingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: chamber adding button pressed");

                //chamber info adding dialog box
                openDialog();
            }
        });

        //Recycler view initialization method
        initChamberRecyclerView();

        //Adapter Listener
        this.adapter.setListener(this);

        //for hiding edit & delete button
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                editButton.setVisible(false);
                deleteButton.setVisible(false);
                return false;
            }
        });
    }

    private void fetchingChamberDatabasekeys() {
        initLoadingDialog("Loading your data.....");

        FirebaseDatabase
                .getInstance()
                .getReference("doctors_profile_info")
                .child(mAuth.getCurrentUser().getUid())
                .child("chamber")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        HashMap<String, String> hashMap = (HashMap<String, String>) dataSnapshot.getValue();

                        try {
                            Log.d(TAG, "onDataChange: " + hashMap.values().toString());

                            for (Map.Entry<String, String> data : hashMap.entrySet()) {
                                fetchingChamberData(data.getValue());
                            }
                        } catch (Exception e) {
                            builder.dismiss();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d(TAG, "onCancelled: " + databaseError.getMessage());
                    }
                });
    }

    private void fetchingChamberData(String s) {
        FirebaseDatabase
                .getInstance()
                .getReference("doctors_chamber_info")
                .child(s)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        HashMap hashMap = (HashMap) dataSnapshot.getValue();

                        Log.d(TAG, "onDataChange: " + hashMap.toString());

                        HashMap latlng = (HashMap) hashMap.get("chamberLatLng");

                        LatLong latLong = new LatLong((Double) latlng.get("latitude"), (Double) latlng.get("longitude"));
                        Chamber chamber = new Chamber((String) hashMap.get("chamberName"), (String) hashMap.get("chamberFees"),
                                (String) hashMap.get("chamberAddress"), latLong,
                                (String) hashMap.get("chamberTime"), (HashMap) hashMap.get("chamberOpenDays"),
                                (String) hashMap.get("doctorUserProfileId"), (String) hashMap.get("chamberDatabaseId"),
                                (String) hashMap.get("chamberDatabaseIdKeyInDoctorProfile"));

                        chamberList.add(chamber);
                        adapter.notifyDataSetChanged();

                        Log.d(TAG, "onDataChange: chamber " + chamber.toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        builder.dismiss();
    }

    private void initLoadingDialog(String message) {
        LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_progress, null);

        MaterialTextView textView = view.findViewById(R.id.loadingText);
        textView.setText(message);
        builder = new AlertDialog.Builder(DoctorDashBoardActivity.this)
                .setTitle("Please Wait")
                .setCancelable(false)
                .setView(view)
                .create();
        builder.show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(DoctorDashBoardActivity.this, SignInActivityForDoctor.class));
        }

        fetchingChamberDatabasekeys();

        initNavigationHeader(mAuth.getCurrentUser());
    }

    private void initNavigationHeader(final FirebaseUser user) {
        MaterialTextView profileName = navHeader.findViewById(R.id.profileNameId);
        MaterialTextView profileEmail = navHeader.findViewById(R.id.profileEmailId);
        final ImageView profileImage = navHeader.findViewById(R.id.profileImageId);

        Log.d(TAG, "initNavigationHeader: " + user.getPhotoUrl());

        Glide.with(DoctorDashBoardActivity.this)
                .load(user.getPhotoUrl())
                .fitCenter()
                .circleCrop()
                .into(profileImage);

        profileName.setText(user.getDisplayName());
        profileEmail.setText(user.getEmail());
    }

    //Method for chamber adding dialog box
    private void openDialog() {
        //Chamber adding alert dialog box initialization
        ChamberAddingDialog chamberAddingDialog = new ChamberAddingDialog(this);

        //Getting fragment support
        chamberAddingDialog.show(getSupportFragmentManager(), "Chamber Adding Dialog");
    }

    private void initChamberRecyclerView() {
        Log.d(TAG, "initChamberRecyclerView: init recyclerView");

        //Initializing recycler view
        recyclerView = findViewById(R.id.doctorChamberRecyclerView);

        //Initializing recycler view adapter
        adapter = new RecyclerViewAdapter(this, chamberList);

        //setting adapter to recycler view
        recyclerView.setAdapter(adapter);

        //Recycler view layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_chamber_menu, menu);

        //Menu item initialization
        editButton = menu.findItem(R.id.editChamberId);
        deleteButton = menu.findItem(R.id.deleteChamberId);

        return super.onCreateOptionsMenu(menu);
    }

    //Method for controlling navigation view close or open hamburger button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.editChamberId: {
                Log.d(TAG, "onOptionsItemSelected: Chamber " + invokedChamber.toString());

                ChamberEditingDialog chamberEditingDialog = new ChamberEditingDialog(this, invokedChamber, position);
                chamberEditingDialog.show(getSupportFragmentManager(), "Edit Chamber Info");
            }
            break;

            case R.id.deleteChamberId: {
                new AlertDialog.Builder(this)
                        .setTitle("Are you want to delete this chamber?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                chamberList.clear();
                                adapter.notifyDataSetChanged();

                                deleteChamberDataFromDatabase();

                                editButton.setVisible(false);
                                deleteButton.setVisible(false);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                editButton.setVisible(false);
                                deleteButton.setVisible(false);
                            }
                        })
                        .create()
                        .show();
            }
            break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteChamberDataFromDatabase() {
        FirebaseDatabase
                .getInstance()
                .getReference("doctors_profile_info")
                .child(mAuth.getCurrentUser().getUid())
                .child("chamber")
                .child(invokedChamber.getChamberDatabaseIdKeyInDoctorProfile())
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: "+invokedChamber.getChamberName()+" chamber Id on doctor profile deleted");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onSuccess: "+invokedChamber.getChamberName()+" chamber Id on doctor profile deletion failed!");
                    }
                });

        FirebaseDatabase
                .getInstance()
                .getReference("doctors_chamber_info")
                .child(invokedChamber.getChamberDatabaseId())
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        onStart();

                        Log.d(TAG, "onSuccess: "+invokedChamber.getChamberName()+" deleted successfully");
                        Toast.makeText(DoctorDashBoardActivity.this, invokedChamber.getChamberName()+" deleted successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onSuccess: "+invokedChamber.getChamberName()+" deletion failed!");
                        Toast.makeText(DoctorDashBoardActivity.this, invokedChamber.getChamberName()+" deletion failed!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        //On navigation view item pressed
        switch (item.getItemId()) {
            case R.id.homeMenuId: {
                Log.d(TAG, "onNavigationItemSelected: Tapped on Home Button");

                //Intenting to this activity
                startActivity(new Intent(DoctorDashBoardActivity.this, DoctorDashBoardActivity.class));
            }
            break;

            case R.id.profileMenuId: {
                Log.d(TAG, "onNavigationItemSelected: Profile Button Pressed");

                //Intenting to doctor's profile activity
                startActivity(new Intent(DoctorDashBoardActivity.this, DoctorProfileActivity.class));
            }
            break;

            case R.id.signOutMenuId: {
                Log.d(TAG, "onNavigationItemSelected: user signing out");

                //Signing out current user
                mAuth.signOut();
                finish();

                //Intenting to sign in activity for doctor
                startActivity(new Intent(DoctorDashBoardActivity.this, SignInActivityForDoctor.class));
            }
            break;

            case R.id.shareMenuId: {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");

                String subject = "Care Bear- In a way of Healthiness";
                String body = "This is a medical type app. It helps you to find catagorized doctor\n" +
                        "easily. It's both for a doctor and a patient.\n" +
                        "Download Link.......";

                intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                intent.putExtra(Intent.EXTRA_TEXT, body);

                startActivity(Intent.createChooser(intent, "Share with"));
            }
            break;
        }

        return false;
    }

    //Method for getting data from chamber adding dialog box
    @Override
    public void chamberAddingTexts(String name, String fess, Map<String, Boolean> activeDays, String address, LatLong latLng) {
        chamberList.clear();
        adapter.notifyDataSetChanged();

        DatabaseReference chamberReference = FirebaseDatabase
                .getInstance()
                .getReference("doctors_chamber_info");

        DatabaseReference doctorReference = FirebaseDatabase
                .getInstance()
                .getReference("doctors_profile_info")
                .child(mAuth.getCurrentUser().getUid());

        String key = chamberReference.push().getKey();
        String chamberDatabaseIdKeyInDoctorProfile = doctorReference.push().getKey();

        final Chamber chamber = new Chamber(name, fess, address, latLng, chamberTime, activeDays,
                mAuth.getCurrentUser().getUid(), key, chamberDatabaseIdKeyInDoctorProfile);
        Log.d(TAG, "chamberAddingTexts: " + chamber.toString());

        chamberReference
                .child(key)
                .setValue(chamber)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: chamber info data added");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: chamber data added failed");
                    }
                });

        doctorReference
                .child("chamber")
                .child(chamberDatabaseIdKeyInDoctorProfile)
                .setValue(key)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: " + chamber.getChamberDatabaseId() + " chamber added into doctor's profile");
                        onStart();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + chamber.getChamberDatabaseId() + " chamber adding into doctor's profile failed");
                    }
                });
    }

    @Override
    public void chamberEditingTexts(String name, String fees, Map<String, Boolean> activeDays, String address, LatLong latLng, int position) {
        adapter.notifyDataSetChanged();

        editButton.setVisible(false);
        deleteButton.setVisible(false);

        final Chamber chamber = invokedChamber;
        chamber.setChamberName(name);
        chamber.setChamberFees(fees);
        chamber.setChamberOpenDays(activeDays);
        chamber.setChamberAddress(address);
        chamber.setChamberLatLng(latLng);

        FirebaseDatabase
                .getInstance()
                .getReference("doctors_chamber_info")
                .child(chamber.getChamberDatabaseId())
                .setValue(chamber)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: chamber info updated successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: chamber info updated failed");
                    }
                });
    }

    @Override
    public void chamberEditingCancel() {
        editButton.setVisible(false);
        deleteButton.setVisible(false);
    }

    @Override
    public void onChamberClick(final Chamber chamber, int position) {
        Log.d(TAG, "onChamberClick: It's working");
    }

    @Override
    public void onChamberLongClick(Chamber chamber, final int position) {
        Log.d(TAG, "onChamberLongClick: Long Click is working");

        editButton.setVisible(true);
        deleteButton.setVisible(true);

        this.position = position;
        this.invokedChamber = chamber;
        Log.d(TAG, "onChamberLongClick: " + chamber.toString() + "\n\n" + position);
    }

    /**
     * Interface listener method for getting time from TimePickerDialog
     */
    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        Log.d(TAG, "onTimeSet: on doctor dash time class");
        Time tme = new Time(hour, minute, 0);
        Format formatter = new SimpleDateFormat("h:mm a");
        chamberTime = formatter.format(tme);

        testing.setTime(chamberTime);
    }

    /**
     * Interface method for
     */
    public void setChamberAddingDialogTimeSetListener(ChamberAddingDialogTimeSetListener chamberAddingDialogTimeSetListener) {
        this.testing = chamberAddingDialogTimeSetListener;
    }
}