package com.kraaiennest.opvang.model;

import com.google.firebase.Timestamp;
import com.google.gson.annotations.SerializedName;
import org.parceler.Parcel;

import java.time.LocalDateTime;

@Parcel
public class CheckIn {

    @SerializedName("childId")
    public String childId;

    @SerializedName("time")
    public LocalDateTime checkInTime;

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }
}
