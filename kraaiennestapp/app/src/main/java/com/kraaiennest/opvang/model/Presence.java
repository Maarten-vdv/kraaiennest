package com.kraaiennest.opvang.model;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

public class Presence {
    @SerializedName("child")
    private Child child;
    @SerializedName("registrations")
    private LocalDateTime registrationTime;

    private LocalDateTime checkIn;

    public Presence(Child child, LocalDateTime registrationTime, LocalDateTime checkIn) {
        this.child = child;
        this.registrationTime = registrationTime;
        this.checkIn = checkIn;
    }

    public Presence() { }

    public Child getChild() {
        return child;
    }

    public void setChild(Child child) {
        this.child = child;
    }

    public LocalDateTime getRegistrationTime() {
        return registrationTime;
    }

    public void setRegistrationTimes(LocalDateTime registrationTime) {
        this.registrationTime = registrationTime;
    }

    public LocalDateTime getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(LocalDateTime checkIn) {
        this.checkIn = checkIn;
    }
}
