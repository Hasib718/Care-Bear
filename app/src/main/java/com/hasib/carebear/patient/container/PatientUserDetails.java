package com.hasib.carebear.patient.container;

import android.widget.CheckBox;

public class PatientUserDetails {
    private String name,mobileNum,address,email,password;
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

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
