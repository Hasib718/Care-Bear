package com.hasib.carebear.doctor.listener;

import com.hasib.carebear.doctor.container.Chamber;

public interface ChamberEventListener {
    //On chamber layout Click
    void onChamberClick(Chamber chamber, int position);

    //On chamber layout long click
    void onChamberLongClick(Chamber chamber, int position);
}
