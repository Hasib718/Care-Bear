package com.hasib.carebear.doctor.listener;

import com.google.android.gms.maps.model.LatLng;

import java.util.Map;

public interface ChamberDialogListener {
    /*
    * This interface passed data to where it's implemented
    * */
    void chamberAddingTexts(String name, String fees, Map<String, Boolean> activeDays, String Address, LatLng latLng);
    /*
    * This interface passed editing data to where it's implemented
    * */
    void chamberEditingTexts(String name, String fees, Map<String, Boolean> activeDays, String address, LatLng latLng, int position);
}
