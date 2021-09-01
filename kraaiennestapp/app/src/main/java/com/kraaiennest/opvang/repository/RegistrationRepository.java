package com.kraaiennest.opvang.repository;

import com.kraaiennest.opvang.api.APIService;
import com.kraaiennest.opvang.model.CheckIn;
import com.kraaiennest.opvang.model.PartOfDay;
import com.kraaiennest.opvang.model.Registration;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    public LocalDateTime getCutOff() {
        LocalDateTime cutoff = LocalDateTime.now();
        if (cutoff.getHour() >= 12) {
            // evening
            return cutoff.withHour(15).withMinute(45);
        } else {
            // morning
            return cutoff.withHour(8).withMinute(5);
        }
    }

    public Integer calculateHalfHours() {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(now, getCutOff()).abs();
        return (30 + Math.toIntExact(duration.toMinutes() -1)) / 30;
    }

    public PartOfDay getPartOfDay() {
        return LocalDateTime.now().getHour() >= 12 ? PartOfDay.A : PartOfDay.O;
    }

}
