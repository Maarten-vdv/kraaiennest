package com.kraaiennest.opvang.model;

import com.google.firebase.Timestamp;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Presence {
    @SerializedName("child")
    private Child child;
    @SerializedName("registrations")
    private List<Timestamp> registrationTimes;

    private Timestamp checkIn;

    public Child getChild() {
        return child;
    }

    public void setChild(Child child) {
        this.child = child;
    }

    public List<Timestamp> getRegistrationTimes() {
        return registrationTimes;
    }

    public void setRegistrationTimes(List<Timestamp> registrationTimes) {
        this.registrationTimes = registrationTimes;
    }

    public Timestamp getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(Timestamp checkIn) {
        this.checkIn = checkIn;
    }
}
