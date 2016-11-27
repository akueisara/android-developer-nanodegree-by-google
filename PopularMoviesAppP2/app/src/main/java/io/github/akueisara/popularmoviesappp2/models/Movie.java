package io.github.akueisara.popularmoviesappp2.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {
    private int mID;
    private String mTitle;
    private String mReleaseDate;
    private String mPosterImageUrl;
    private Double mVoteAverage;
    private String mOverview;

    // Only for createFromParcel
    private Movie(){

    }

    public Movie(int id, String title, String date, String posterUrl, Double voteAverage, String overview) {
        this.mID = id;
        this.mTitle = title;
        this.mReleaseDate = date;
        this.mPosterImageUrl = posterUrl;
        this.mVoteAverage = voteAverage;
        this.mOverview = overview;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeInt(mID);
        out.writeString(mTitle);
        out.writeString(mReleaseDate);
        out.writeString(mPosterImageUrl);
        out.writeDouble(mVoteAverage);
        out.writeString(mOverview);
    }

    public Movie(Parcel in) {
        this.mID = in.readInt();
        this.mTitle = in.readString();
        this.mReleaseDate = in.readString();
        this.mPosterImageUrl = in.readString();
        this.mVoteAverage = in.readDouble();
        this.mOverview = in.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Movie> CREATOR
            = new Creator<Movie>() {

        // This simply calls our new constructor (typically private) and
        // passes along the unmarshalled `Parcel`, and then returns the new object!
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        // We just need to copy this and change the type to match our class.
        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public int getID() {
        return mID;
    }

    public void setID(int id) {
        mID = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public void setReleaseDate(String date) {
        mReleaseDate = date;
    }

    public String getPosterImageURL() {
        return mPosterImageUrl;
    }

    public void setPosterImageURL(String url) {
        mPosterImageUrl = url;
    }

    public Double getVoteAverage() {
        return mVoteAverage;
    }

    public void setVoteAverage(Double vote) {
        mVoteAverage = vote;
    }

    public String getOverview() {
        return mOverview;
    }

    public void setOverview(String overview) {
        mOverview = overview;
    }
}
