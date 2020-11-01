package com.kraaiennest.kraaiennestapp.model;

import com.google.gson.annotations.SerializedName;

public class Total {
    @SerializedName("name")
    private String name;
    @SerializedName("amount")
    private int amount;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
