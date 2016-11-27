package io.github.akueisara.popularmoviesappp2;

import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.akueisara.popularmoviesappp2.data.MovieContract.MovieEntry;
import io.github.akueisara.popularmoviesappp2.data.MovieContract.FavoritesEntry;
import io.github.akueisara.popularmoviesappp2.loaders.ReviewLoader;
import io.github.akueisara.popularmoviesappp2.loaders.TrailerLoader;
import io.github.akueisara.popularmoviesappp2.models.Review;
import io.github.akueisara.popularmoviesappp2.models.Trailer;

public class DetailFragment extends Fragment {
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    static final String DETAIL_URI = "URI";
    private Uri mUri;
    private String mSortBy;

    private static final String MOVIE_SHARE_HASHTAG = " #PopMoviesApp";

    private ShareActionProvider mShareActionProvider;

    private String mMovieShareContent;
    private int mMovieID;
    private String mTitle;
    private String mPosterURL;
    private String mDate;
    private Double mVoteAverage;
    private String mOverview;
    private String[] mArgs;
    private ContentValues mCV;

    private static final int DETAIL_LOADER = 0;
    private static final int TRAILER_LOADER = 1;
    private static final int REVIEW_LOADER = 2;

    private static final String MOVIE_REQUEST_URL =
            "https://api.themoviedb.org/3/movie/";

    @BindView(R.id.movie_title)
    TextView mMovieTitleTextView;
    @BindView(R.id.movie_date)
    TextView mMovieDateTextView;
    @BindView(R.id.movie_image)
    ImageView mMovieImageView;
    @BindView(R.id.movie_rate)
    TextView mMovieRateTextView;
    @BindView(R.id.movie_vote_average)
    TextView mMovieVoteAverageTextView;
    @BindView(R.id.fav_button)
    Button mFavButton;
    @BindView(R.id.movie_overview)
    TextView mMovieOverviewTextView;
    @BindView(R.id.movie_trailer)
    TextView mMovieTrailerTextView;
    @BindView(R.id.trailer_recyclerview)
    RecyclerView mTrailerRecyclerView;
    @BindView(R.id.trailer_empty_view)
    TextView mTrailerEmptyStateTextView;
    @BindView(R.id.trailer_loading_indicator)
    View mTrailerLoadingIndicator;
    @BindView(R.id.movie_review)
    TextView mMovieReviewTextView;
    @BindView(R.id.review_recyclerview)
    RecyclerView mReviewRecyclerView;
    @BindView(R.id.review_empty_view)
    TextView mReviewEmptyStateTextView;
    @BindView(R.id.review_loading_indicator)
    View mReviewLoadingIndicator;

    private TrailerAdapter mTrailerAdapter;
    private ReviewAdapter mReviewAdapter;

    private static final String[] MOVIE_COLUMNS = {
            MovieEntry.TABLE_NAME + "." + MovieEntry.COLUMN_MOVIE_ID,
            MovieEntry.COLUMN_TITLE,
            MovieEntry.COLUMN_POSTER_IMG_URL,
            MovieEntry.COLUMN_DATE,
            MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieEntry.COLUMN_OVERVIEW
    };

    private static final String[] FAVORITE_COLUMNS = {
            FavoritesEntry.TABLE_NAME + "." + FavoritesEntry.COLUMN_MOVIE_ID,
            FavoritesEntry.COLUMN_TITLE,
            FavoritesEntry.COLUMN_POSTER_IMG_URL,
            FavoritesEntry.COLUMN_DATE,
            FavoritesEntry.COLUMN_VOTE_AVERAGE,
            FavoritesEntry.COLUMN_OVERVIEW
    };

