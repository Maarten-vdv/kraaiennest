package com.kraaiennest.opvang.activities.pin;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Objects;

public class PinViewModel extends ViewModel {

    private final MutableLiveData<String> pin = new MutableLiveData<>("");

    public MutableLiveData<String> getPin() {
        return pin;
    }

    public void addDigit(String digit) {
        if (pin.getValue().length() < 5) {
            pin.setValue(pin.getValue() + digit);
        }
    }

    public void back() {
        if (Objects.requireNonNull(pin.getValue()).length() > 0)
            pin.setValue(pin.getValue().substring(0, pin.getValue().length() - 1));
    }
}
