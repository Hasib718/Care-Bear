package com.hasib.carebear.doctor.listener;

import com.hasib.carebear.support.LatLong;

import java.util.Map;

public interface ChamberDialogListener {
    /*
    * This interface passed data to where it's implemented
    * */
    void chamberAddingTexts(String name, String fees, Map<String, Boolean> activeDays, String Address, LatLong latLng);
    /*
    * This interface passed editing data to where it's implemented
    * */
    void chamberEditingTexts(String name, String fees, Map<String, Boolean> activeDays, String address, LatLong latLng, int position);
    /*
    * This interface is for invoking edit & delete option on edit cancellation
    * */
    void chamberEditingCancel();
}
