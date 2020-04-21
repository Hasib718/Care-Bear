package com.hasib.carebear.support;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import androidx.core.util.Pair;

import com.hasib.carebear.R;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


public class DayPicker {
    private static final String TAG = "DayPicker";

    private View view;

    private LinearLayout toggleButtonParent;
    private ToggleButton toggleSunday;
    private ToggleButton toggleMonday;
    private ToggleButton toggleTuesday;
    private ToggleButton toggleWednesday;
    private ToggleButton toggleThursday;
    private ToggleButton toggleFriday;
    private ToggleButton toggleSaturday;

    private Map<String, Boolean> markedDays = new LinkedHashMap<>();
    private Map<String, Boolean> customMarkedDays = new LinkedHashMap<>();

    private static final String SUNDAY = "S";
    private static final String MONDAY = "M";
    private static final String TUESDAY = "T";
    private static final String WEDNESDAY = "W";
    private static final String THURSDAY = "Th";
    private static final String FRIDAY = "F";
    private static final String SATURDAY = "Sa";

    private Map<Integer, Pair<String, Boolean>> trackedDays = new HashMap<>();

    public static final Integer TRUE_SUNDAY_ID = 711;
    public static final Integer TRUE_MONDAY_ID = 712;
    public static final Integer TRUE_TUESDAY_ID = 713;
    public static final Integer TRUE_WEDNESDAY_ID = 714;
    public static final Integer TRUE_THURSDAY_ID = 715;
    public static final Integer TRUE_FRIDAY_ID = 716;
    public static final Integer TRUE_SATURDAY_ID = 717;

    public static final Integer FALSE_SUNDAY_ID = 700;
    public static final Integer FALSE_MONDAY_ID = 702;
    public static final Integer FALSE_TUESDAY_ID = 703;
    public static final Integer FALSE_WEDNESDAY_ID = 704;
    public static final Integer FALSE_THURSDAY_ID = 705;
    public static final Integer FALSE_FRIDAY_ID = 706;
    public static final Integer FALSE_SATURDAY_ID = 707;


    private static final Pair<String, Boolean> TRUE_SUNDAY = Pair.create(SUNDAY, Boolean.TRUE);
    private static final Pair<String, Boolean> TRUE_MONDAY = Pair.create(MONDAY, Boolean.TRUE);
    private static final Pair<String, Boolean> TRUE_TUESDAY = Pair.create(TUESDAY, Boolean.TRUE);
    private static final Pair<String, Boolean> TRUE_WEDNESDAY = Pair.create(WEDNESDAY, Boolean.TRUE);
    private static final Pair<String, Boolean> TRUE_THURSDAY = Pair.create(THURSDAY, Boolean.TRUE);
    private static final Pair<String, Boolean> TRUE_FRIDAY = Pair.create(FRIDAY, Boolean.TRUE);
    private static final Pair<String, Boolean> TRUE_SATURDAY = Pair.create(SATURDAY, Boolean.TRUE);

    private static final Pair<String, Boolean> FALSE_SUNDAY = Pair.create(SUNDAY, Boolean.FALSE);
    private static final Pair<String, Boolean> FALSE_MONDAY = Pair.create(MONDAY, Boolean.FALSE);
    private static final Pair<String, Boolean> FALSE_TUESDAY = Pair.create(TUESDAY, Boolean.FALSE);
    private static final Pair<String, Boolean> FALSE_WEDNESDAY = Pair.create(WEDNESDAY, Boolean.FALSE);
    private static final Pair<String, Boolean> FALSE_THURSDAY = Pair.create(THURSDAY, Boolean.FALSE);
    private static final Pair<String, Boolean> FALSE_FRIDAY = Pair.create(FRIDAY, Boolean.FALSE);
    private static final Pair<String, Boolean> FALSE_SATURDAY = Pair.create(SATURDAY, Boolean.FALSE);

    public DayPicker(View view) {
        this.view = view;
        toggleButtonParent = (LinearLayout) view;

        init();
    }

    public DayPicker(View view, Map<String, Boolean> markedDays) {
        this.view = view;
        toggleButtonParent = (LinearLayout) view;

        init();

        this.markedDays = markedDays;
    }

