package com.kraaiennest.opvang.activities.nfc;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.kraaiennest.opvang.R;
import com.kraaiennest.opvang.activities.register.ApiCallState;
import com.kraaiennest.opvang.model.Child;
import com.kraaiennest.opvang.repository.ChildRepository;
import com.kraaiennest.opvang.repository.RegistrationRepository;

import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class NfcOverviewViewModel extends ViewModel {

    private final ChildRepository childRepository;
    private MutableLiveData<Child> child;
    private MutableLiveData<ApiCallState> apiCallState;
    private Map<Integer, String> strings;

    @Inject
    public NfcOverviewViewModel(RegistrationRepository registrationRepository, ChildRepository childRepository) {
        this.childRepository = childRepository;
    }

    public void loadExtra(Map<Integer, String> strings) {
        this.strings = strings;
    }

    public LiveData<Child> getChild() {
        if (child == null) {
            child = new MutableLiveData<>();
            child.setValue(null);
        }
        return child;
    }

    public void loadChildByQrId(String id) {
        childRepository.findByQrId(id).thenAcceptAsync(c -> child.postValue(c)).exceptionally(f -> null);
    }

    public void loadChildByPIN(String pin) {
        childRepository.findByPIN(pin).thenAcceptAsync(c -> child.postValue(c)).exceptionally(f -> null);
    }

    public LiveData<String> getFullName() {
        return Transformations.map(getChild(), c -> c == null ? strings.get(R.string.scan_child) : c.getFullName());
    }

    public MutableLiveData<ApiCallState> getState() {
        if (apiCallState == null) {
            apiCallState = new MutableLiveData<>(ApiCallState.IDLE);
        }
        return apiCallState;
    }

    public void writeDone() {
        apiCallState.setValue(ApiCallState.IDLE);
    }

    public void clearChild() {
        this.child.setValue(null);
    }
}
