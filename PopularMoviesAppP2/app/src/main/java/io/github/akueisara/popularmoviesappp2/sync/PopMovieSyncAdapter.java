package io.github.akueisara.popularmoviesappp2.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Vector;

import io.github.akueisara.popularmoviesappp2.MovieFragment;
import io.github.akueisara.popularmoviesappp2.R;
import io.github.akueisara.popularmoviesappp2.Utility;
import io.github.akueisara.popularmoviesappp2.data.MovieContract;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class PopMovieSyncAdapter extends AbstractThreadedSyncAdapter {
    public static final String LOG_TAG = PopMovieSyncAdapter.class.getSimpleName();

    private static final String MOVIE_DB_API_KEY = "b7c08ce2e5830c458a57fddfb7976b13";

    private int mPage;

    // Interval at which to sync with the movie list, in milliseconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60*180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    public PopMovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        String sortBy = Utility.getSortBy(getContext());
        mPage = MovieFragment.mPage;

        // Will contain the raw JSON response as a string.
        String movieJsonStr = null;

        String language = "en-US";

        try {
            final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/" + sortBy;
            final String LANGUAGE_PARAM = "language";
            final String PAGE_PARAM = "page";
            final String APIKEY_PARAM = "api_key";

            Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendQueryParameter(LANGUAGE_PARAM, language)
                    .appendQueryParameter(PAGE_PARAM, Integer.toString(mPage))
                    .appendQueryParameter(APIKEY_PARAM, MOVIE_DB_API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());

            OkHttpClient client = new OkHttpClient();
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(url)
                    .build();
            Response responses = null;

            try {
                responses = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            movieJsonStr = responses.body().string();
            getMovieDataFromJson(movieJsonStr);
        } catch (IOException e) {
            Log.e("MovieFragment", "Error ", e);
            // If the code didn't successfully get the movie data, there's no point in attemping
            // to parse it.
            return;
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {

        }
    }

    public void getMovieDataFromJson(String movieJsonStr) throws JSONException {
        String IMAGE_BASE_URL =  "http://image.tmdb.org/t/p/";
        String IMAGE_SIZE =  "w185";

        try {
            JSONObject reader = new JSONObject(movieJsonStr);
            JSONArray resultArray = reader.getJSONArray("results");

            // Insert the new movie information into the database
            Vector<ContentValues> cVVector = new Vector<ContentValues>(resultArray.length());

            for(int i=0; i< resultArray.length(); i++) {
                JSONObject movie = resultArray.getJSONObject(i);
                int id = movie.getInt("id");
                String title = movie.getString("title");
                String date = movie.getString("release_date");
                String posterPath = IMAGE_BASE_URL + IMAGE_SIZE + movie.getString("poster_path");
                Double vote = movie.getDouble("vote_average");
                String overview =  movie.getString("overview");

                ContentValues movieValues = new ContentValues();

                movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, id);
                movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
                movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_IMG_URL, posterPath);
                movieValues.put(MovieContract.MovieEntry.COLUMN_DATE, date);
                movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, vote);
                movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overview);

                cVVector.add(movieValues);
            }

            // add to database
            if ( cVVector.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                int deleted = 0;
                if(mPage == 1) {
                    deleted = getContext().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, null, null);
                }
                getContext().getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the movie JSON results", e);
        }
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Log.d(LOG_TAG, "syncImmediately");
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        PopMovieSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
