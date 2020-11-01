package com.kraaiennest.kraaiennestapp.presence;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.kraaiennest.kraaiennestapp.R;
import com.kraaiennest.kraaiennestapp.model.Presence;
import com.kraaiennest.kraaiennestapp.model.Timestamp;

import java.util.Date;
import java.util.List;

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
        holder.mItem = data.get(position);
        holder.mIdView.setText(data.get(position).getChild().getFirstName());
        holder.mContentView.setText(data.get(position).getTimestamps().stream()
                .map(t -> t.getRegisteredAt().toString()).reduce("", String::concat));
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
        public final TextView mIdView;
        public final TextView mContentView;
        public Presence mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
