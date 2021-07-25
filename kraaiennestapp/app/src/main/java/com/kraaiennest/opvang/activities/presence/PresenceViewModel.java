package com.kraaiennest.opvang.activities.presence;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import com.kraaiennest.opvang.model.*;
import com.kraaiennest.opvang.repository.ChildRepository;
import com.kraaiennest.opvang.repository.RegistrationRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PresenceViewModel extends ViewModel {

    private final RegistrationRepository registrationRepository;
    private final ChildRepository childRepository;
    private MutableLiveData<List<Presence>> presences;

    private MutableLiveData<String> errorMessage;

    Map<Integer, String> strings;
    private PresenceSortOrder sortOrder = PresenceSortOrder.NAME;

    @ViewModelInject
    public PresenceViewModel(RegistrationRepository registrationRepository, ChildRepository childRepository) {
        this.registrationRepository = registrationRepository;
        this.childRepository = childRepository;
    }

    public MutableLiveData<String> getErrorMessage() {
        if (errorMessage == null) {
            errorMessage = new MutableLiveData<>();
        }
        return errorMessage;
    }

    public void loadExtra(Map<Integer, String> strings) {
        this.strings = strings;
    }

    public void refreshPresences() {
        CompletableFuture<List<Child>> childFuture = childRepository.getChildren();
        CompletableFuture<List<Registration>> regFuture = registrationRepository.getRegistrationsOnDay(LocalDate.now());
        CompletableFuture<List<CheckIn>> checkInFuture = registrationRepository.getCheckInsOnDay(LocalDate.now());

        CompletableFuture.allOf(childFuture, regFuture, checkInFuture).thenRunAsync(() -> {
            Map<String, Child> children = childFuture.join().stream().collect(Collectors.toMap(Child::getId, Function.identity()));
            Map<String, Registration> regs = regFuture.join().stream().collect(Collectors.toMap(Registration::getChildId, Function.identity()));
            this.presences.postValue(
                    checkInFuture.join().stream()
                            .map(c -> new Presence(children.get(c.getChildId()), regs.get(c.getChildId()).getRegistrationTime(), c.getCheckInTime()))
                            .collect(Collectors.toList())
            );
        });
    }

    public void setSortOrder(PresenceSortOrder sortOrder) {
        this.sortOrder = sortOrder;
        List<Presence> value = this.presences.getValue();
        if (value != null) {
            value.sort(new PresenceComparator(sortOrder));
            this.presences.setValue(value);
        }
    }

    public MutableLiveData<List<Presence>> getPresences() {
        return presences;
    }

    public LiveData<Boolean> isEmpty() {
        return Transformations.map(presences, List::isEmpty);
    }
}
