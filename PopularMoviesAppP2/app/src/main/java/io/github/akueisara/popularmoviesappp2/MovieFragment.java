package io.github.akueisara.popularmoviesappp2;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.akueisara.popularmoviesappp2.data.MovieContract.MovieEntry;
import io.github.akueisara.popularmoviesappp2.data.MovieContract.FavoritesEntry;
import io.github.akueisara.popularmoviesappp2.data.MovieDbHelper;
import io.github.akueisara.popularmoviesappp2.sync.PopMovieSyncAdapter;

public class MovieFragment extends Fragment {
    private static final int MOVIE_LOADER = 0;

    private static final String[] MOVIE_COLUMNS = {
            MovieEntry.TABLE_NAME + "." + MovieEntry._ID,
            MovieEntry.COLUMN_MOVIE_ID,
            MovieEntry.COLUMN_TITLE,
            MovieEntry.COLUMN_POSTER_IMG_URL,
            MovieEntry.COLUMN_DATE,
            MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieEntry.COLUMN_OVERVIEW
    };

    private static final String[] FAVORITE_COLUMNS = {
            FavoritesEntry.TABLE_NAME + "." + FavoritesEntry._ID,
            FavoritesEntry.COLUMN_MOVIE_ID,
            FavoritesEntry.COLUMN_TITLE,
            FavoritesEntry.COLUMN_POSTER_IMG_URL,
            FavoritesEntry.COLUMN_DATE,
            FavoritesEntry.COLUMN_VOTE_AVERAGE,
            FavoritesEntry.COLUMN_OVERVIEW
    };

    // These indices are tied to MOVIE_COLUMNS and FAVORITE_COLUMNS.  If MOVIE_COLUMNS or  FAVORITE_COLUMNS changes, these
    // must change.
    static final int COL_ID = 0;
    static final int COL_MOVIE_ID = 1;
    static final int COL_MOVIE_TITLE = 2;
    static final int COL_MOVIE_POSTER_IMG_URL = 3;
    static final int COL_MOVIE_DATE = 4;
    static final int COL_MOVIE_VOTE_AVERAGE = 5;
    static final int COL_MOVIE_OVERVIEW = 6;

    private Context mContext;
    private MovieCursorAdapter mMovieCursorAdapter;
    private GridLayoutManager mLayoutManager;
    public static int mPage = 1;
    private static final int TOTAL_PAGES = 999;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_view)
    TextView mEmptyStateTextView;

    private int mPosition = -1;

    private static final String SELECTED_KEY = "selected_position";

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        void onItemSelected(Uri dateUri);
    }

    public MovieFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.moviefragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            mPage = 1;
            updateMovie();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Context context) {
        this.mContext = context;
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);

        String sortBy = Utility.getSortBy(getContext());

        // The MovieCursorAdapter will take data from a source and
        // use it to populate the RecycleView it's attached to.
        mMovieCursorAdapter = new MovieCursorAdapter(getActivity(), null, new MovieCursorAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Cursor cursor, int position) {
                if (cursor != null) {
                    cursor.moveToPosition(position);
                    String sortBy = Utility.getSortBy(getActivity());
                    long id = cursor.getLong(COL_ID);
                    int movie_id = cursor.getInt(COL_MOVIE_ID);
                    Uri uri = MovieEntry.buildMovieUri(id);
                    if(sortBy.equals(getString(R.string.pref_sort_by_favorite_value))) {
                        uri = FavoritesEntry.buildFavoriteMovieUri(id);
                    }
                    ((Callback) getActivity())
                            .onItemSelected(uri);
                }
                mPosition = position;
            }
        });

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mMovieCursorAdapter);
        mLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // Load more if RecyclerView has reached the end and isn't already loading
                if (mLayoutManager.findLastVisibleItemPosition() == new MovieDbHelper(getContext()).getMovieTableRowCount() - 1) {
                    if (mPage < TOTAL_PAGES) {
                        updateMovie();
                        mPage++;
                    }
                }
            }
        });

        if (Utility.checkNetworkInfo(getActivity()) != null && Utility.checkNetworkInfo(getActivity()).isConnected()) {
            if (mLayoutManager.getItemCount() == 0 && !sortBy.equals(getString(R.string.pref_sort_by_favorite_value))) {
//                Intent intent = new Intent(getActivity(), PopMovieService.class);
//                intent.putExtra(PopMovieService.SORTBY_QUERY_EXTRA, sortBy);
//                intent.putExtra(PopMovieService.PAGE_QUERY_EXTRA, Integer.toString(mPage));
//                getActivity().startService(intent);
                        PopMovieSyncAdapter.initializeSyncAdapter(getContext());
            }
        } else {
            if(!sortBy.equals(getString(R.string.pref_sort_by_favorite_value))) {
                mRecyclerView.setVisibility(View.GONE);
                mEmptyStateTextView.setVisibility(View.VISIBLE);
                mEmptyStateTextView.setText(R.string.no_internet_connection);
            }
            else {
                mRecyclerView.setVisibility(View.VISIBLE);
                mEmptyStateTextView.setVisibility(View.GONE);
            }
        }


        // If there's instance state, mine it for useful information.
        // The end-goal here is that the user never knows that turning their device sideways
        // does crazy lifecycle related things.  It should feel like some stuff stretched out,
        // or magically appeared to take advantage of room, but data or place in the app was never
        // actually *lost*.
