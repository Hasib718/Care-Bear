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
import com.hasib.carebear.MainActivity;
import com.hasib.carebear.R;

import java.util.ArrayList;
import java.util.List;

public class DoctorDashBoardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ChamberAddingDialog.ChamberAddingDialogListener {
    private static final String TAG = "DoctorDashBoardActivity";

    private FloatingActionButton chamberAddingButton;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;

    private List<String> mChamberName = new ArrayList<>();
    private List<String> mChamberAddress = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_dash_board);

        //Enable HamBurger Action Bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        chamberAddingButton = findViewById(R.id.chamberAddingButtonId);
        navigationView = findViewById(R.id.navigationId);
        drawerLayout = findViewById(R.id.drawerId);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();

        mChamberName.add("Fuck");
        mChamberName.add("Suck");
        mChamberName.add("Duck");
        mChamberAddress.add("Dhaka");
        mChamberAddress.add("Sylhet");
        mChamberAddress.add("Jessore");

        chamberAddingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: chamber adding button pressed");

                mChamberName.add("Duck");
                mChamberAddress.add("Dhaka");

                openDialog();

                adapter.notifyDataSetChanged();
            }
        });

        //Recycler view
        initChamberRecyclerView();
    }

    private void openDialog() {
        ChamberAddingDialog chamberAddingDialog = new ChamberAddingDialog(this);
        chamberAddingDialog.show(getSupportFragmentManager(), "Chamber Adding Dialog");
    }

    private void initChamberRecyclerView() {
        Log.d(TAG, "initChamberRecyclerView: init recyclerView");

        recyclerView = findViewById(R.id.doctorChamberRecyclerView);
        adapter = new RecyclerViewAdapter(this, mChamberName, mChamberAddress);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.homeMenuId : {
                Log.d(TAG, "onNavigationItemSelected: Tapped on Home Button");

                startActivity(new Intent(DoctorDashBoardActivity.this, DoctorDashBoardActivity.class));
            }
            break;

            case R.id.profileMenuId : {
                Log.d(TAG, "onNavigationItemSelected: Profile Button Pressed");

                startActivity(new Intent(DoctorDashBoardActivity.this, DoctorProfileActivity.class));
            }
            break;

            case R.id.signOutMenuId : {
                Log.d(TAG, "onNavigationItemSelected: user signing out");

                mAuth.signOut();
                finish();
                startActivity(new Intent(DoctorDashBoardActivity.this, SignInActivityForDoctor.class));
            }
            break;
        }

        return false;
    }

    @Override
    public void chamberNameTexts(String name) {

    }

//    private void getLocationPermisson() {
//        Log.d(TAG, "getLocationPermisson: getting location permission");
//        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET};
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
//        } else {
//            mLocationPermissionsGranted = true;
//            initMap();
//        }
//    }
//
//    private void initMap() {
//        Log.d(TAG, "initMap: initializing map");
//        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.google_Map);
//        supportMapFragment.getMapAsync(DoctorDashBoardActivity.this);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        Log.d(TAG, "onRequestPermissionsResult: called");
//        mLocationPermissionsGranted = false;
//
//        switch (requestCode) {
//            case LOCATION_PERMISSION_REQUEST_CODE:{
//                if (grantResults.length > 0){
//                    for (int i=0; i<grantResults.length; i++){
//                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
//                            mLocationPermissionsGranted = false;
//                            Log.d(TAG, "onRequestPermissionsResult: permisson failed");
//                            return;
//                        }
//                    }
//
//                    Log.d(TAG, "onRequestPermissionsResult: permisson granted");
//                    mLocationPermissionsGranted = true;
//                    initMap();
//                }
//            }
//        }
//    }

}