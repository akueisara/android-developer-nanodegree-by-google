package io.github.akueisara.popularmoviesappp2;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.github.akueisara.popularmoviesappp2.models.Review;
import io.github.akueisara.popularmoviesappp2.models.Trailer;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static io.github.akueisara.popularmoviesappp2.data.MovieProvider.LOG_TAG;

// This class will never be extended, so it can be marked as final.
public final class Utility {

    // This class will never be instantiated, so we can suppress the constructor.
    private Utility(){}

    public static ArrayList<Trailer> extractTrailerResultFromJson(String trailerJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(trailerJSON)) {
            return null;
        }

        ArrayList<Trailer> trailers = new ArrayList<>();

        try {
            JSONObject reader = new JSONObject(trailerJSON);
            JSONArray resultArray = reader.getJSONArray("results");
            for(int i=0; i< resultArray.length(); i++) {
                JSONObject trailer = resultArray.getJSONObject(i);
                String id = trailer.getString("id");
                String key = trailer.getString("key");
                String name = trailer.getString("name");
                String site = trailer.getString("site");
                int size = trailer.getInt("size");
                Trailer t = new Trailer(id, key, name, site, size);
                trailers.add(t);
            }
        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the trailer JSON results", e);
        }

        return trailers;
    }

    public static ArrayList<Review> extractReviewResultFromJson(String reviewJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(reviewJSON)) {
            return null;
        }

        ArrayList<Review> reviews = new ArrayList<>();

        try {
            JSONObject reader = new JSONObject(reviewJSON);
            JSONArray resultArray = reader.getJSONArray("results");
            for(int i=0; i< resultArray.length(); i++) {
                JSONObject trailer = resultArray.getJSONObject(i);
                String id = trailer.getString("id");
                String author = trailer.getString("author");
                String content = trailer.getString("content");
                Review r = new Review(id, author, content);
                reviews.add(r);
            }
        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the review JSON results", e);
        }

        return reviews;
    }


    public static List<Trailer> fetchTrailerData(String requestUrl) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Create URL object
        URL url = createUrl(requestUrl);

        String jsonResponse = null;
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response responses = null;

            try {
                responses = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Perform HTTP request to the URL and receive a JSON response back

            jsonResponse = responses.body().string();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create an {@link Event} object
        List<Trailer> trailers = extractTrailerResultFromJson(jsonResponse);

        // Return the {@link Event}
        return trailers;
    }

    public static List<Review> fetchReviewData(String requestUrl) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Create URL object
        URL url = createUrl(requestUrl);

        String jsonResponse = null;
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response responses = null;

            try {
                responses = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Perform HTTP request to the URL and receive a JSON response back

            jsonResponse = responses.body().string();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create an {@link Event} object
        List<Review> reviews = extractReviewResultFromJson(jsonResponse);

        // Return the {@link Event}
        return reviews;
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    public static String getSortBy(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPrefs.getString(
                context.getString(R.string.pref_sort_by_key),
                context.getString(R.string.pref_sort_by_default)
        );
    }

    public static NetworkInfo checkNetworkInfo(Activity activity) {
        ConnectivityManager connMgr = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo;
    }
}