//        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
//            // The recycle view probably hasn't even been populated yet.  Actually perform the
//            // swapout in onLoadFinished.
//            mPosition = savedInstanceState.getInt(SELECTED_KEY);
//        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, new CursorLoaderCallbacks());
        super.onActivityCreated(savedInstanceState);
    }

    void onSortByChanged( ) {
        mPage = 1;
        updateMovie();
        getLoaderManager().restartLoader(MOVIE_LOADER, null, new CursorLoaderCallbacks());
    }

    private void updateMovie() {
        String sortBy = Utility.getSortBy(getActivity());
//        if (!sortBy.equals(getString(R.string.pref_sort_by_favorite_value))) {
//            FetchMovieTask movieTask = new FetchMovieTask(getActivity());
////            movieTask.execute(sortBy);
//            movieTask.execute(sortBy, Integer.toString(mPage));
//        }
        if (Utility.checkNetworkInfo(getActivity()) != null && Utility.checkNetworkInfo(getActivity()).isConnected()) {
            if (!sortBy.equals(getString(R.string.pref_sort_by_favorite_value))) {
//                mRecyclerView.setVisibility(View.VISIBLE);
//                mEmptyStateTextView.setVisibility(View.GONE);
//                Intent intent = new Intent(getActivity(), PopMovieService.class);
//                intent.putExtra(PopMovieService.SORTBY_QUERY_EXTRA, sortBy);
//                intent.putExtra(PopMovieService.PAGE_QUERY_EXTRA, Integer.toString(mPage));
//                getActivity().startService(intent);
                // TODO: onPerformSync is not called for the one pane layout...
                PopMovieSyncAdapter.syncImmediately(getActivity());
            }
        } else {
            if(!sortBy.equals(getString(R.string.pref_sort_by_favorite_value))) {
                mRecyclerView.setVisibility(View.GONE);
                mEmptyStateTextView.setVisibility(View.VISIBLE);
                mEmptyStateTextView.setText(R.string.no_internet_connection);
            }
            else {
                mRecyclerView.setVisibility(View.VISIBLE);
                mEmptyStateTextView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != -1) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    public class CursorLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {
        @Override
        public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
            Uri movieUri = MovieEntry.CONTENT_URI;
            String[] projection = MOVIE_COLUMNS;
            String sortBy = Utility.getSortBy(getActivity());
            if(sortBy.equals(getString(R.string.pref_sort_by_favorite_value))) {
                movieUri = FavoritesEntry.CONTENT_URI;
                projection = FAVORITE_COLUMNS;
            }
            return new CursorLoader(getActivity(), movieUri, projection, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
            mMovieCursorAdapter.swapCursor(cursor);

            if (mPosition != -1) {
                // If we don't need to restart the loader, and there's a desired position to restore
                // to, do so now.
                mLayoutManager.scrollToPosition(mPosition);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> cursorLoader) {
            mMovieCursorAdapter.swapCursor(null);
        }
    }

}
