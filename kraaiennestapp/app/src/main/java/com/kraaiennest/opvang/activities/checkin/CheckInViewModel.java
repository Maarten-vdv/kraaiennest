package com.kraaiennest.opvang.activities.checkin;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.kraaiennest.opvang.R;
import com.kraaiennest.opvang.activities.register.ApiCallState;
import com.kraaiennest.opvang.model.CheckIn;
import com.kraaiennest.opvang.model.Child;
import com.kraaiennest.opvang.repository.ChildRepository;
import com.kraaiennest.opvang.repository.RegistrationRepository;

import java.util.Map;

public class CheckInViewModel extends ViewModel {

    private final RegistrationRepository registrationRepository;
    private final ChildRepository childRepository;
    private MutableLiveData<Child> child;
    private MutableLiveData<ApiCallState> apiCallState;
    private Map<Integer, String> strings;

    @ViewModelInject
    public CheckInViewModel(RegistrationRepository registrationRepository, ChildRepository childRepository) {
        this.registrationRepository = registrationRepository;
        this.childRepository = childRepository;
    }

    public void loadExtra( Map<Integer, String> strings) {
        this.strings = strings;
    }

    public LiveData<Child> getChild() {
        if (child == null) {
            child = new MutableLiveData<>();
            child.setValue(null);
        }
        return child;
    }

    public void loadChildById(String id) {
        childRepository.findById(id).addOnSuccessListener(c -> child.setValue(c));
    }

    public void loadChildByPIN(String pin) {
        childRepository.findByPIN(pin).addOnSuccessListener(c -> child.setValue(c)).addOnFailureListener(f -> System.out.println(f));
    }


    public LiveData<String> getFullName() {
        return Transformations.map(getChild(), c -> c == null ? strings.get(R.string.scan_child) : c.getFullName());
    }

    public MutableLiveData<ApiCallState> getCheckInState() {
        if (apiCallState == null) {
            apiCallState = new MutableLiveData<>(ApiCallState.IDLE);
        }
        return apiCallState;
    }

    public void checkInDone() {
        apiCallState.setValue(ApiCallState.IDLE);
    }

    public void createCheckIn() {
        apiCallState.setValue(ApiCallState.BUSY);
        if (child.getValue() != null) {
            CheckIn checkIn = new CheckIn();
            checkIn.setCheckInTime(Timestamp.now());
            checkIn.setChildId(child.getValue().getId());
            Task<DocumentReference> checkInTask = registrationRepository.createCheckIn(checkIn);

            checkInTask.addOnSuccessListener((DocumentReference result) -> {
                child.setValue(null);
                apiCallState.setValue(ApiCallState.SUCCESS);
            });
            checkInTask.addOnFailureListener((Exception error) -> apiCallState.setValue(ApiCallState.ERROR));
        }
    }

    public void clearChild() {
        this.child.setValue(null);
    }
}
