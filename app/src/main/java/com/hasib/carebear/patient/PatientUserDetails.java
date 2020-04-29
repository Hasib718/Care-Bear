package com.hasib.carebear.patient;

import android.widget.CheckBox;

public class PatientUserDetails {
    private String name,mobileNum,address;
    private Boolean sex;

    public PatientUserDetails() {
    }

    public PatientUserDetails(String name, String mobileNum, String address, Boolean sex) {
        this.name = name;
        this.mobileNum = mobileNum;
        this.address = address;
        this.sex = sex;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMobileNum(String mobileNum) {
        this.mobileNum = mobileNum;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setSex(Boolean sex) {
        this.sex = sex;
    }

    public String getName() {
        return name;
    }

    public String getMobileNum() {
        return mobileNum;
    }

    public String getAddress() {
        return address;
    }

    public Boolean getSex() {
        return sex;
    }
}
