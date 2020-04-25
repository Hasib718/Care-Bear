package com.tinvodai.carebear.doctor.fragment;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.tinvodai.carebear.doctor.listener.TimePickerListener;

import java.util.Calendar;

public class TimerPickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private TimePickerListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (TimePickerListener) context;
        } catch (Exception e) {
            throw new ClassCastException(getActivity().toString() +
                    "must implements TimePickerListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getContext()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
       listener.onTimeSet(view, hourOfDay, minute);
    }

}