    private void init() {
        toggleSunday = toggleButtonParent.findViewById(R.id.toggle_Sunday);
        toggleMonday = toggleButtonParent.findViewById(R.id.toggle_Monday);
        toggleTuesday = toggleButtonParent.findViewById(R.id.toggle_Tuesday);
        toggleWednesday = toggleButtonParent.findViewById(R.id.toggle_Wednesday);
        toggleThursday = toggleButtonParent.findViewById(R.id.toggle_Thursday);
        toggleFriday = toggleButtonParent.findViewById(R.id.toggle_Friday);
        toggleSaturday = toggleButtonParent.findViewById(R.id.toggle_Saturday);

        markedDays.put(SUNDAY, false);
        markedDays.put(MONDAY, false);
        markedDays.put(TUESDAY, false);
        markedDays.put(WEDNESDAY, false);
        markedDays.put(THURSDAY, false);
        markedDays.put(FRIDAY, false);
        markedDays.put(SATURDAY, false);

        customMarkedDays.put(SUNDAY, false);
        customMarkedDays.put(MONDAY, false);
        customMarkedDays.put(TUESDAY, false);
        customMarkedDays.put(WEDNESDAY, false);
        customMarkedDays.put(THURSDAY, false);
        customMarkedDays.put(FRIDAY, false);
        customMarkedDays.put(SATURDAY, false);

        trackedDays.put(TRUE_SUNDAY_ID, TRUE_SUNDAY);
        trackedDays.put(TRUE_MONDAY_ID, TRUE_MONDAY);
        trackedDays.put(TRUE_TUESDAY_ID, TRUE_TUESDAY);
        trackedDays.put(TRUE_WEDNESDAY_ID, TRUE_WEDNESDAY);
        trackedDays.put(TRUE_THURSDAY_ID, TRUE_THURSDAY);
        trackedDays.put(TRUE_FRIDAY_ID, TRUE_FRIDAY);
        trackedDays.put(TRUE_SATURDAY_ID, TRUE_SATURDAY);

        trackedDays.put(FALSE_SUNDAY_ID, FALSE_SUNDAY);
        trackedDays.put(FALSE_MONDAY_ID, FALSE_MONDAY);
        trackedDays.put(FALSE_TUESDAY_ID, FALSE_TUESDAY);
        trackedDays.put(FALSE_WEDNESDAY_ID, FALSE_WEDNESDAY);
        trackedDays.put(FALSE_THURSDAY_ID, FALSE_THURSDAY);
        trackedDays.put(FALSE_FRIDAY_ID, FALSE_FRIDAY);
        trackedDays.put(FALSE_SATURDAY_ID, FALSE_SATURDAY);
    }

    public Map<String, Boolean> getMarkedDays() {
        markedDays.put(SUNDAY, false);
        markedDays.put(MONDAY, false);
        markedDays.put(TUESDAY, false);
        markedDays.put(WEDNESDAY, false);
        markedDays.put(THURSDAY, false);
        markedDays.put(FRIDAY, false);
        markedDays.put(SATURDAY, false);

        for (int i = 0; i < toggleButtonParent.getChildCount(); i++) {
            if (((ToggleButton) toggleButtonParent.getChildAt(i)).isChecked()) {
                markedDays.put(String.valueOf(((ToggleButton) toggleButtonParent.getChildAt(i)).getText()), true);
            }
        }

        return markedDays;
    }

    public void setMarkedDays() {

        for (int i = 0; i < toggleButtonParent.getChildCount(); i++) {
            if (((ToggleButton) toggleButtonParent.getChildAt(i)).isChecked() && !markedDays.get(String.valueOf(((ToggleButton) toggleButtonParent.getChildAt(i)).getText()))) {
                ((ToggleButton) toggleButtonParent.getChildAt(i)).setChecked(false);
            }
            if (markedDays.get(String.valueOf(((ToggleButton) toggleButtonParent.getChildAt(i)).getText()))) {
                ((ToggleButton) toggleButtonParent.getChildAt(i)).setChecked(true);
            }
        }
    }

    public void setCustomMarkedDays(Integer[] days) {
        for (int i = 0; i < days.length; i++) {
            markedDays.put(trackedDays.get(days[i]).first, trackedDays.get(days[i]).second);
        }
    }

    public Map<String, Boolean> getCustomMarkedDays() {
        return customMarkedDays;
    }

    public LinearLayout getToggleButtonParent() {
        return toggleButtonParent;
    }

    public void setToggleButtonParent(LinearLayout toggleButtonParent) {
        this.toggleButtonParent = toggleButtonParent;
    }

    public ToggleButton getToggleSunday() {
        return toggleSunday;
    }

    public void setToggleSunday(ToggleButton toggleSunday) {
        this.toggleSunday = toggleSunday;
    }

    public ToggleButton getToggleMonday() {
        return toggleMonday;
    }

    public void setToggleMonday(ToggleButton toggleMonday) {
        this.toggleMonday = toggleMonday;
    }

    public ToggleButton getToggleTuesday() {
        return toggleTuesday;
    }

    public void setToggleTuesday(ToggleButton toggleTuesday) {
        this.toggleTuesday = toggleTuesday;
    }

    public ToggleButton getToggleWednesday() {
        return toggleWednesday;
    }

    public void setToggleWednesday(ToggleButton toggleWednesday) {
        this.toggleWednesday = toggleWednesday;
    }

    public ToggleButton getToggleThursday() {
        return toggleThursday;
    }

    public void setToggleThursday(ToggleButton toggleThursday) {
        this.toggleThursday = toggleThursday;
    }

    public ToggleButton getToggleFriday() {
        return toggleFriday;
    }

    public void setToggleFriday(ToggleButton toggleFriday) {
        this.toggleFriday = toggleFriday;
    }

    public ToggleButton getToggleSaturday() {
        return toggleSaturday;
    }

    public void setToggleSaturday(ToggleButton toggleSaturday) {
        this.toggleSaturday = toggleSaturday;
    }
}
