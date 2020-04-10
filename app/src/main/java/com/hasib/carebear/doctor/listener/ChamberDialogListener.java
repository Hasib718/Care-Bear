package com.hasib.carebear.doctor.listener;

import com.google.android.gms.maps.model.LatLng;

public interface ChamberDialogListener {
    /*
    * This interface passed data to where it's implemented
    * */
    void chamberAddingTexts(String name, String fees, String Address, LatLng latLng);
    /*
    * This interface passed editing data to where it's implemented
    * */
    void chamberEditingTexts(String name, String fees, String address, LatLng latLng, int position);
}
