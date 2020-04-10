package com.hasib.carebear.doctor.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.hasib.carebear.R;
import com.hasib.carebear.doctor.listener.ChamberDialogListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ChamberAddingDialog extends AppCompatDialogFragment implements OnMapReadyCallback {
    private static final String TAG = "ChamberAddingDialog";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final LatLng SOUTH_POLE = new LatLng(-72.293924, 0.696189);

    private EditText chamberNameText;
    private EditText chamberFeesText;
    private TextView chamberTimeText;

    //A Interface for getting data into the parent activity
    private ChamberDialogListener listener;

    //For storing parent activity context;
    private Context mContext;

    //For checking Location permission current state
    private boolean mLocationPermissionsGranted = false;

    //Location client for getting device current location
    private FusedLocationProviderClient fusedLocationProviderClient;

    //Storing device current location
    private Location currentLocation;

    //Long click location address
    String longClickAddress;
    LatLng longClickLatlng;

    //Map
    private GoogleMap mapGoogle;
    private MapView mapView;

    //Geocoder
    private Geocoder geocoder;

    //Marker
    Marker markerSearchView;
    Marker markerOnMapLongClick;

    //Constructor of this class
    public ChamberAddingDialog(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //Adding layout for showing
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.layout_chamber_dialog, null);

        chamberNameText = view.findViewById(R.id.chamberNameId);
        chamberFeesText = view.findViewById(R.id.chamberFeesId);
        chamberTimeText = view.findViewById(R.id.chamberTimeId);
        mapView = view.findViewById(R.id.google_Map);

        //SearchView
        final SearchView searchView = view.findViewById(R.id.locationSearchView);

        //showing map
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }

        //calling method of getting device current location
        fetchDeviceLocation(view);

        //For searching location
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String submittedQuery = searchView.getQuery().toString();
                List<Address> addressList = null;

                try {
                    addressList = geocoder.getFromLocationName(submittedQuery, 1);

                    Log.d(TAG, "onQueryTextSubmit: found submitted location");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, "onQueryTextSubmit: "+e.getMessage());
                }
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                //Toast.makeText(MainActivity.this, latLng.toString(), Toast.LENGTH_LONG).show();

                // TODO: 10-Apr-20 Have to fix getting exception when no search result found

                mapGoogle.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(address.getLatitude(), address.getLongitude()), 15f));
                if (markerSearchView != null) {
                    markerSearchView.remove();
                    markerSearchView = mapGoogle.addMarker(new MarkerOptions()
                            .position(new LatLng(address.getLatitude(), address.getLongitude()))
                            .title(geoCoding(new LatLng(address.getLatitude(), address.getLongitude()))));
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        chamberTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePickerFragment = new TimerPickerFragment();
                timePickerFragment.setCancelable(false);
                timePickerFragment.show(fragmentManager, "Time Picker");
            }
        });

        //Dialog builder
        builder.setView(view)
                .setTitle("Enter Chamber Info")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.chamberAddingTexts(chamberNameText.getEditableText().toString(),
                                chamberFeesText.getText().toString(), longClickAddress, longClickLatlng);
                    }
                });

        return builder.create();
    }

    //On attaching fragment to the parent activity
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        //Will ask & get location permission
        getLocationPermisson();

        //Initializing Geocoder
        geocoder = new Geocoder(mContext, Locale.getDefault());

        //Initializing data sharing interface
        try {
            listener = (ChamberDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "Must implement ChamberAddingDialogListener");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Initializing Map
        MapsInitializer.initialize(mContext);
        mapGoogle = googleMap;

        //Initializing default markers
        markerSearchView = mapGoogle.addMarker(new MarkerOptions().position(SOUTH_POLE));
        markerOnMapLongClick = mapGoogle.addMarker(new MarkerOptions().position(SOUTH_POLE));

        //Setting On Map Long Click Listener for getting chamber location
        mapGoogle.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                if (markerOnMapLongClick != null) {
                    markerOnMapLongClick.remove();

                    longClickLatlng = latLng;
                    longClickAddress = geoCoding(latLng);

                    markerOnMapLongClick = mapGoogle.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(longClickAddress)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

                    Toast.makeText(mContext, "Location Saved", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Method for asking location permission
    private void getLocationPermisson() {
        Log.d(TAG, "getLocationPermisson: getting location permission");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET};

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) mContext, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            mLocationPermissionsGranted = true;
        }
    }

    //Method for checking permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called");
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if (grantResults.length > 0){
                    for (int i=0; i<grantResults.length; i++){
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permisson failed");
                            return;
                        }
                    }

                    Log.d(TAG, "onRequestPermissionsResult: permisson granted");
                    mLocationPermissionsGranted = true;
                }
            }
        }
    }

    //Method for getting device current location
    public void fetchDeviceLocation(View view){
        Log.d(TAG, "fetchDeviceLocation: getting the device current location");

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);

        try {
            if (mLocationPermissionsGranted) {
                final Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
                locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (locationTask.isSuccessful()) {
                            Log.d(TAG, "onSuccess: found location!");

                            currentLocation = (Location) locationTask.getResult();
                            //Toast.makeText(MainActivity.this, currentLocation.getLatitude()+" "+currentLocation.getLongitude(), Toast.LENGTH_LONG).show();

                            //showing device current location on map
                            mapGoogle.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15f));
                            mapGoogle.addMarker(new MarkerOptions()
                                    .position(new LatLng(location.getLatitude(), location.getLongitude()))
                                    .title(geoCoding(new LatLng(location.getLatitude(), location.getLongitude()))));
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            e.printStackTrace();
            Log.d(TAG, "fetchDeviceLocation: "+e.getMessage());
        }
    }

    //Method for getting address from co-ordinates
    public String geoCoding(LatLng latLng) {
        String addressLine = "";

        try {
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

            if (addressList != null && addressList.size() > 0) {
                Log.d(TAG, "geoCoding: "+addressList.get(0).toString());

                addressLine = addressList.get(0).getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return addressLine;
    }
}
