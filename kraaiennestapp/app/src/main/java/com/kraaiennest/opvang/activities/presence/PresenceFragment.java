package com.kraaiennest.opvang.activities.presence;

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
import com.kraaiennest.opvang.R;
import com.kraaiennest.opvang.databinding.FragmentPresenceListBinding;
import dagger.hilt.android.AndroidEntryPoint;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A fragment representing a list of Items.
 */
@AndroidEntryPoint
public class PresenceFragment extends Fragment {

    private PresenceViewModel model;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = new ViewModelProvider(requireActivity()).get(PresenceViewModel.class);

        Map<Integer, String> strings = new HashMap<>();
        strings.put(R.string.error_load_presence, getString(R.string.error_load_presence));
        model.loadExtra(strings);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        com.kraaiennest.opvang.databinding.FragmentPresenceListBinding binding = FragmentPresenceListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        model.isEmpty().observe(getViewLifecycleOwner(), empty ->
                view.findViewById(R.id.swipeContainer1).setVisibility(empty ? View.VISIBLE : View.GONE));

        PresenceRecyclerAdapter presenceRecyclerAdapter
                = new PresenceRecyclerAdapter(Collections.emptyList());

        model.getPresences().observe(getViewLifecycleOwner(), presenceRecyclerAdapter::setData);

        RecyclerView recycleView = view.findViewById(R.id.list);
        // Set the adapter
        Context context = recycleView.getContext();
        recycleView.setNestedScrollingEnabled(false);
        recycleView.setLayoutManager(new LinearLayoutManager(context));
        recycleView.setAdapter(presenceRecyclerAdapter);

        initSwipeContainer(view.findViewById(R.id.swipeContainer1));
        initSwipeContainer(view.findViewById(R.id.swipeContainer2));

        model.refreshPresences();
    }

    private void initSwipeContainer(SwipeRefreshLayout swipeContainer) {
        swipeContainer.setRefreshing(false);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(() -> model.refreshPresences());
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }
}
