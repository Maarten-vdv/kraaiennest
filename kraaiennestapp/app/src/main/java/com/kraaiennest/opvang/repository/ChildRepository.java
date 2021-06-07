package com.kraaiennest.opvang.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.kraaiennest.opvang.model.Child;

import java.util.List;

public class ChildRepository {

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    CollectionReference children = firebaseFirestore.collection("children");


    public Task<List<Child>> getAll() {
        return children.get().continueWith(task -> task.getResult().toObjects(Child.class));
    }

    public Query getAllQuery() {
        return children;
    }

    public Task<Child> findById(String userId) {
        return children.whereEqualTo("userId", userId).limit(1).get().continueWith(snap -> {
            if (snap.isSuccessful() || !snap.getResult().isEmpty()) {
                return snap.getResult().toObjects(Child.class).get(0);
            } else {
                return null;
            }
        });
    }

    public Task<Child> findByPIN(String pin) {
        return children.whereEqualTo("pin", pin).limit(1).get().continueWith(snap -> {
            if (snap.isSuccessful() || !snap.getResult().isEmpty()) {
                return snap.getResult().toObjects(Child.class).get(0);
            } else {
                return null;
            }
        });
    }
}
