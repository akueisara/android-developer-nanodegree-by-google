package io.github.akueisara.popularmoviesappp2.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

import io.github.akueisara.popularmoviesappp2.Utility;
import io.github.akueisara.popularmoviesappp2.models.Trailer;

public class TrailerLoader extends AsyncTaskLoader<List<Trailer>> {
    private String mUrl;

    public TrailerLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Trailer> loadInBackground() {
        if (mUrl.length() < 1 || mUrl == null) {
            return null;
        }

        List<Trailer> trailers = Utility.fetchTrailerData(mUrl);

        return trailers;
    }
}
