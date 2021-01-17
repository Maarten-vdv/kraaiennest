package com.kraaiennest.kraaiennestapp.activities.presence;

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
import dagger.hilt.android.AndroidEntryPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A fragment representing a list of Items.
 */
@AndroidEntryPoint
public class PresenceFragment extends Fragment {

    private PresenceViewModel model;
    private PresenceRecyclerViewAdapter presenceAdapter;
    private List<SwipeRefreshLayout> swipeContainers = new ArrayList<>();

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


        model = new ViewModelProvider(requireActivity()).get(PresenceViewModel.class);

        String scriptId = sharedPreferences.getString("scriptId", "");
        Map<Integer, String> strings = new HashMap<>();
        strings.put(R.string.error_load_presence, getString(R.string.error_load_presence));
        model.loadExtra(scriptId, strings);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_presence_list, container, false);

        model.isEmpty().observe(getViewLifecycleOwner(), empty ->
                view.findViewById(R.id.swipeContainer1).setVisibility(empty ? View.VISIBLE : View.GONE));

        RecyclerView recycleView = view.findViewById(R.id.list);
        // Set the adapter
        Context context = recycleView.getContext();

        presenceAdapter = new PresenceRecyclerViewAdapter(new ArrayList<>());

        recycleView.setNestedScrollingEnabled(false);
        recycleView.setLayoutManager(new LinearLayoutManager(context));
        recycleView.setAdapter(presenceAdapter);

        initSwipeContainer(view.findViewById(R.id.swipeContainer1));
        initSwipeContainer(view.findViewById(R.id.swipeContainer2));

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        model.getPresences().observe(getViewLifecycleOwner(), newList -> {
            presenceAdapter.setData(newList);
            swipeContainers.forEach(c -> c.setRefreshing(false));
        });

        return view;
    }

    private void initSwipeContainer(SwipeRefreshLayout swipeContainer) {
        swipeContainer.setRefreshing(true);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(() -> model.refreshPresences());
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeContainers.add(swipeContainer);
    }
}
