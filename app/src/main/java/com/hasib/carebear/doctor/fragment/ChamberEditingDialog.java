package com.hasib.carebear.doctor.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hasib.carebear.R;
import com.hasib.carebear.doctor.DoctorDashBoardActivity;
import com.hasib.carebear.doctor.container.Chamber;
import com.hasib.carebear.doctor.listener.ChamberAddingDialogTimeSetListener;
import com.hasib.carebear.doctor.listener.ChamberDialogListener;
import com.hasib.carebear.support.DayPicker;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ChamberEditingDialog extends AppCompatDialogFragment implements OnMapReadyCallback {
    private static final String TAG = "ChamberEditingDialog";
    private static final LatLng SOUTH_POLE = new LatLng(-72.293924, 0.696189);

    private EditText chamberNameText;
    private EditText chamberFeesText;
    private TextView chamberTimeText;
    private DayPicker dayPicker;

    //A Interface for getting data into the parent activity
    private ChamberDialogListener listener;

    //For storing parent activity context;
    private Context mContext;
    private Chamber chamber;
    private int position;

    //Storing device current location
    private Location currentLocation;

    //Long click location address
    private String longClickAddress = "";
    private LatLng longClickLatlng;

    //Map
    private GoogleMap mapGoogle;
    private MapView mapView;

    //Geocoder
    private Geocoder geocoder;

    //Marker
    private Marker markerSearchView;
    private Marker markerOnMapLongClick;

    //Constructor of this class
    public ChamberEditingDialog(Context mContext, Chamber chamber, int position) {
        this.mContext = mContext;
        this.chamber = chamber;
        this.position = position;
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
        dayPicker = new DayPicker(view.findViewById(R.id.daypicker), chamber.getChamberOpenDays());

        mapView = view.findViewById(R.id.google_Map);

        //Setting last saved chamber info
        chamberNameText.setText(chamber.getChamberName());
        chamberFeesText.setText(chamber.getChamberFees());
        chamberTimeText.setText(chamber.getChamberTime());
        dayPicker.setMarkedDays();

        //SearchView
        final SearchView searchView = view.findViewById(R.id.locationSearchView);

        //showing map
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
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
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                //Toast.makeText(MainActivity.this, latLng.toString(), Toast.LENGTH_LONG).show();

                // TODO: 10-Apr-20 have to fix getting exception when no search result found

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
        builder.setView(view)
                .setTitle("Enter Chamber Info")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (longClickAddress.isEmpty()) {
                            longClickAddress = chamber.getChamberAddress();
                        }
                        if (longClickLatlng == null) {
                            longClickLatlng = chamber.getChamberLatLng();
                        }
                        Log.d(TAG, "onClick: chamber active "+dayPicker.getMarkedDays().toString());
                        listener.chamberEditingTexts(chamberNameText.getEditableText().toString(),
                                chamberFeesText.getText().toString(), dayPicker.getMarkedDays(), longClickAddress, longClickLatlng, position);
                    }
                });

        return builder.create();
    }

    //On attaching fragment to the parent activity
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        //Initializing Geocoder
        geocoder = new Geocoder(mContext, Locale.getDefault());

        //Initializing data sharing interface
        try {
            listener = (ChamberDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "Must implement ChamberDialogListener");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Initializing Map
        MapsInitializer.initialize(mContext);
        mapGoogle = googleMap;

        lastSavedLocation();

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

    //Method for getting device current location
    public void lastSavedLocation() {
        Log.d(TAG, "lastSavedLocation: getting the device current location");

        if (chamber.getChamberLatLng() != null) {
            mapGoogle.animateCamera(CameraUpdateFactory.newLatLngZoom(chamber.getChamberLatLng(), 15f));
            mapGoogle.addMarker(new MarkerOptions()
                    .position(chamber.getChamberLatLng())
                    .title(geoCoding(chamber.getChamberLatLng())));
        }
    }

    //Method for getting address from co-ordinates
    public String geoCoding(LatLng latLng) {
        String addressLine = "";

        try {
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

            if (addressList != null && addressList.size() > 0) {
                Log.d(TAG, "geoCoding: " + addressList.get(0).toString());

                addressLine = addressList.get(0).getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return addressLine;
    }
}
