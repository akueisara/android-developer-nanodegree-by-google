package io.github.akueisara.popularmoviesappp2.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Review implements Parcelable {
    private String mID;
    private String mAuthor;
    private String mContent;

    private Review() {

    }

    public Review(String id, String author, String content) {
        mID = id;
        mAuthor = author;
        mContent = content;
    }

    protected Review(Parcel in) {
        mID = in.readString();
        mAuthor = in.readString();
        mContent = in.readString();
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mID);
        parcel.writeString(mAuthor);
        parcel.writeString(mContent);
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getContent() {
        return mContent;
    }

    public String getReviewUrl() {
        return "https://www.themoviedb.org/review/" + mID;
    }
}
