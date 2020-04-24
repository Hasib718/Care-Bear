package com.hasib.carebear.doctor.container;

public class UserDetails {
    private String fullName;
    private String email;
    private String mobile;
    private String password;
    private String specialist;
    private String checkBoxInfo = "";
    private String registrationInfo;
    private String presentAddressInfo;
    private String permanentAddressInfo;
    private String commonChamberInfo;
    private String doctorImageUrl;

    public UserDetails() {
    }

    public UserDetails(String fullName, String email, String mobile, String password, String specialist, String checkBoxInfo, String registrationInfo, String presentAddressInfo, String permanentAddressInfo, String commonChamberInfo, String doctorImageUrl) {
        this.fullName = fullName;
        this.email = email;
        this.mobile = mobile;
        this.password = password;
        this.specialist = specialist;
        this.checkBoxInfo = checkBoxInfo;
        this.registrationInfo = registrationInfo;
        this.presentAddressInfo = presentAddressInfo;
        this.permanentAddressInfo = permanentAddressInfo;
        this.commonChamberInfo = commonChamberInfo;
        this.doctorImageUrl = doctorImageUrl;
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

    public void setSpecialist(String specialist) {
        this.specialist = specialist;
    }

    public String getCheckBoxInfo() {
        return checkBoxInfo;
    }

    public void setCheckBoxInfo(String checkBoxInfo) {
        this.checkBoxInfo += checkBoxInfo+" ";
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

    public String getPermanentAddressInfo() {
        return permanentAddressInfo;
    }

    public void setPermanentAddressInfo(String permanentAddressInfo) {
        this.permanentAddressInfo = permanentAddressInfo;
    }

    public String getCommonChamberInfo() {
        return commonChamberInfo;
    }

    public void setCommonChamberInfo(String commonChamberInfo) {
        this.commonChamberInfo = commonChamberInfo;
    }

    public String getDoctorImageUrl() {
        return doctorImageUrl;
    }

    public void setDoctorImageUrl(String doctorImageUrl) {
        this.doctorImageUrl = doctorImageUrl;
    }
}