    // These indices are tied to MOVIE_COLUMNS and FAVORITE_COLUMNS.  If MOVIE_COLUMNS or FAVORITE_COLUMNS changes, these
    // must change.
    static final int COL_MOVIE_ID = 0;
    static final int COL_MOVIE_TITLE = 1;
    static final int COL_MOVIE_POSTER_IMG_URL = 2;
    static final int COL_MOVIE_DATE = 3;
    static final int COL_MOVIE_VOTE_AVERAGE = 4;
    static final int COL_MOVIE_OVERVIEW = 5;


    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, rootView);

        mSortBy = Utility.getSortBy(getActivity());

        mTrailerAdapter = new TrailerAdapter(getContext(), new ArrayList<Trailer>(), new TrailerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Trailer trailer) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(trailer.getTrailerUrl()));

                // Verify that the intent will resolve to an activity
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        mTrailerRecyclerView.setAdapter(mTrailerAdapter);
        mTrailerRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mReviewAdapter = new ReviewAdapter(getContext(), new ArrayList<Review>(), new ReviewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Review review) {
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(review.getReviewUrl()));
//                startActivity(intent);
            }
        });

        mReviewRecyclerView.setAdapter(mReviewAdapter);
        mReviewRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (savedInstanceState != null && savedInstanceState.containsKey("uri")) {
            mUri = Uri.parse(savedInstanceState.getString("uri"));
            mTitle = savedInstanceState.getString("title");
            mPosterURL = savedInstanceState.getString("url");
            mDate = savedInstanceState.getString("date");
            mVoteAverage = savedInstanceState.getDouble("vote");
            mOverview = savedInstanceState.getString("overview");
            mArgs = savedInstanceState.getStringArray("args");
            mCV = savedInstanceState.getParcelable("cv");
            updateView(mTitle, mPosterURL, mDate, mVoteAverage, mOverview, mArgs, mCV);
        }

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detailfragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (mMovieShareContent != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mMovieShareContent + MOVIE_SHARE_HASHTAG);
        return shareIntent;
    }

    public void onSortByChanged() {
        String sortBy = Utility.getSortBy(getActivity());
        if ( mUri != null ){
            mUri = Uri.parse(MovieEntry.CONTENT_ITEM_TYPE);
            if (sortBy.equals(getString(R.string.pref_sort_by_favorite_value))){
                mUri = Uri.parse(FavoritesEntry.CONTENT_ITEM_TYPE);
            }
            initLoaders();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if ( null != mUri ) {
            outState.putString("uri", mUri.toString());
            outState.putString("title", mTitle);
            outState.putString("url", mPosterURL);
            outState.putString("date", mDate);
            outState.putDouble("vote", mVoteAverage);
            outState.putString("overview", mOverview);
            outState.putString("button_text", mFavButton.getText().toString());
            outState.putStringArray("args", mArgs);
            outState.putParcelable("cv", mCV);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if(mUri != null) {
            initLoaders();
        }
        super.onActivityCreated(savedInstanceState);
    }

    private void initLoaders() {
        getLoaderManager().initLoader(DETAIL_LOADER, null, new CursorLoaderCallbacks());
        if (Utility.checkNetworkInfo(getActivity()) != null && Utility.checkNetworkInfo(getActivity()).isConnected()) {
            getLoaderManager().initLoader(TRAILER_LOADER, null, new TrailerLoaderCallbacks());
            getLoaderManager().initLoader(REVIEW_LOADER, null, new ReviewLoaderCallbacks());
        } else {
            mTrailerEmptyStateTextView.setVisibility(View.VISIBLE);
            mReviewEmptyStateTextView.setVisibility(View.VISIBLE);
            mTrailerEmptyStateTextView.setText(R.string.no_internet_connection);
            mReviewEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    public class CursorLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if ( null != mUri ) {
                String[] projection = MOVIE_COLUMNS;
                if(mSortBy.equals(getString(R.string.pref_sort_by_favorite_value))) {
                    projection = FAVORITE_COLUMNS;
                }
                // Now create and return a CursorLoader that will take care of
                // creating a Cursor for the data being displayed.
                return new CursorLoader(
                        getActivity(),
                        mUri,
                        projection,
                        null,
                        null,
                        null
                );
            }
            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data != null && data.moveToFirst()) {
                mMovieID = data.getInt(COL_MOVIE_ID);

                // Read movie title from cursor and update view
                mTitle = data.getString(COL_MOVIE_TITLE);

                // Read poster url from cursor and update view
                mPosterURL = data.getString(COL_MOVIE_POSTER_IMG_URL);

                // Read movie date from cursor and update view
                mDate = data.getString(COL_MOVIE_DATE);

                // Read movie rating from cursor and update view
                mVoteAverage = data.getDouble(COL_MOVIE_VOTE_AVERAGE);

                // Read movie overview from cursor and update view
                mOverview = data.getString(COL_MOVIE_OVERVIEW);

                mArgs = new String[]{data.getString(COL_MOVIE_ID)};
                mCV = getFavoriteContentValues(data);

                updateView(mTitle, mPosterURL, mDate, mVoteAverage, mOverview, mArgs, mCV);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }

    private void updateView(String title, String posterURL, String date, Double voteAverage, String overview, String[] arg, ContentValues cv) {
        mMovieTitleTextView.setText(title);
        mMovieTitleTextView.setBackgroundColor(getResources().getColor(R.color.TitleBackGroundColor));

        Picasso.with(mMovieImageView.getContext())
                .load(posterURL)
                .into(mMovieImageView);

        mMovieDateTextView.setText(date.substring(0, 4));

        mMovieRateTextView.setText(getResources().getString(R.string.movie_rate));

        mMovieVoteAverageTextView.setText(voteAverage + "/10");

        mMovieOverviewTextView.setText(overview);

        mMovieTrailerTextView.setText(getResources().getString(R.string.movie_trailer));

        mMovieReviewTextView.setText(getResources().getString(R.string.movie_review));

        final String selection = FavoritesEntry.TABLE_NAME +"."+ FavoritesEntry.COLUMN_MOVIE_ID + " = ? ";
        final String[] args = arg;
        final ContentValues cvFav = cv;
        final AsyncQueryHandler handler = new AsyncQueryHandler(getContext().getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                if (cursor.moveToFirst())
                    mFavButton.setText(getString(R.string.remove_favo));
            }

            @Override
            protected void onInsertComplete(int token, Object cookie, Uri uri) {
                mFavButton.setText(getString(R.string.remove_favo));
                Toast.makeText(getContext(), getString(R.string.successfully_add_favo), Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onDeleteComplete(int token, Object cookie, int result) {
                mFavButton.setText(getString(R.string.add_favo));
                Toast.makeText(getContext(), getString(R.string.successfully_remove_favo), Toast.LENGTH_SHORT).show();
            }
        };

        mFavButton.setVisibility(View.VISIBLE);
        mFavButton.setText(getString(R.string.add_favo));
        handler.startQuery(0, null, FavoritesEntry.CONTENT_URI, null, selection, args,null);

        mFavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFavButton.getText().equals(getString(R.string.remove_favo))){
                    handler.startDelete(0, null, FavoritesEntry.CONTENT_URI, selection, args);
                }else {
                    handler.startInsert(0, null, FavoritesEntry.CONTENT_URI, cvFav);
                }
            }
        });


    }

    private ContentValues getFavoriteContentValues(Cursor data) {
        ContentValues cv = new ContentValues();
        if (data.moveToFirst()) {
            cv.put(FavoritesEntry.COLUMN_MOVIE_ID, mMovieID);
            cv.put(FavoritesEntry.COLUMN_TITLE, mTitle);
            cv.put(FavoritesEntry.COLUMN_POSTER_IMG_URL, mPosterURL);
            cv.put(FavoritesEntry.COLUMN_DATE, mDate);
            cv.put(FavoritesEntry.COLUMN_VOTE_AVERAGE, mVoteAverage);
            cv.put(FavoritesEntry.COLUMN_OVERVIEW, mOverview);
        }

        return cv;
    }

    public class TrailerLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<Trailer>> {

        @Override
        public Loader<List<Trailer>> onCreateLoader(int id, Bundle args) {
            if ( null != mUri ) {
                String[] projection = MOVIE_COLUMNS;
                if(mSortBy.equals(getString(R.string.pref_sort_by_favorite_value))) {
                    projection = FAVORITE_COLUMNS;
                }
                CursorLoader cursorLoader = new CursorLoader(getActivity(),
                        mUri,
                        projection,
                        null,
                        null,
                        null);
                Cursor cursor = cursorLoader.loadInBackground();
                if (cursor != null && cursor.moveToFirst()) {
                    mMovieID = cursor.getInt(COL_MOVIE_ID);
                }
                cursor.close();
            }

            if("".equals(mMovieID)) {
                return null;
            }

            mTrailerLoadingIndicator.setVisibility(View.VISIBLE);
            Uri baseUri = Uri.parse(MOVIE_REQUEST_URL + mMovieID + "/videos");
            Uri.Builder uriBuilder = baseUri.buildUpon();
            uriBuilder.appendQueryParameter("api_key", "b7c08ce2e5830c458a57fddfb7976b13");
            uriBuilder.appendQueryParameter("language","en-US");
            return new TrailerLoader(getContext(), uriBuilder.toString());
        }

        @Override
        public void onLoadFinished(Loader<List<Trailer>> loader, List<Trailer> trailers) {
            mTrailerAdapter.clear();

            if (trailers != null && !trailers.isEmpty()) {
                mTrailerLoadingIndicator.setVisibility(View.GONE);
                mTrailerEmptyStateTextView.setVisibility(View.GONE);
                mTrailerRecyclerView.setVisibility(View.VISIBLE);
                mTrailerAdapter.addAll(trailers);

                mMovieShareContent = String.format("%s - %s",mTitle ,trailers.get(0).getTrailerUrl());
            }
            else {
                mTrailerLoadingIndicator.setVisibility(View.GONE);
                mTrailerRecyclerView.setVisibility(View.GONE);
                mTrailerEmptyStateTextView.setVisibility(View.VISIBLE);
                mTrailerEmptyStateTextView.setText(R.string.no_trailer);
            }

            // If onCreateOptionsMenu has already happened, we need to update the share intent now.
            if (mShareActionProvider != null && mMovieShareContent != null) {
                mShareActionProvider.setShareIntent(createShareForecastIntent());
            }
        }

        @Override
        public void onLoaderReset(Loader<List<Trailer>> loader) {
            mTrailerAdapter.clear();
        }

    }

    public class ReviewLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<Review>> {

        @Override
        public Loader<List<Review>>  onCreateLoader(int id, Bundle args) {
            if ( null != mUri ) {
                String[] projection = MOVIE_COLUMNS;
                if(mSortBy.equals(getString(R.string.pref_sort_by_favorite_value))) {
                    projection = FAVORITE_COLUMNS;
                }
                CursorLoader cursorLoader = new CursorLoader(getActivity(),
                        mUri,
                        projection,
                        null,
                        null,
                        null);
                Cursor cursor = cursorLoader.loadInBackground();
                if (cursor != null && cursor.moveToFirst()) {
                    mMovieID = cursor.getInt(COL_MOVIE_ID);
                }
                cursor.close();
            }
            if("".equals(mMovieID)) {
                return null;
            }
            mReviewLoadingIndicator.setVisibility(View.VISIBLE);
            Uri baseUri = Uri.parse(MOVIE_REQUEST_URL + mMovieID + "/reviews");
            Uri.Builder uriBuilder = baseUri.buildUpon();
            uriBuilder.appendQueryParameter("api_key", "b7c08ce2e5830c458a57fddfb7976b13");
            uriBuilder.appendQueryParameter("language","en-US");
            return new ReviewLoader(getContext(), uriBuilder.toString());
        }

        @Override
        public void onLoadFinished(Loader<List<Review>> loader, List<Review> reviews) {
            mReviewAdapter.clear();

            if (reviews != null && !reviews.isEmpty()) {
                mReviewLoadingIndicator.setVisibility(View.GONE);
                mReviewEmptyStateTextView.setVisibility(View.GONE);
                mReviewRecyclerView.setVisibility(View.VISIBLE);
                mReviewAdapter.addAll(reviews);
            }
            else {
                mReviewLoadingIndicator.setVisibility(View.GONE);
                mReviewRecyclerView.setVisibility(View.GONE);
                mReviewEmptyStateTextView.setVisibility(View.VISIBLE);
                mReviewEmptyStateTextView.setText(R.string.no_review);
            }
        }

        @Override
        public void onLoaderReset(Loader<List<Review>> loader) {
            mReviewAdapter.clear();
        }
    }

}