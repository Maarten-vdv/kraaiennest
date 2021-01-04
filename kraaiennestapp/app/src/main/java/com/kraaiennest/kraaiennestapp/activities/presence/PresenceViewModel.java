package com.kraaiennest.kraaiennestapp.activities.presence;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import com.kraaiennest.kraaiennestapp.R;
import com.kraaiennest.kraaiennestapp.api.APIService;
import com.kraaiennest.kraaiennestapp.model.Presence;
import com.kraaiennest.kraaiennestapp.model.PresenceSortOrder;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class PresenceViewModel extends ViewModel {

    private final APIService api;
    private String scriptId;
    private MutableLiveData<List<Presence>> presences;

    private MutableLiveData<String> errorMessage;

    Map<Integer, String> strings;
    private PresenceSortOrder sortOrder = PresenceSortOrder.NAME;

    @ViewModelInject
    public PresenceViewModel(APIService api) {
        this.api = api;
    }

    public LiveData<List<Presence>> getPresences() {
        if (presences == null) {
            presences = new MutableLiveData<>();
            refreshPresences();
        }
        return presences;
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

    public LiveData<Boolean> isEmpty() {
        return Transformations.map(getPresences(), p -> p != null && p.isEmpty());
    }


    public void refreshPresences() {
        try {
            CompletableFuture<List<Presence>> getPresence = api.doGetPresence(scriptId);
            getPresence.thenAcceptAsync((p) -> {
                p.sort(new PresenceComparator(sortOrder));
                presences.postValue(p);
            });

        } catch (Exception e) {
            presences.setValue(Collections.emptyList());
            errorMessage.setValue(strings.get(R.string.error_load_presence));
        }
    }

    public void setSortOrder(PresenceSortOrder sortOrder) {
        this.sortOrder = this.sortOrder;
        List<Presence> value = this.presences.getValue();
        if (value != null) {
            value.sort(new PresenceComparator(sortOrder));
            this.presences.setValue(value);
        }
    }
}
