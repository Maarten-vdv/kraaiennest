package com.kraaiennest.opvang.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PresenceResponse {

    @SerializedName("totals")
    public List<Total> totals;
    @SerializedName("presences")
    public List<Presence> presences;
}
