package com.hasib.carebear.doctor.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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
import com.hasib.carebear.doctor.DoctorDashBoardActivity;
import com.hasib.carebear.doctor.listener.ChamberDialogListener;
import com.hasib.carebear.doctor.listener.ChamberAddingDialogTimeSetListener;
import com.hasib.carebear.support.DayPicker;
import com.hasib.carebear.support.LatLong;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ChamberAddingDialog extends AppCompatDialogFragment implements OnMapReadyCallback {
    private static final String TAG = "ChamberAddingDialog";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final LatLng SOUTH_POLE = new LatLng(-72.293924, 0.696189);
    private static final int REQUEST_CHECK_SETTINGS = 1001;

    private EditText chamberNameText;
    private EditText chamberFeesText;
    private TextView chamberTimeText;
    private DayPicker dayPicker;

    //A Interface for getting data into the parent activity
    private ChamberDialogListener listener;

    //For storing parent activity context;
    private Context mContext;

    //For checking Location permission current state
    private boolean mLocationPermissionsGranted = false;

    //Location client for getting device current location
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

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

    //Chamber Time
    private String chamberTime;


    //Constructor of this class
    public ChamberAddingDialog(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //Adding layout for showing
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.layout_chamber_dialog, null);


        chamberNameText = view.findViewById(R.id.chamberNameId);
        chamberFeesText = view.findViewById(R.id.chamberFeesId);
        chamberTimeText = view.findViewById(R.id.chamberTimeId);
        dayPicker = new DayPicker(view.findViewById(R.id.daypicker));

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
        final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if(!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        } else {
            fetchDeviceLocation();
        }

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
                    Log.d(TAG, "onQueryTextSubmit: " + e.getMessage());
                }
                Address address = null;
                try {
                    address = addressList.get(0);

                    mapGoogle.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(address.getLatitude(), address.getLongitude()), 15f));

                } catch (Exception e) {
                    Toast.makeText(mContext, "No Place Found!\nTry to search with additional info\nExample: Gulshan, Dhaka", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        /**
         * Fragment support manager for TimePickerDialog
         */
        final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        /**
         * TimePickerDialog for choosing chamber time
         */
        chamberTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePickerFragment = new TimerPickerFragment();
                timePickerFragment.setCancelable(false);
                timePickerFragment.show(fragmentManager, "Time Picker");
            }
        });

        /**
         * Setting time on ChamberTimeText (EditText)
         */
        ((DoctorDashBoardActivity) getActivity()).setChamberAddingDialogTimeSetListener(new ChamberAddingDialogTimeSetListener() {
            @Override
            public void setTime(String time) {
                chamberTimeText.setText(time);
            }
        });

        //Dialog builder
        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle("Enter Chamber Info")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if (longClickLatlng != null) {
                            listener.chamberAddingTexts(chamberNameText.getEditableText().toString(),
                                    chamberFeesText.getText().toString(), dayPicker.getMarkedDays(), longClickAddress,
                                    LatLong.castLatLngToCustomLatLongClass(longClickLatlng));

                            dialog.dismiss();
                        } else {
                            Toast.makeText(mContext, "You Must Select Chamber Location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        return dialog;
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
                    longClickAddress = LatLong.geoCoding(mContext, latLng);

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
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permisson failed");
                            return;
                        }
                    }

                    Log.d(TAG, "onRequestPermissionsResult: permisson granted");
                    mLocationPermissionsGranted = true;
                    fetchDeviceLocation();
                }
            }
        }
    }

    //Method for getting device current location
    public void fetchDeviceLocation() {
        Log.d(TAG, "fetchDeviceLocation: getting the device current location");

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(20 * 1000);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                Log.d(TAG, "onLocationResult: "+locationResult.getLocations().get(0).getLatitude()+" "+locationResult.getLocations().get(0).getLongitude());
                mapGoogle.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(locationResult.getLocations().get(0).getLatitude(), locationResult.getLocations().get(0).getLongitude()), 15f));
                mapGoogle.addMarker(new MarkerOptions()
                        .position(new LatLng(locationResult.getLocations().get(0).getLatitude(), locationResult.getLocations().get(0).getLongitude()))
                        .title(LatLong.geoCoding(mContext, new LatLng(locationResult.getLocations().get(0).getLatitude(), locationResult.getLocations().get(0).getLongitude()))));

                if (fusedLocationProviderClient != null) {
                    fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                }
            }
        };

        try {
            if (mLocationPermissionsGranted) {
                final Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();

                locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (locationTask.isSuccessful()) {
                            currentLocation = (Location) locationTask.getResult();
                            //Toast.makeText(MainActivity.this, currentLocation.getLatitude()+" "+currentLocation.getLongitude(), Toast.LENGTH_LONG).show();

                            //showing device current location on map
                            if (location != null) {
                                Log.d(TAG, "onSuccess: found location!");

                                mapGoogle.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15f));
                                mapGoogle.addMarker(new MarkerOptions()
                                        .position(new LatLng(location.getLatitude(), location.getLongitude()))
                                        .title(LatLong.geoCoding(mContext, new LatLng(location.getLatitude(), location.getLongitude()))));
                            } else {
                                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
                            }
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            e.printStackTrace();
            Log.d(TAG, "fetchDeviceLocation: " + e.getMessage());
        }
    }

    private void buildAlertMessageNoGps() {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(mContext)
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
                            status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });    }

}
