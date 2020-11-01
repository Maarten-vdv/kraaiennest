package com.kraaiennest.kraaiennestapp.presence;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.kraaiennest.kraaiennestapp.R;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 */
public class PresenceFragment extends Fragment {

    private static final String ARG_ITEMS = "column-count";
    private PresenceViewModel model;
    private PresenceRecyclerViewAdapter presenceAdapter;
    private RecyclerView recyclerView;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PresenceFragment() {
        System.out.println("created!");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the ViewModel.
        model = new ViewModelProvider(this).get(PresenceViewModel.class);
        // Create the observer which updates the UI.
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_presence_list, container, false);

        RecyclerView recycleView = view.findViewById(R.id.list);
        // Set the adapter
        Context context = recycleView.getContext();

        presenceAdapter = new PresenceRecyclerViewAdapter(new ArrayList<>());

        recycleView.setNestedScrollingEnabled(false);
        recycleView.setLayoutManager(new GridLayoutManager(context, 1));
        recycleView.setLayoutManager(new LinearLayoutManager(context));
        recycleView.setAdapter(presenceAdapter);

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        model.getPresences().observe(this, newList -> {
            presenceAdapter.setData(newList);
        });

        return view;
    }
}
