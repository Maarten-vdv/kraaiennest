package com.kraaiennest.kraaiennestapp.presence;

import com.kraaiennest.kraaiennestapp.model.Presence;
import com.kraaiennest.kraaiennestapp.model.PresenceSortOrder;
import com.kraaiennest.kraaiennestapp.model.Timestamp;

import java.util.Comparator;

public class PresenceComparator implements Comparator<Presence> {

    private PresenceSortOrder sortOrder;

    public PresenceComparator(PresenceSortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Override
    public int compare(Presence o1, Presence o2) {
        int compare;

        switch (sortOrder) {
            case NAME:
                return o1.getChild().getFullName().compareTo(o2.getChild().getFullName());
            case GROUP:
                compare = o1.getChild().getGroup().compareTo(o2.getChild().getGroup());
                if (compare == 0) {
                    compare = o1.getChild().getFullName().compareTo(o2.getChild().getFullName());
                }
                return compare;
            case PRESENCE:
                compare = Boolean.compare(o1.getTimestamps().stream().noneMatch(Timestamp::isCheckIn), o2.getTimestamps().stream().noneMatch(Timestamp::isCheckIn));
                if (compare == 0) {
                    compare = o1.getChild().getFullName().compareTo(o2.getChild().getFullName());
                }
                return compare;
        }
        return o1.getChild().getFullName().compareTo(o2.getChild().getFullName());
    }
}
