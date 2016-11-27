package io.github.akueisara.popularmoviesappp2.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Trailer implements Parcelable {
    private String mID;
    private String mKey;
    private String mName;
    private String mSite;
    private int mSize;

    private Trailer() {

    }

    public Trailer(String id, String key, String name, String site, int size) {
        mID = id;
        mKey = key;
        mName = name;
        mSite = site;
        mSize = size;
    }

    protected Trailer(Parcel in) {
        mID = in.readString();
        mKey = in.readString();
        mName = in.readString();
        mSite = in.readString();
        mSize = in.readInt();
    }

    public static final Creator<Trailer> CREATOR = new Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mID);
        parcel.writeString(mKey);
        parcel.writeString(mName);
        parcel.writeString(mSite);
        parcel.writeInt(mSize);
    }

    public String getTrailerUrl() {
        return "http://www.youtube.com/watch?v=" + mKey;
    }
}
