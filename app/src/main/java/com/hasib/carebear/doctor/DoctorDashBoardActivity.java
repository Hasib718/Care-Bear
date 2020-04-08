package com.hasib.carebear.doctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hasib.carebear.MainActivity;
import com.hasib.carebear.R;

import java.util.ArrayList;
import java.util.List;

public class DoctorDashBoardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ChamberAddingDialog.ChamberAddingDialogListener {
    private static final String TAG = "DoctorDashBoardActivity";

    //Floating Action "+" Bar
    private FloatingActionButton chamberAddingButton;

    //Main parent drawer layout
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    //Navigation Menu
    private NavigationView navigationView;

    //Container for storing chamberName & address
    private List<String> mChamberName = new ArrayList<>();
    private List<String> mChamberAddress = new ArrayList<>();

    //For showing chamber details to user
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;

    //Firebase authenticator
    private FirebaseAuth mAuth;

    //Firebase realtime database
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_dash_board);

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

        //Firebase authenticator initialization
        mAuth = FirebaseAuth.getInstance();

        //Firebase realtime database initialization
        databaseReference = FirebaseDatabase.getInstance().getReference("doctors_profile_info/chamber_info");

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
        adapter = new RecyclerViewAdapter(this, mChamberName, mChamberAddress);

        //setting adapter to recycler view
        recyclerView.setAdapter(adapter);

        //Recycler view layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    //Method for controlling navigation view close or open hamburger button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        //On navigation view item pressed
        switch (item.getItemId()) {
            case R.id.homeMenuId : {
                Log.d(TAG, "onNavigationItemSelected: Tapped on Home Button");

                //Intenting to this activity
                startActivity(new Intent(DoctorDashBoardActivity.this, DoctorDashBoardActivity.class));
            }
            break;

            case R.id.profileMenuId : {
                Log.d(TAG, "onNavigationItemSelected: Profile Button Pressed");

                //Intenting to doctor's profile activity
                startActivity(new Intent(DoctorDashBoardActivity.this, DoctorProfileActivity.class));
            }
            break;

            case R.id.signOutMenuId : {
                Log.d(TAG, "onNavigationItemSelected: user signing out");

                //Signing out current user
                mAuth.signOut();
                finish();

                //Intenting to sign in activity for doctor
                startActivity(new Intent(DoctorDashBoardActivity.this, SignInActivityForDoctor.class));
            }
            break;
        }

        return false;
    }

    //Method for getting data from chamber adding dialog box
    @Override
    public void chamberNameTexts(String name, String address) {
        mChamberName.add(name);
        mChamberAddress.add(address);

        //Notifying recycler view for adapter on data change
        adapter.notifyDataSetChanged();
    }
}