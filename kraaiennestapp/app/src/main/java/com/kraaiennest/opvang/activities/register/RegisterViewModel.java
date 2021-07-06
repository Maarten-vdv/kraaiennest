package com.kraaiennest.opvang.activities.register;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import com.google.firebase.Timestamp;
import com.kraaiennest.opvang.R;
import com.kraaiennest.opvang.model.Child;
import com.kraaiennest.opvang.model.PartOfDay;
import com.kraaiennest.opvang.model.Registration;
import com.kraaiennest.opvang.repository.ChildRepository;
import com.kraaiennest.opvang.repository.RegistrationRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static java.text.MessageFormat.format;

public class RegisterViewModel extends ViewModel {

    private final RegistrationRepository repository;
    private final ChildRepository childRepository;
    private List<Child> children;
    private MutableLiveData<Child> child;
    private MutableLiveData<Integer> halfHours;
    private MutableLiveData<LocalDateTime> cutOff;
    private MutableLiveData<PartOfDay> partOfDay;
    private MutableLiveData<ApiCallState> registrationState;
    private Map<Integer, String> strings;
    private final DateTimeFormatter dateTimeFormatter;

    @ViewModelInject
    public RegisterViewModel(RegistrationRepository registrationRepository, ChildRepository childRepository) {
        this.repository = registrationRepository;
        this.childRepository = childRepository;
        this.dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
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

    public LiveData<String> getFullName() {
        return Transformations.map(getChild(), c -> c == null ? strings.get(R.string.scan_child) : c.getFullName());
    }

    public LiveData<PartOfDay> getPartOfDay() {
        if (partOfDay == null) {
            LocalDateTime now = LocalDateTime.now();
            partOfDay = new MutableLiveData<>(now.getHour() > 11 ? PartOfDay.A : PartOfDay.O);
        }
        return partOfDay;
    }

    public LiveData<LocalDateTime> getCutOff() {
        if (cutOff == null) {
            LocalDateTime cutoff = LocalDateTime.now();
            if (cutoff.getHour() >= 12) {
                // evening
                cutoff = cutoff.withHour(15).withMinute(45);
            } else {
                // morning
                cutoff = cutoff.withHour(8).withMinute(0);
            }
            cutOff = new MutableLiveData<>(cutoff);
        }

        return cutOff;
    }

    public LiveData<String> getCutOffValue() {
        return Transformations.map(getCutOff(), c -> c == null ? "" : c.format(dateTimeFormatter));
    }

    public LiveData<String> getHalfHours() {
        if (halfHours == null) {
            halfHours = new MutableLiveData<>(calculateHalfHours());
        }

        return Transformations.map(halfHours, integer -> integer == null ? "" : format("{0}{1}", integer.toString(), strings.get(R.string.half_hours)));
    }

    public MutableLiveData<ApiCallState> getRegistrationState() {
        if (registrationState == null) {
            registrationState = new MutableLiveData<>(ApiCallState.IDLE);
        }
        return registrationState;
    }

    private Integer calculateHalfHours() {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(now, getCutOff().getValue()).abs();
        return Math.toIntExact(duration.toMinutes()) / 30;
    }

    public void createRegistration() {
        if (child.getValue() == null) {
            return;
        }

        registrationState.setValue(ApiCallState.BUSY);
        Registration registration = new Registration();
        registration.setChildId(child.getValue().getId());
        registration.setHalfHours(halfHours.getValue() != null ? halfHours.getValue() : 0);
        registration.setRealHalfHours(calculateHalfHours());
        registration.setRegistrationTime(Timestamp.now());
        registration.setPartOfDay(getPartOfDay().getValue());

        repository.createRegistration(registration)
                .addOnFailureListener(e -> registrationState.setValue(ApiCallState.ERROR))
                .addOnSuccessListener(documentReference -> {
                    child.setValue(null);
                    halfHours.setValue(null);
                    registrationState.setValue(ApiCallState.SUCCESS);
                });
    }

    public void loadChildById(String id) {
        childRepository.findById(id).addOnSuccessListener(c -> child.setValue(c));
    }

    public void loadChildByPIN(String pin) {
        childRepository.findByPIN(pin).addOnSuccessListener(c -> child.setValue(c)).addOnFailureListener(f -> System.out.println(f));
    }

    public void addHalfHours(int i) {
        if (this.halfHours.getValue() != null) {
            this.halfHours.setValue(Math.max(0, this.halfHours.getValue() + i));
        }
    }

    public void clearChild() {
        this.child.setValue(null);
    }

    public void registrationDone() {
        registrationState.setValue(ApiCallState.IDLE);
    }
}
