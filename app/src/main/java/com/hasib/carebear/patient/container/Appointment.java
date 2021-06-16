package com.hasib.carebear.patient.container;

import com.hasib.carebear.doctor.container.Chamber;
import com.hasib.carebear.doctor.container.Doctor;

public class Appointment {
    private String id;
    private String idInChamber;
    private Chamber chamber;
    private Doctor doctor;
    private String timeDate;
    private long serialNumber;

    public Appointment(String id, String idInChamber, Chamber chamber, Doctor doctor, String timeDate, long serialNumber) {
        this.id = id;
        this.idInChamber = idInChamber;
        this.chamber = chamber;
        this.doctor = doctor;
        this.timeDate = timeDate;
        this.serialNumber = serialNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdInChamber() {
        return idInChamber;
    }

    public void setIdInChamber(String idInChamber) {
        this.idInChamber = idInChamber;
    }

    public Chamber getChamber() {
        return chamber;
    }

    public void setChamber(Chamber chamber) {
        this.chamber = chamber;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public String getTimeDate() {
        return timeDate;
    }

    public void setTimeDate(String timeDate) {
        this.timeDate = timeDate;
    }

    public long getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(long serialNumber) {
        this.serialNumber = serialNumber;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "id='" + id + '\'' +
                ", idInChamber='" + idInChamber + '\'' +
                ", chamber=" + chamber +
                ", doctor=" + doctor +
                ", timeDate='" + timeDate + '\'' +
                ", serialNumber=" + serialNumber +
                '}';
    }
}
