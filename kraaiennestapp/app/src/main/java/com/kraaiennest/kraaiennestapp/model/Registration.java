package com.kraaiennest.kraaiennestapp.model;

import com.google.gson.annotations.SerializedName;
import org.parceler.Parcel;

import java.time.LocalDateTime;

@Parcel
public class Registration {

    @SerializedName("childId")
    public String childId;

    @SerializedName("halfHours")
    public int halfHours;

    @SerializedName("realHalfHours")
    public int realHalfHours;

    @SerializedName("partOfDay")
    public PartOfDay partOfDay;

    @SerializedName("registrationTime")
    public LocalDateTime registrationTime;

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    public int getHalfHours() {
        return halfHours;
    }

    public void setHalfHours(int halfHours) {
        this.halfHours = halfHours;
    }

    public int getRealHalfHours() {
        return realHalfHours;
    }

    public void setRealHalfHours(int realHalfHours) {
        this.realHalfHours = realHalfHours;
    }

    public PartOfDay getPartOfDay() {
        return partOfDay;
    }

    public void setPartOfDay(PartOfDay partOfDay) {
        this.partOfDay = partOfDay;
    }

    public LocalDateTime getRegistrationTime() {
        return registrationTime;
    }

    public void setRegistrationTime(LocalDateTime registrationTime) {
        this.registrationTime = registrationTime;
    }
}