package com.hasib.carebear.doctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.hasib.carebear.R;

import java.util.ArrayList;
import java.util.List;

public class DoctorDashBoardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "DoctorDashBoardActivity";

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private List<String> mChamberName = new ArrayList<>();
    private List<String> mChamberAddress = new ArrayList<>();

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_dash_board);

        //Enable HamBurger Action Bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        //Recycler view
        initChamberRecyclerView();
    }

    private void initChamberRecyclerView() {
        Log.d(TAG, "initChamberRecyclerView: init recyclerView");

        RecyclerView recyclerView = findViewById(R.id.doctorChamberRecyclerView);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, mChamberName, mChamberAddress);
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
}