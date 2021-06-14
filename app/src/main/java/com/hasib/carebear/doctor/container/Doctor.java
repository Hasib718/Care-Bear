package com.hasib.carebear.doctor.container;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Doctor implements Parcelable {
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
    private String doctorImageUrl = "";

    public Doctor() {}

    public Doctor(Parcel parcel) {
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
    }

    public Doctor(String id, String fullName, String email, String mobile, String password, String specialist, String checkBoxInfo, String registrationInfo, String presentAddressInfo, String medicalInfo, String doctorImageUrl) {
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
                "Image Uri "+getDoctorImageUrl()+"\n");
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
    }

    public static final Parcelable.Creator<Doctor> CREATOR = new Parcelable.Creator<Doctor>() {
        @Override
        public Doctor createFromParcel(Parcel source) {
            return new Doctor(source);
        }

        @Override
        public Doctor[] newArray(int size) {
            return new Doctor[size];
        }
    };

    @Override
    public int describeContents() {
        return hashCode();
    }

    public void setCommonChamberInfo(String toString) {
    }
}
