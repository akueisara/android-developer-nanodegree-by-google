package io.github.akueisara.popularmoviesapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by akueisara on 10/24/2016.
 */

public class Movie implements Parcelable {
    private String mTitle;
    private String mReleaseDate;
    private String mPosterImageURL;
    private Double mVoteAverage;
    private String mPlotSynopsis;

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeString(mTitle);
        out.writeString(mReleaseDate);
        out.writeString(mPosterImageURL);
        out.writeDouble(mVoteAverage);
        out.writeString(mPlotSynopsis);
    }

    public Movie(Parcel in) {
        this.mTitle = in.readString();
        this.mReleaseDate = in.readString();
        this.mPosterImageURL = in.readString();
        this.mVoteAverage = in.readDouble();
        this.mPlotSynopsis = in.readString();
    }

    public Movie(){

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>() {

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
        return mPosterImageURL;
    }

    public void setPosterImageURL(String url) {
        mPosterImageURL = url;
    }

    public Double getVoteAverage() {
        return mVoteAverage;
    }

    public void setVoteAverage(Double vote) {
        mVoteAverage = vote;
    }

    public String getPlotSynopsis() {
        return mPlotSynopsis;
    }

    public void setPlotSynopsis(String plot) {
        mPlotSynopsis = plot;
    }
}
