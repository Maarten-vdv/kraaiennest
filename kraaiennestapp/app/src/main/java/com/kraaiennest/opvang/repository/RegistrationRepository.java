package com.kraaiennest.opvang.repository;

import com.kraaiennest.opvang.api.APIService;
import com.kraaiennest.opvang.model.CheckIn;
import com.kraaiennest.opvang.model.Registration;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RegistrationRepository {

    private final APIService api;

    public RegistrationRepository(APIService apiService) {
        this.api = apiService;
    }

    public CompletableFuture<List<Registration>> getRegistrationsOnDay(LocalDate date) {
        return api.doGetRegistrationsOnDay(date.getMonthValue(), date.getDayOfMonth());
    }

    public CompletableFuture<List<CheckIn>> getCheckInsOnDay(LocalDate date) {
        return api.doGetCheckInsOnDay(date.getMonthValue(), date.getDayOfMonth());
    }

    public CompletableFuture<CheckIn> createCheckIn(CheckIn checkIn) {
        return api.doPostCheckIn(checkIn);
    }

    public CompletableFuture<Registration> createRegistration(Registration registration) {
        return api.doPostRegistration(registration);
    }

}
