package com.kraaiennest.kraaiennestapp.activities.presence;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.flexbox.FlexboxLayout;
import com.kraaiennest.kraaiennestapp.R;
import com.kraaiennest.kraaiennestapp.model.Child;
import com.kraaiennest.kraaiennestapp.model.Presence;
import com.kraaiennest.kraaiennestapp.model.Timestamp;

import java.text.SimpleDateFormat;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class PresenceRecyclerViewAdapter extends RecyclerView.Adapter<PresenceRecyclerViewAdapter.ViewHolder> {

    private final List<Presence> data;

    public PresenceRecyclerViewAdapter(List<Presence> items) {
        data = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_presence, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = data.get(position);
        Child child = data.get(position).getChild();
        holder.childNameView.setText(child.getFirstName() + " " + child.getLastName());
        holder.childGroupView.setText(child.getGroup());

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Context context = holder.childNameView.getContext();
        holder.timestampsView.removeAllViews();
        holder.item.getTimestamps().forEach(t -> holder.timestampsView.addView(createTimestampView(context, dateFormat, t)));

        if(!holder.item.getTimestamps().stream().allMatch(Timestamp::isCheckIn)) {
            holder.presenseItemLayout.setAlpha(0.25f);
        } else {
            holder.presenseItemLayout.setAlpha(1f);
        }
    }

    private TextView createTimestampView(Context context, SimpleDateFormat dateFormat, com.kraaiennest.kraaiennestapp.model.Timestamp t) {
        TextView view = new TextView(context);
        view.setText(dateFormat.format(t.getRegisteredAt()));
        view.setTextSize(18);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        params.setMargins(20,0,20,0);
        view.setLayoutParams(params);
        return view;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<Presence> newData) {
        this.data.clear();
        this.data.addAll(newData);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView childNameView;
        public final TextView childGroupView;
        public final FlexboxLayout timestampsView;
        public final LinearLayout presenseItemLayout;
        public Presence item;

        public ViewHolder(View view) {
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
