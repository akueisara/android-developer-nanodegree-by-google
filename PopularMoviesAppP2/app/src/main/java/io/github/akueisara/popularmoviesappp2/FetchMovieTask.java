package io.github.akueisara.popularmoviesappp2;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Vector;

import okhttp3.OkHttpClient;
import okhttp3.Response;

import io.github.akueisara.popularmoviesappp2.data.MovieContract.MovieEntry;

public class FetchMovieTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

    private static final String MOVIE_DB_API_KEY = "b7c08ce2e5830c458a57fddfb7976b13";

    private MovieAdapter mMovieAdapter;
    private final Context mContext;

    private int mPage;

    public FetchMovieTask(Context context) {
        mContext = context;
    }

    private boolean DEBUG = true;

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

                movieValues.put(MovieEntry.COLUMN_MOVIE_ID, id);
                movieValues.put(MovieEntry.COLUMN_TITLE, title);
                movieValues.put(MovieEntry.COLUMN_POSTER_IMG_URL, posterPath);
                movieValues.put(MovieEntry.COLUMN_DATE, date);
                movieValues.put(MovieEntry.COLUMN_VOTE_AVERAGE, vote);
                movieValues.put(MovieEntry.COLUMN_OVERVIEW, overview);

                cVVector.add(movieValues);
            }

            int inserted = 0;
            // add to database
            if ( cVVector.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                int deleted = 0;
                if(mPage == 1) {
                    deleted = mContext.getContentResolver().delete(MovieEntry.CONTENT_URI, null, null);
                    Log.d(LOG_TAG, "FetchMovieTask before Complete. " + deleted + " Deleted");
                }
                inserted = mContext.getContentResolver().bulkInsert(MovieEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "FetchMovieTask Complete. " + inserted  + " Inserted");

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the movie JSON results", e);
        }
    }

    @Override
    protected Void doInBackground(String... params) {
        // If there's no sort_by parameter there's nothing to look up.  Verify size of params.
        if (params.length == 0) {
            return null;
        }

        // Will contain the raw JSON response as a string.
        String movieJsonStr = null;

        String language = "en-US";
        mPage = Integer.parseInt(params[1]);

        try {
            final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/" + params[0];
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
            return null;
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {

        }

        // This will only happen if there was an error getting or parsing the movie list.
        return null;
    }
}