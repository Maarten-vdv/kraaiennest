package com.kraaiennest.opvang.activities.presence;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.firestore.*;
import com.google.android.flexbox.FlexboxLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.kraaiennest.opvang.R;
import com.kraaiennest.opvang.model.*;
import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.kraaiennest.opvang.util.CollectionUtil.distinctByKey;

public class PresenceRecyclerAdapter extends RecyclerView.Adapter<PresenceRecyclerAdapter.PresenceViewHolder>
        implements ChangeEventListener, LifecycleObserver {

    private static final String TAG = "FirestoreRecycler";
    private ObservableSnapshotArray<Registration> registrationSnapshots;
    private ObservableSnapshotArray<CheckIn> checkInSnapshots;
    private ObservableSnapshotArray<Child> childSnapshots;

    private List<Presence> precenses = new ArrayList<>();

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     */
    public PresenceRecyclerAdapter(LifecycleOwner owner, Query registrations, Query checkIns, Query children) {
        registrationSnapshots = new FirestoreArray<>(registrations, MetadataChanges.EXCLUDE, new ClassSnapshotParser<>(Registration.class));
        checkInSnapshots = new FirestoreArray<>(checkIns, MetadataChanges.EXCLUDE, new ClassSnapshotParser<>(CheckIn.class));
        childSnapshots = new FirestoreArray<>(children, MetadataChanges.EXCLUDE, new ClassSnapshotParser<>(Child.class));

        owner.getLifecycle().addObserver(this);
    }

    /**
     * Start listening for database changes and populate the adapter.
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void startListening() {
        if (!registrationSnapshots.isListening(this)) {
            registrationSnapshots.addChangeEventListener(this);
        }
        if (!checkInSnapshots.isListening(this)) {
            checkInSnapshots.addChangeEventListener(this);
        }
        if (!childSnapshots.isListening(this)) {
            childSnapshots.addChangeEventListener(this);
        }
    }

    /**
     * Stop listening for database changes and clear all items in the adapter.
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void stopListening() {
        registrationSnapshots.removeChangeEventListener(this);
        checkInSnapshots.removeChangeEventListener(this);
        childSnapshots.removeChangeEventListener(this);
        notifyDataSetChanged();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void cleanup(LifecycleOwner source) {
        source.getLifecycle().removeObserver(this);
    }

    /**
     * Gets the item at the specified position from the backing snapshot array.
     *
     * @see ObservableSnapshotArray#get(int)
     */
    @NonNull
    public Presence getItem(int position) {
        return precenses.get(position);
    }

    /**
     * Gets the size of snapshots in adapter.
     *
     * @return the total count of snapshots in adapter.
     * @see ObservableSnapshotArray#size()
     */
    @Override
    public int getItemCount() {
        return precenses.size();
    }

    @Override
    public void onChildChanged(@NonNull ChangeEventType type,
                               @NonNull DocumentSnapshot snapshot,
                               int newIndex,
                               int oldIndex) {
        List<Registration> registrations = registrationSnapshots.stream().filter(distinctByKey(Registration::getChildId)).collect(Collectors.toList());

        // if the registration is a morning registration or there is a checking (during evening)
        precenses = registrations.stream()
                .filter(registration -> registration.getPartOfDay() == PartOfDay.O || checkInSnapshots.stream().anyMatch(c -> c.getChildId().equals(registration.getChildId())))
                .map(registration -> {
                    List<Timestamp> timestamps = registrationSnapshots.stream().filter(r -> r.getChildId().equals(registration.getChildId())).map(Registration::getRegistrationTime).collect(Collectors.toList());
                    Presence presence = new Presence();
                    presence.setChild(childSnapshots.stream().filter(c -> c.getId().equals(registration.getChildId())).findFirst().orElse(null));
                    presence.setRegistrationTimes(timestamps);
                    return presence;
                }).collect(Collectors.toList());

        notifyDataSetChanged();
    }

    @Override
    public void onDataChanged() {
    }

    @Override
    public void onError(@NonNull FirebaseFirestoreException e) {
        Log.w(TAG, "onError", e);
    }

    @Override
    public void onBindViewHolder(@NonNull PresenceViewHolder holder, int position) {
        onBindViewHolder(holder, position, getItem(position));
    }


    @Override
    public void onBindViewHolder(@NonNull @NotNull PresenceRecyclerAdapter.PresenceViewHolder holder, int position, @NonNull @NotNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    public void onBindViewHolder(PresenceViewHolder holder, int position, Presence presence) {
        holder.childNameView.setText(presence.getChild().getFirstName() + " " + presence.getChild().getLastName());
        holder.childGroupView.setText(presence.getChild().getGroup());

        DateFormat dateFormat = DateFormat.getTimeInstance();
        Context context = holder.childNameView.getContext();
        holder.timestampsView.removeAllViews();
        presence.getRegistrationTimes().forEach(t -> holder.timestampsView.addView(createTimestampView(context, dateFormat, t)));
    }

    @Override
    public PresenceViewHolder onCreateViewHolder(ViewGroup group, int i) {
        // Create a new instance of the ViewHolder, in this case we are using a custom
        // layout called R.layout.message for each item
        View view = LayoutInflater.from(group.getContext())
                .inflate(R.layout.fragment_presence, group, false);

        return new PresenceViewHolder(view);
    }

    private TextView createTimestampView(Context context, DateFormat dateFormat, Timestamp t) {
        TextView view = new TextView(context);
        view.setText(dateFormat.format(t.toDate()));
        view.setTextSize(18);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        params.setMargins(20, 0, 20, 0);
        view.setLayoutParams(params);
        return view;
    }

    public static class PresenceViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView childNameView;
        public final TextView childGroupView;
        public final FlexboxLayout timestampsView;
        public final LinearLayout presenseItemLayout;

        public Presence item;

        public PresenceViewHolder(View view) {
            super(view);
            mView = view;
            childNameView = view.findViewById(R.id.child_name);
            childGroupView = view.findViewById(R.id.child_group);
            timestampsView = view.findViewById(R.id.timestamps);
            presenseItemLayout = view.findViewById(R.id.presenseItemLayout);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + item.toString() + "'";
        }
    }
}
