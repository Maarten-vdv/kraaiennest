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
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.kraaiennest.opvang.R;
import com.kraaiennest.opvang.databinding.FragmentPresenceListBinding;
import com.kraaiennest.opvang.model.Registration;
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
    private PresenceRecyclerAdapter presenceRecyclerAdapter;
    private List<SwipeRefreshLayout> swipeContainers = new ArrayList<>();
    private FragmentPresenceListBinding binding;
    private Query mQuery;
    private FirebaseFirestore mFirestore;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPresenceListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
//        model.isEmpty().observe(getViewLifecycleOwner(), empty ->
//                view.findViewById(R.id.swipeContainer1).setVisibility(empty ? View.VISIBLE : View.GONE));

        // Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get ${LIMIT} restaurants
        mQuery = mFirestore.collection("registrations")
                .orderBy("registrationTime", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Registration> options = new FirestoreRecyclerOptions.Builder<Registration>()
                .setQuery(mQuery, Registration.class)
                .setLifecycleOwner(getViewLifecycleOwner())
                .build();


        presenceRecyclerAdapter = new PresenceRecyclerAdapter(this, model.getRegistrations(), model.getChildren(), model.getCheckIns());

        RecyclerView recycleView = view.findViewById(R.id.list);
        // Set the adapter
        Context context = recycleView.getContext();
        recycleView.setNestedScrollingEnabled(false);
        recycleView.setLayoutManager(new LinearLayoutManager(context));
        recycleView.setAdapter(presenceRecyclerAdapter);

        initSwipeContainer(view.findViewById(R.id.swipeContainer1));
        initSwipeContainer(view.findViewById(R.id.swipeContainer2));
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

        swipeContainers.add(swipeContainer);
    }
}
