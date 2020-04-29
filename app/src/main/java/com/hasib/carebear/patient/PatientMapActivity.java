package com.hasib.carebear.patient;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryDataEventListener;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hasib.carebear.MainActivity;
import com.hasib.carebear.R;
import com.hasib.carebear.support.LatLong;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class PatientMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "PatientMapActivity";

    private FloatingActionButton chamberSearchButton;

    private GoogleMap mMap;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Marker currentLocationMarker;
    private LatLng currentLocation;

    private DatabaseReference databaseReference;
    private GeoFire geoFire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_map);

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

        chamberSearchButton = findViewById(R.id.chamberSearchButton);

    }

    private void settingGeoFire(LatLng currentLocation) {
        databaseReference = FirebaseDatabase.getInstance().getReference("chambers_locations");
        databaseReference.keepSynced(true);
        geoFire = new GeoFire(databaseReference);

        geoFire.setLocation("-"+"M61CyB98YJhtsoRhktG", new GeoLocation(
                23.776050619805073,
                90.39685528725384));

        geoFire.setLocation("-"+"M61EhHEtIFIKLccFC3e", new GeoLocation(
                23.785938766926794,
                90.40099561214447));

        geoFire.setLocation("-"+"M61F3uiO_ov79NrfxUR", new GeoLocation(
                23.772922852375014,
                90.4044707491994));

        geoFire.setLocation("-"+"M613WUT9T8goR_pPZ7o", new GeoLocation(
                23.778293459498947,
                90.40583934634924));

        geoFire.setLocation("-"+"M614LBkRT-X6HH9GFU6", new GeoLocation(
                23.78299900168265,
                90.40205541998148));

        geoFire.setLocation("-"+"M614UWJqfQ--c8DUxs7", new GeoLocation(
                23.781156935816906,
                90.40634695440531));

         geoFire.setLocation("-"+"M616mz6WplAjg2gIJk4", new GeoLocation(
                 23.77850055970865,
                 90.40589097887278));

         geoFire.setLocation("-"+"M616v-nZE_-qJlXa9Wm", new GeoLocation(
                 23.781597205431698,
                 90.39984963834286));

          geoFire.setLocation("-"+"M6172fkikosDtRuRYD4", new GeoLocation(
                  23.777686577281365,
                  90.40195818990469));

          geoFire.setLocation("-"+"M60u8nv3YloCcS4xvEV", new GeoLocation(
                  23.770718274227473,
                  90.40450595319271));

          geoFire.setLocation("-"+"M60uI83zE8S7g_WOF7N", new GeoLocation(
                  23.793352006171553,
                  90.41464939713477));

           geoFire.setLocation("-"+"M60uXUNxe_6yaxfknZR", new GeoLocation(
                   23.78116307198903,
                   90.42242847383024));

            geoFire.setLocation("-"+"M60udZ7ERT0xdF5LAgf", new GeoLocation(
                    23.777979893812958,
                    90.39379689842463));

            geoFire.setLocation("-"+"M60uj7w2vFJ0nfMZwtI", new GeoLocation(
                    23.781010281217007,
                    90.42679443955421));

            geoFire.setLocation("-"+"M6-JBp9uXrwSVNEWCPR", new GeoLocation(
                    23.79051334139877,
                    90.4063516482711));

            geoFire.setLocation("-"+"M6-JFONGJdrQl7SSecN", new GeoLocation(
                    23.787133123441066,
                    90.4086848348379));

            geoFire.setLocation("-"+"M6-W9tert3MBOUiMrad", new GeoLocation(
                    23.78702237057578,
                    90.4093486815691));

            geoFire.setLocation("-"+"M6-Whldup4pgieasm1D", new GeoLocation(
                    23.81198294312523,
                    90.4148968309164));

              geoFire.setLocation("-"+"M64ObjosOoG0ZmMkP3r", new GeoLocation(
                      24.89977763794124,
                      91.8740852549672));

               geoFire.setLocation("-"+"M60OlXAIG2tmTk6Ua4u", new GeoLocation(
                       23.781427847540296,
                       90.40363758802413));

               geoFire.setLocation("-"+"M617wliqqAiuC5Q4lWP", new GeoLocation(
                       23.776229802947835,
                       90.39877474308014));

               geoFire.setLocation("-"+"M61835cSD2QBDibx_hy", new GeoLocation(
                       23.776701077980373,
                       90.40164638310672));

               geoFire.setLocation("-"+"M618QbTirPlf2TTueBw", new GeoLocation(
                       23.784027098714894,
                       90.40423672646284));

               geoFire.setLocation("-"+"M619Qi8YDQ6_P5ngtVF", new GeoLocation(
                       23.78073630052759,
                       90.41059222072363));

               geoFire.setLocation("-"+"M619WkfQy1ccI3e_91q", new GeoLocation(
                       23.78083754765812,
                       90.39571166038513));

               geoFire.setLocation("-"+"M619ewH4iUzNjuFWnEd", new GeoLocation(
                       23.784332366187694,
                       90.40979359298944));

               geoFire.setLocation("-"+"M60sqrISUoSsCU1LO9f", new GeoLocation(
                       23.782331087078678,
                       90.40385719388723));

               geoFire.setLocation("-"+"M60t58LdgXMYm9Rvez0", new GeoLocation(
                       23.79248412313357,
                       90.40803708136082));

               geoFire.setLocation("-"+"M60tJjRMWbGjLR8MAii", new GeoLocation(
                       23.778275050575456,
                       90.40576960891485));

               geoFire.setLocation("-"+"M60t_Cm-0bq9bX1N6je", new GeoLocation(
                       23.790516409264043,
                       90.40881726890802));

               geoFire.setLocation("-"+"M60opr23pO7Dhp0_qnG", new GeoLocation(
                       23.78066389344043,
                       90.42480826377867));

               geoFire.setLocation("-"+"M60pVS1Uce5c1dRVsBe", new GeoLocation(
                       23.79044584834434,
                       90.42575642466545));

               geoFire.setLocation("-"+"M60pjqPajY_rHOwkQ2N", new GeoLocation(
                       23.78063382607883,
                       90.41019156575203));

               geoFire.setLocation("-"+"M60q3pqhh0S-caQibyJ", new GeoLocation(
                       23.78838177109923,
                       90.40236353874207));

        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(currentLocation.latitude, currentLocation.longitude), 2.0);

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Log.d(TAG, "onKeyEntered: "+String.format("Key %s entered the search area at [%f,%f]", key, location.latitude, location.longitude));

                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(location.latitude, location.longitude))
                        .title(LatLong.geoCoding(PatientMapActivity.this, location.latitude, location.longitude))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

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

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocationMarker.getPosition(), 12.0f));
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
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);

        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }
    }

    @Override
    protected void onStop() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        super.onStop();
    }
}
