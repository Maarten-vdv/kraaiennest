package com.kraaiennest.opvang.model;

import com.google.firebase.Timestamp;
import com.google.gson.annotations.SerializedName;
import org.parceler.Parcel;

@Parcel
public class CheckIn {

    @SerializedName("childId")
    public String childId;

    @SerializedName("checkInTime")
    public Timestamp checkInTime;

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    public Timestamp getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(Timestamp checkInTime) {
        this.checkInTime = checkInTime;
    }
}