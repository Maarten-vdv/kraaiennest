package com.kraaiennest.opvang.model;

import android.os.Parcel;
import android.os.Parcelable;

public class FoundChildId implements Parcelable {

    private String id;
    private FoundChildIdType type;

    public FoundChildId(String id, FoundChildIdType typ) {
        this.id = id;
        this.type = typ;
    }

    protected FoundChildId(Parcel in) {
        id = in.readString();
        type = FoundChildIdType.valueOf(in.readString());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(type.name());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FoundChildId> CREATOR = new Creator<FoundChildId>() {
        @Override
        public FoundChildId createFromParcel(Parcel in) {
            return new FoundChildId(in);
        }

        @Override
        public FoundChildId[] newArray(int size) {
            return new FoundChildId[size];
        }
    };

    public String getId() {
        return id;
    }

    public FoundChildIdType getType() {
        return type;
    }
}
