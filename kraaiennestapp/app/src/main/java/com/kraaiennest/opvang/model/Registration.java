package com.kraaiennest.opvang.model;

import androidx.room.Entity;
import com.google.firebase.Timestamp;
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
    public Timestamp registrationTime;

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

    public Timestamp getRegistrationTime() {
        return registrationTime;
    }

    public void setRegistrationTime(Timestamp registrationTime) {
        this.registrationTime = registrationTime;
    }
}
