package com.kraaiennest.opvang.activities.presence;

import com.kraaiennest.opvang.model.Presence;
import com.kraaiennest.opvang.model.PresenceSortOrder;

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
        }
        return o1.getChild().getFullName().compareTo(o2.getChild().getFullName());
    }
}
