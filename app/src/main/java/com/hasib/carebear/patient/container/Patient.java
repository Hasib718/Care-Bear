package com.hasib.carebear.patient.container;

import android.widget.CheckBox;

public class Patient {
    private String name,mobileNum,address,email,password;
    private Boolean sex;
    private String imageName;
    private String imageUrl;


    public Patient() {
    }

    public Patient(String name, String mobileNum, String address, Boolean sex) {
        this.name = name;
        this.mobileNum = mobileNum;
        this.address = address;
        this.sex = sex;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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


    public String getImageName() {
        return imageName;
    }

    public String getImageUrl() {
        return imageUrl;
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
