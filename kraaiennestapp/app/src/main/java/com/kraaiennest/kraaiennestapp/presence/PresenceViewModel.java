package com.kraaiennest.kraaiennestapp.presence;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.kraaiennest.kraaiennestapp.api.APIInterface;
import com.kraaiennest.kraaiennestapp.api.APIService;
import com.kraaiennest.kraaiennestapp.model.Presence;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class PresenceViewModel extends ViewModel {

    private MutableLiveData<List<Presence>> presences;
    public LiveData<List<Presence>> getPresences() {
        if (presences == null) {
            presences = new MutableLiveData<>();
            loadPresence();
        }
        return presences;
    }

    private void loadPresence() {
        APIInterface api = APIService.getClient().create(APIInterface.class);
        CompletableFuture<List<Presence>> getPresence = api.doGetPresence();
        try {
            presences.postValue(getPresence.get());

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
