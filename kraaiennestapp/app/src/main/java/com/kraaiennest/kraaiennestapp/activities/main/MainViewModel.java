package com.kraaiennest.kraaiennestapp.activities.main;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.kraaiennest.kraaiennestapp.api.APIService;
import com.kraaiennest.kraaiennestapp.model.Child;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class MainViewModel extends ViewModel {

    APIService api;
    private String scriptId;
    private MutableLiveData<List<Child>> children;

    @ViewModelInject
    public MainViewModel(APIService api) {
        this.api = api;
    }

    public void loadExtra(String scriptId) {
        this.scriptId = scriptId;
    }


    public MutableLiveData<List<Child>> getChildren() {
        if (children == null) {
            children = new MutableLiveData<>();
            children.setValue(loadChildren());
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
