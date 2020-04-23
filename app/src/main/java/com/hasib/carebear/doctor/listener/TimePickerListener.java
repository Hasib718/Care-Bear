package com.hasib.carebear.doctor.listener;

import android.widget.TimePicker;

public interface TimePickerListener {
    void onTimeSet(TimePicker timePicker, int hour, int minute);
}
