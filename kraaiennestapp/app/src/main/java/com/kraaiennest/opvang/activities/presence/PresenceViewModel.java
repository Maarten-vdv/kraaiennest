package com.kraaiennest.opvang.activities.presence;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.kraaiennest.opvang.model.CheckIn;
import com.kraaiennest.opvang.model.Child;
import com.kraaiennest.opvang.model.Presence;
import com.kraaiennest.opvang.model.PresenceSortOrder;
import com.kraaiennest.opvang.model.Registration;
import com.kraaiennest.opvang.repository.ChildRepository;
import com.kraaiennest.opvang.repository.RegistrationRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class PresenceViewModel extends ViewModel {

    private final RegistrationRepository registrationRepository;
    private final ChildRepository childRepository;
    private MutableLiveData<List<Presence>> presences;

    private MutableLiveData<String> errorMessage;

    Map<Integer, String> strings;
    private PresenceSortOrder sortOrder = PresenceSortOrder.NAME;

    @Inject
    public PresenceViewModel(RegistrationRepository registrationRepository, ChildRepository childRepository) {
        this.registrationRepository = registrationRepository;
        this.childRepository = childRepository;
        presences = new MutableLiveData<>(new ArrayList<>());
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

    public CompletableFuture<Void> refreshPresences() {
        CompletableFuture<List<Child>> childFuture = childRepository.getChildren();
        CompletableFuture<List<Registration>> regFuture = registrationRepository.getRegistrationsOnDay(LocalDate.now());

        if (LocalTime.now().getHour() > 12) {
            //evening
            CompletableFuture<List<CheckIn>> checkInFuture = registrationRepository.getCheckInsOnDay(LocalDate.now());
            return CompletableFuture.allOf(childFuture, regFuture, checkInFuture).thenRunAsync(() -> {
                Map<Integer, Child> children = childFuture.join().stream().collect(Collectors.toMap(Child::getId, Function.identity()));
                Map<Integer, Registration> regs = regFuture.join().stream().collect(Collectors.toMap(Registration::getChildId, Function.identity()));
                this.presences.postValue(
                        checkInFuture.join().stream().map(c -> {
                            Integer childId = c.getChildId();
                            return new Presence(children.get(childId), regs.containsKey(childId) ? regs.get(childId).getRegistrationTime() : null, c.getCheckInTime());
                        }).collect(Collectors.toList())
                );
            });
        } else {
            // morning
            return childFuture.thenCombineAsync(regFuture, (c, regs) -> {
                Map<Integer, Child> children = c.stream().collect(Collectors.toMap(Child::getId, Function.identity()));
                return regs.stream().map(reg -> new Presence(children.get(reg.getChildId()), reg.getRegistrationTime(), null)).collect(Collectors.toList());
            }).thenAcceptAsync(list -> this.presences.postValue(list));
        }
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
        if (presences == null) {
            refreshPresences();
        }
        return presences;
    }

    public LiveData<Boolean> isEmpty() {
        return Transformations.map(presences, List::isEmpty);
    }
}
