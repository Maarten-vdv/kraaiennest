package com.kraaiennest.opvang.model;

import androidx.room.Entity;
import com.google.firebase.Timestamp;
import com.google.gson.annotations.SerializedName;
import org.parceler.Parcel;

import java.time.LocalDateTime;

@Parcel
public class Registration {

    @SerializedName("childId")
    public Integer childId;

    @SerializedName("halfHours")
    public int halfHours;

    @SerializedName("realHalfHours")
    public int realHalfHours;

    @SerializedName("partOfDay")
    public PartOfDay partOfDay;

    @SerializedName("time")
    public LocalDateTime registrationTime;

    public Integer getChildId() {
        return childId;
    }

    public void setChildId(Integer childId) {
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
