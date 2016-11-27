package io.github.akueisara.popularmoviesappp2.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Vector;

import io.github.akueisara.popularmoviesappp2.data.MovieContract;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class PopMovieService extends IntentService {

    public static final String SORTBY_QUERY_EXTRA = "sqe";
    public static final String PAGE_QUERY_EXTRA = "pqe";

    private final String LOG_TAG = PopMovieService.class.getSimpleName();

    private static final String MOVIE_DB_API_KEY = "b7c08ce2e5830c458a57fddfb7976b13";

    private int mPage;

    public PopMovieService() {
        super("PopMovie");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String sortBy = intent.getStringExtra(SORTBY_QUERY_EXTRA);
        String page = intent.getStringExtra(PAGE_QUERY_EXTRA);

        // Will contain the raw JSON response as a string.
        String movieJsonStr = null;

        String language = "en-US";
        mPage = Integer.parseInt(page);

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
                    deleted = this.getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, null, null);
                    Log.d(LOG_TAG, "PopMovies Service before Complete. " + deleted + " Deleted");
                }
                this.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "PopMovies Service Complete. " + cVVector.size() + " Inserted");

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the movie JSON results", e);
        }
    }

}
