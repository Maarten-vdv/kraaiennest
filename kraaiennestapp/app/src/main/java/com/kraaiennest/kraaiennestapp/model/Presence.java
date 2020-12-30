package com.kraaiennest.kraaiennestapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Presence {
    @SerializedName("child")
    private Child child;
    @SerializedName("timestamps")
    private List<Timestamp> timestamps;

    public Child getChild() {
        return child;
    }

    public void setChild(Child child) {
        this.child = child;
    }

    public List<Timestamp> getTimestamps() {
        return timestamps;
    }

    public void setTimestamps(List<Timestamp> timestamps) {
        this.timestamps = timestamps;
    }
}
