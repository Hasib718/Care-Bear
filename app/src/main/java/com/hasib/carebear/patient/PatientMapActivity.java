package com.hasib.carebear.patient;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hasib.carebear.MainActivity;
import com.hasib.carebear.R;
import com.hasib.carebear.doctor.adapter.ChamberRecyclerViewAdapter;
import com.hasib.carebear.doctor.container.Chamber;
import com.hasib.carebear.doctor.container.Doctor;
import com.hasib.carebear.patient.authentication.SignInActivityForPatient;
import com.hasib.carebear.support.CareBear;
import com.hasib.carebear.support.FeedBackActivity;
import com.hasib.carebear.support.LatLong;
import com.hasib.carebear.support.Tools;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PatientMapActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "PatientMapActivity";
    private static final int REQUEST_CHECK_SETTINGS = 8001;

    private GoogleMap mMap;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Marker currentLocationMarker, marker;
    private LatLng currentLocation;

    private final Map<Marker, String> queryMarker = new HashMap<>();

    private DatabaseReference databaseReference;
    private GeoFire geoFire;
    private GeoQuery geoQuery;
    private GeoQueryEventListener listener;

    //Main parent drawer layout
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    //Navigation Menu
    private NavigationView navigationView;
    private View navHeader;

    private SearchView searchView;
    private Geocoder geocoder;

    private FirebaseAuth mAuth;

    private BottomSheetBehavior bottomSheetBehavior;

    private TextView chamberNameShow, doctorNameShow, doctorDegreeShow, chamberAddressShow, chamberFeeShow, chamberOpenDaysShow;
    private ImageView doctorImageShow;

    private FloatingActionButton searchButton;
    private final List<Chamber> chambers = new ArrayList<>();
    private RecyclerView recyclerView;
    private ChamberRecyclerViewAdapter adapter;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_map);
        this.setTitle("Nearby Chambers");

        //Enable HamBurger Action Bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().hide();

        initBottomSheet();

        //Navigation panel control
        navigationView = findViewById(R.id.navigationId);
        drawerLayout = findViewById(R.id.drawerId);
        toggle = new ActionBarDrawerToggle(PatientMapActivity.this, drawerLayout, R.string.nav_open, R.string.nav_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //Navigation View on click listener
        navigationView.setNavigationItemSelectedListener(this);


        searchView = findViewById(R.id.locationSearchView);
        geocoder = new Geocoder(this, Locale.getDefault());

        //Firebase authentication
        mAuth = FirebaseAuth.getInstance(CareBear.getPatientFirebaseApp());

        Dexter.withContext(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                        buildLocationRequest();
                        buildLocationCallBack();
                        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(PatientMapActivity.this);

                        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                .findFragmentById(R.id.google_Map);
                        mapFragment.getMapAsync(PatientMapActivity.this);

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        startActivity(new Intent(PatientMapActivity.this, MainActivity.class));

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();


        //calling method of getting device current location
        final LocationManager manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        findViewById(R.id.chamberSearchButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                Intent intent = new Intent(PatientMapActivity.this, DoctorSearch.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.deviceCurrentLocationButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    buildAlertMessageNoGps();
                } else {
                    gettingCurrentLocationButton();
                }
            }
        });

        findViewById(R.id.patientMapHamBurgerIcon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        onLocationSearch();
    }

    private void initBottomSheet() {
        // get the bottom sheet view
        LinearLayout llBottomSheet = findViewById(R.id.bottom_sheet);

        // init the bottom sheet behavior
        bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);

        // change the state of the bottom sheet
         bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        llBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        // set callback for changes
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // TODO: 01-May-20 have to add back button for bottom sheet 
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        chamberNameShow = findViewById(R.id.chamberNameShow);
        doctorNameShow = findViewById(R.id.doctorNameShow);
        doctorDegreeShow = findViewById(R.id.doctorDegreeShow);
        chamberAddressShow = findViewById(R.id.chamberAddressShow);
        chamberFeeShow = findViewById(R.id.chamberFeeShow);
        chamberOpenDaysShow = findViewById(R.id.chamberOpenDaysShow);
        doctorImageShow = findViewById(R.id.doctorImageShow);
        recyclerView = findViewById(R.id.otherChambersShowRecyclerView);
        adapter = new ChamberRecyclerViewAdapter(this)
                .setChamberList(chambers)
                .setComingClass(PatientMapActivity.class.getSimpleName());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void gettingCurrentLocationButton() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(PatientMapActivity.this);

        try {
            final Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();

            locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (locationTask.isSuccessful()) {

                        //showing device current location on map
                        if (location != null) {
                            Log.d(TAG, "onSuccess: found location!");

                            if (currentLocationMarker != null) {
                                currentLocationMarker.remove();
                            }

                            currentLocation = new LatLng(location.getLatitude(),
                                    location.getLongitude());

                            settingGeoFire(currentLocation);

                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15f));
                            currentLocationMarker = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(location.getLatitude(), location.getLongitude()))
                                    .title(LatLong.geoCoding(PatientMapActivity.this, new LatLng(location.getLatitude(), location.getLongitude()))));
                        } else {
                            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
                        }
                    }
                }
            });
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void onLocationSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

                String submittedQuery = searchView.getQuery().toString();
                List<Address> addressList = null;

                try {
                    addressList = geocoder.getFromLocationName(submittedQuery, 1);

                    Log.d(TAG, "onQueryTextSubmit: found submitted location");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, "onQueryTextSubmit: " + e.getMessage());
                }
                Address address = null;
                try {
                    address = addressList.get(0);

                    settingGeoFire(new LatLng(address.getLatitude(), address.getLongitude()));

                    if (currentLocationMarker != null) {
                        currentLocationMarker.remove();
                    }

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(address.getLatitude(), address.getLongitude()), 15f));
                    currentLocationMarker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(address.getLatitude(), address.getLongitude()))
                            .title(LatLong.geoCoding(PatientMapActivity.this, address.getLatitude(), address.getLongitude())));

                } catch (Exception e) {
                    Toast.makeText(PatientMapActivity.this, "No Place Found!\nTry to search with additional info\nExample: Gulshan, Dhaka", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                return false;
            }
        });
    }

    private void settingGeoFire(final LatLng currentLocation) {
        databaseReference = FirebaseDatabase.getInstance().getReference("chambers_locations");
        databaseReference.keepSynced(true);
        geoFire = new GeoFire(databaseReference);

        geoQuery = geoFire.queryAtLocation(new GeoLocation(currentLocation.latitude, currentLocation.longitude), 0.5);

        Iterator mark = queryMarker.keySet().iterator();

        while (mark.hasNext()) {
            Marker marker1 = (Marker) mark.next();
            marker1.remove();
            mark.remove();
        }
        queryMarker.clear();

        listener = new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                //Log.d(TAG, "onKeyEntered: "+String.format("Key %s entered the search area at [%f,%f]", key, location.latitude, location.longitude));

                marker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(location.latitude, location.longitude))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

                queryMarker.put(marker, key);
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                Log.d(TAG, "onGeoQueryReady: " + queryMarker.toString());
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        Log.d(TAG, "onMarkerClick: " + queryMarker.get(marker));

                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                        FirebaseDatabase
                                .getInstance()
                                .getReference("doctors_chamber_info")
                                .child(queryMarker.get(marker))
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Chamber chamber = dataSnapshot.getValue(Chamber.class);

                                        Log.d(TAG, "onDataChange: " + chamber.toString());

                                        chamberNameShow.setText(chamber.getChamberName());
                                        chamberAddressShow.setText(chamber.getChamberAddress());
                                        chamberFeeShow.setText(chamber.getChamberFees() + " Taka");
                                        addDoctorInfo(chamber.getDoctorUserProfileId());
                                        StringBuilder stringBuilder = new StringBuilder();

                                        for (Map.Entry<String, Boolean> data : chamber.getChamberOpenDays().entrySet()) {
                                            if (data.getValue().equals(Boolean.TRUE)) {
                                                stringBuilder.append(data.getKey() + ",  " + chamber.getChamberTime() + "\n");
                                            }
                                        }
                                        chamberOpenDaysShow.setText(stringBuilder);

                                        fetchingChamberDatabasekeys(chamber.getDoctorUserProfileId());
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                        return false;
                    }
                });
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        };

        geoQuery.addGeoQueryEventListener(listener);
    }

    private void addDoctorInfo(String doctorUserProfileId) {
        FirebaseDatabase
                .getInstance()
                .getReference("doctors_profile_info")
                .child(doctorUserProfileId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                        Doctor doctor = dataSnapshot.getValue(Doctor.class);
                        Log.d(TAG, "onDataChange: " + doctor.toString());
                        doctorNameShow.setText(doctor.getFullName());
                        doctorDegreeShow.setText(doctor.getCheckBoxInfo());

                        Glide.with(PatientMapActivity.this)
                                .load(doctor.getDoctorImageUrl())
                                .placeholder(R.drawable.bear)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .into(doctorImageShow);
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {

                    }
                });
    }

    private void buildLocationCallBack() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (mMap != null) {
                    if (currentLocationMarker != null) {
                        currentLocationMarker.remove();
                    }

                    currentLocation = new LatLng(locationResult.getLastLocation().getLatitude(),
                            locationResult.getLastLocation().getLongitude());

                    settingGeoFire(currentLocation);

                    currentLocationMarker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(locationResult.getLastLocation().getLatitude(),
                                    locationResult.getLastLocation().getLongitude()))
                            .title(LatLong.geoCoding(PatientMapActivity.this, locationResult.getLastLocation().getLatitude(),
                                    locationResult.getLastLocation().getLongitude())));

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocationMarker.getPosition(), 15.0f));
                }
            }
        };
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10f);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = Tools.configActivityMaps(googleMap);

        if (fusedLocationProviderClient != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }
    }

    @Override
    protected void onStop() {
        if(geoQuery != null && fusedLocationProviderClient !=null) {
            geoQuery.removeGeoQueryEventListener(listener);
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
        super.onStop();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(PatientMapActivity.this, SignInActivityForPatient.class));
        }

        initNavigationHeader(mAuth.getCurrentUser());
    }

    private void initNavigationHeader(final FirebaseUser user) {
//        MaterialTextView profileName = navHeader.findViewById(R.id.profileNameId);
//        MaterialTextView profileEmail = navHeader.findViewById(R.id.profileEmailId);
//        final ImageView profileImage = navHeader.findViewById(R.id.profileImageId);
//
//        Log.d(TAG, "initNavigationHeader: " + user.getPhotoUrl());
//
//        Glide.with(PatientMapActivity.this)
//                .load(user.getPhotoUrl())
//                .diskCacheStrategy(DiskCacheStrategy.DATA)
//                .fitCenter()
//                .circleCrop()
//                .into(profileImage);
//
//        profileName.setText(user.getDisplayName());
//        profileEmail.setText(user.getEmail());
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_chamber_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        //On navigation view item pressed
        switch (item.getItemId()) {
            case R.id.homeMenuId: {
                Log.d(TAG, "onNavigationItemSelected: Tapped on Home Button");

                //Intenting to this activity
                startActivity(new Intent(PatientMapActivity.this, PatientMapActivity.class));
            }
            break;

            case R.id.profileMenuId: {
                Log.d(TAG, "onNavigationItemSelected: Profile Button Pressed");
                Toast toast = Toast.makeText(getApplicationContext(), "profile", Toast.LENGTH_SHORT);
                toast.show();

                //Intenting to doctor's profile activity
//                startActivity(new Intent(PatientMapActivity.this, DoctorProfileActivity.class));
            }
            break;

            case R.id.signOutMenuId: {
                Toast toast = Toast.makeText(getApplicationContext(), "signOut", Toast.LENGTH_SHORT);
                toast.show();
                Log.d(TAG, "onNavigationItemSelected: user signing out");

                //Signing out current user
                mAuth.signOut();
                finish();

                //Intenting to sign in activity for doctor
                startActivity(new Intent(PatientMapActivity.this, SignInActivityForPatient.class));
            }
            break;

            case R.id.appointmentsId: {
                Log.d(TAG, "onNavigationItemSelected: user signing out");

                startActivity(new Intent(PatientMapActivity.this, PatientAppointmentActivity.class));
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

            case R.id.contactMenuId: {
                startActivity(new Intent(PatientMapActivity.this, FeedBackActivity.class));
            }
            break;
        }

        return false;
    }

    private void buildAlertMessageNoGps() {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(PatientMapActivity.this)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(PatientMapActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }

    private void fetchingChamberDatabasekeys(String id) {
        DatabaseReference reference = FirebaseDatabase
                .getInstance()
                .getReference("doctors_profile_info")
                .child(id)
                .child("chamber");
        reference.keepSynced(true);

        adapter.notifyDataSetChanged();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, String> hashMap = (HashMap<String, String>) dataSnapshot.getValue();

                try {
                    Log.d(TAG, "onDataChange: " + hashMap.values().toString());

                    for (Map.Entry<String, String> data : hashMap.entrySet()) {
                        fetchingChamberData(data.getValue());
                    }
                } catch (Exception e) {
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
        DatabaseReference reference = FirebaseDatabase
                .getInstance()
                .getReference("doctors_chamber_info")
                .child(s);

        reference.keepSynced(true);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Chamber chamber = dataSnapshot.getValue(Chamber.class);

                Log.d(TAG, "onDataChange: " + chamber.toString());

                chambers.add(chamber);
                adapter.notifyDataSetChanged();

                Log.d(TAG, "onDataChange: chamber " + chamber.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
