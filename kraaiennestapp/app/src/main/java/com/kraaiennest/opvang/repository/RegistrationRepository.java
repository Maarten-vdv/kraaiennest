package com.kraaiennest.opvang.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.kraaiennest.opvang.model.CheckIn;
import com.kraaiennest.opvang.model.Registration;

import java.time.LocalDate;
import java.util.List;

public class RegistrationRepository {

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    CollectionReference registrations = firebaseFirestore.collection("registrations");
    CollectionReference checkIns = firebaseFirestore.collection("check-ins");


    public Query getRegistrationsQuery() {
        return registrations;
    }

    public Query getRegistrationByDateQuery(LocalDate date) {
        return registrations
                .whereGreaterThanOrEqualTo("registrationTime", date.toEpochDay())
                .whereLessThan("registrationTime", date.plusDays(1).toEpochDay());
    }

    public Query getChecksByDateQuery(LocalDate date) {
        return checkIns
                .whereGreaterThanOrEqualTo("checkInTime", date.toEpochDay())
                .whereLessThan("checkInTime", date.plusDays(1).toEpochDay());
    }

    Task<List<Registration>> getRegistrationByDate(LocalDate date) {
        return getRegistrationByDateQuery(date).get().continueWith(snap -> {
            if (snap.isSuccessful() || !snap.getResult().isEmpty()) {
                return snap.getResult().toObjects(Registration.class);
            } else {
                return null;
            }
        });
    }

    public Task<DocumentReference> createRegistration(Registration registration) {
        return registrations.add(registration);
    }

    public Task<DocumentReference> createCheckIn(CheckIn checkIn) {
        return checkIns.add(checkIn);
    }
}
