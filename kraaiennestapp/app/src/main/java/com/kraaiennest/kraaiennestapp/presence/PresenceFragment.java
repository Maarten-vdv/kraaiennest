package com.kraaiennest.kraaiennestapp.presence;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.kraaiennest.kraaiennestapp.R;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 */
public class PresenceFragment extends Fragment {

    private PresenceViewModel model;
    private PresenceRecyclerViewAdapter presenceAdapter;
    private String scriptId;
    private SwipeRefreshLayout swipeContainer;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PresenceFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        model = new ViewModelProvider(this).get(PresenceViewModel.class);
        scriptId = sharedPreferences.getString("scriptId", "");
        model.loadExtra(scriptId);
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
        recycleView.setLayoutManager(new LinearLayoutManager(context));
        recycleView.setAdapter(presenceAdapter);

        swipeContainer = view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(() -> {
            // Your code to refresh the list here.
            // Make sure you call swipeContainer.setRefreshing(false)
            // once the network request has completed successfully.
            model.refreshPresences();
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        model.getPresences().observe(this, newList -> {
            presenceAdapter.setData(newList);
            swipeContainer.setRefreshing(false);
        });

        return view;
    }
}
