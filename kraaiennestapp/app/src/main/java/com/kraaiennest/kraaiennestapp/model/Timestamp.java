package com.kraaiennest.kraaiennestapp.model;

import java.util.Date;

public class Timestamp {
    private Date date;
    private PartOfDay partOfDay;
    private Date registeredAt;
    private int registeredUnits;
    private int realUnits;

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

    public int getRegisteredUnits() {
        return registeredUnits;
    }

    public void setRegisteredUnits(int registeredUnits) {
        this.registeredUnits = registeredUnits;
    }

    public int getRealUnits() {
        return realUnits;
    }

    public void setRealUnits(int realUnits) {
        this.realUnits = realUnits;
    }
}
