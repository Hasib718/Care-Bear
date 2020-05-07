package com.hasib.carebear.doctor.container;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.HashMap;

public class UserDetails implements Parcelable {
    private String id;
    private String fullName;
    private String email;
    private String mobile;
    private String password;
    private String specialist;
    private String checkBoxInfo = "";
    private String registrationInfo;
    private String presentAddressInfo;
    private String medicalInfo;
    private String doctorImageUrl;
    private HashMap chamber;

    public UserDetails() {}

    public UserDetails(Parcel parcel) {
        id = parcel.readString();
        fullName = parcel.readString();
        email = parcel.readString();
        mobile = parcel.readString();
        password = parcel.readString();
        specialist = parcel.readString();
        checkBoxInfo = parcel.readString();
        registrationInfo = parcel.readString();
        presentAddressInfo = parcel.readString();
        medicalInfo = parcel.readString();
        doctorImageUrl = parcel.readString();
        chamber = parcel.readHashMap(HashMap.class.getClassLoader());
    }

    public UserDetails(String id, String fullName, String email, String mobile, String password,
                       String specialist, String checkBoxInfo, String registrationInfo, String presentAddressInfo,
                       String medicalInfo, String doctorImageUrl, HashMap chamber) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.mobile = mobile;
        this.password = password;
        this.specialist = specialist;
        this.checkBoxInfo = checkBoxInfo;
        this.registrationInfo = registrationInfo;
        this.presentAddressInfo = presentAddressInfo;
        this.medicalInfo = medicalInfo;
        this.doctorImageUrl = doctorImageUrl;
        this.chamber = (HashMap) chamber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getSpecialist() {
        return specialist;
    }

    public void setCheckBoxInfoEmpty() { this.checkBoxInfo = ""; }

    public void setSpecialist(String specialist) {
        this.specialist = specialist;
    }

    public String getCheckBoxInfo() {
        return checkBoxInfo;
    }

    public void setCheckBoxInfo(String checkBoxInfo) {
        this.checkBoxInfo += checkBoxInfo+", ";
    }

    public void setCheckBoxInfoNull() {
        this.checkBoxInfo = "";
    }

    public String getRegistrationInfo() {
        return registrationInfo;
    }

    public void setRegistrationInfo(String registrationInfo) {
        this.registrationInfo = registrationInfo;
    }

    public String getPresentAddressInfo() {
        return presentAddressInfo;
    }

    public void setPresentAddressInfo(String presentAddressInfo) {
        this.presentAddressInfo = presentAddressInfo;
    }

    public String getMedicalInfo() {
        return medicalInfo;
    }

    public void setMedicalInfo(String medicalInfo) {
        this.medicalInfo = medicalInfo;
    }

    public String getDoctorImageUrl() {
        return doctorImageUrl;
    }

    public void setDoctorImageUrl(String doctorImageUrl) {
        this.doctorImageUrl = doctorImageUrl;
    }

    public HashMap getChambers() {
        return chamber;
    }

    public void setChambers(HashMap chamber) {
        this.chamber = chamber;
    }

    @NonNull
    @Override
    public String toString() {
        return ("id "+getId()+"\n"+
                "Name "+getFullName()+"\n"+
                "Email "+getEmail()+"\n"+
                "Mobile "+getMobile()+"\n"+
                "Password "+getPassword()+"\n"+
                "Specialist "+getSpecialist()+"\n"+
                "CheckBox "+getCheckBoxInfo()+"\n"+
                "Registration "+getRegistrationInfo()+"\n"+
                "Present Address "+getPresentAddressInfo()+"\n"+
                "Common Chamber "+getMedicalInfo()+"\n"+
                "Image Uri "+getDoctorImageUrl()+"\n"+
                "Chambers "+getChambers()+"\n");
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(fullName);
        dest.writeString(email);
        dest.writeString(mobile);
        dest.writeString(password);
        dest.writeString(specialist);
        dest.writeString(checkBoxInfo);
        dest.writeString(registrationInfo);
        dest.writeString(presentAddressInfo);
        dest.writeString(medicalInfo);
        dest.writeString(doctorImageUrl);
        dest.writeMap(chamber);
    }

    public static final Parcelable.Creator<UserDetails> CREATOR = new Parcelable.Creator<UserDetails>() {
        @Override
        public UserDetails createFromParcel(Parcel source) {
            return new UserDetails(source);
        }

        @Override
        public UserDetails[] newArray(int size) {
            return new UserDetails[size];
        }
    };

    @Override
    public int describeContents() {
        return hashCode();
    }
}
