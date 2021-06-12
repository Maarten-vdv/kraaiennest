package com.kraaiennest.opvang.activities.presence;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.firestore.Query;
import com.kraaiennest.opvang.model.Child;
import com.kraaiennest.opvang.model.Presence;
import com.kraaiennest.opvang.model.PresenceSortOrder;
import com.kraaiennest.opvang.repository.ChildRepository;
import com.kraaiennest.opvang.repository.RegistrationRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class PresenceViewModel extends ViewModel {

    private final RegistrationRepository registrationRepository;
    private final ChildRepository childRepository;
    private String scriptId;
    private MutableLiveData<List<Presence>> presences;

    private MutableLiveData<String> errorMessage;

    Map<Integer, String> strings;
    private PresenceSortOrder sortOrder = PresenceSortOrder.NAME;
    private MutableLiveData<List<Child>> children = new MutableLiveData<>();

    @ViewModelInject
    public PresenceViewModel(RegistrationRepository registrationRepository, ChildRepository childRepository) {
        this.registrationRepository = registrationRepository;
        this.childRepository = childRepository;
    }

    public Query getRegistrations() {
        return registrationRepository.getRegistrationByDateQuery(LocalDate.now());
    }

    public Query getCheckIns() {
        return registrationRepository.getCheckInsByDateQuery(LocalDate.now());
    }

    public MutableLiveData<String> getErrorMessage() {
        if (errorMessage == null) {
            errorMessage = new MutableLiveData<>();
        }
        return errorMessage;
    }

    public void loadExtra(String scriptId, Map<Integer, String> strings) {
        this.scriptId = scriptId;
        this.strings = strings;
    }

    public void refreshPresences() {
//        try {
//            CompletableFuture<List<Presence>> getPresence = api.doGetPresence(scriptId);
//            getPresence.thenAcceptAsync((p) -> {
//                p.sort(new PresenceComparator(sortOrder));
//                presences.postValue(p);
//            });
//
//        } catch (Exception e) {
//            presences.setValue(Collections.emptyList());
//            errorMessage.setValue(strings.get(R.string.error_load_presence));
//        }
    }

    public void setSortOrder(PresenceSortOrder sortOrder) {
        this.sortOrder = sortOrder;
        List<Presence> value = this.presences.getValue();
        if (value != null) {
            value.sort(new PresenceComparator(sortOrder));
            this.presences.setValue(value);
        }
    }

    public Query getChildren() {
        return childRepository.getAllQuery();
    }
}
