package com.kraaiennest.kraaiennestapp.main;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.kraaiennest.kraaiennestapp.api.APIInterface;
import com.kraaiennest.kraaiennestapp.api.APIService;
import com.kraaiennest.kraaiennestapp.model.Child;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainViewModel extends ViewModel {

    private final APIInterface api;
    private String scriptId;
    private MutableLiveData<List<Child>> children;

    public MainViewModel() {
        api = APIService.getClient().create(APIInterface.class);
    }

    public void loadExtra(String scriptId) {
        this.scriptId = scriptId;
    }


    public MutableLiveData<List<Child>> getChildren() {
        if (children == null) {
            children = new MutableLiveData<>();
            loadChildren();
        }
        return children;
    }

    private List<Child> loadChildren() {
        try {
            List<Child> children = api.doGetChildren(scriptId).get();
            System.out.println(children.size());
            return children;
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        return Collections.emptyList();
    }
}
