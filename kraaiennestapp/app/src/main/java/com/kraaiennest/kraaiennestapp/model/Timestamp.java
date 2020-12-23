package com.kraaiennest.kraaiennestapp.model;

import java.util.Date;

public class Timestamp {
    private Date date;
    private PartOfDay partOfDay;
    private Date registeredAt;
    private boolean isCheckIn;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public PartOfDay getPartOfDay() {
        return partOfDay;
    }

    public void setPartOfDay(PartOfDay partOfDay) {
        this.partOfDay = partOfDay;
    }

    public Date getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(Date registeredAt) {
        this.registeredAt = registeredAt;
    }

    public boolean isCheckIn() {
        return isCheckIn;
    }

    public void setCheckIn(boolean checkIn) {
        isCheckIn = checkIn;
    }
}
