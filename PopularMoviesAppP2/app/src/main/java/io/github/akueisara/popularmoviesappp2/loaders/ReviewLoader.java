package io.github.akueisara.popularmoviesappp2.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

import io.github.akueisara.popularmoviesappp2.Utility;
import io.github.akueisara.popularmoviesappp2.models.Review;

public class ReviewLoader extends AsyncTaskLoader<List<Review>> {
    private String mUrl;

    public ReviewLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Review> loadInBackground() {
        if (mUrl.length() < 1 || mUrl == null) {
            return null;
        }

        List<Review> reviews = Utility.fetchReviewData(mUrl);

        return reviews;
    }
}
