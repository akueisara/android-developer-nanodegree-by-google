package com.udacity.gradle.builditbigger;

import android.content.Context;
import android.support.v4.util.Pair;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.Joker;

import java.util.concurrent.ExecutionException;

public class AsyncTaskTest extends AndroidTestCase {

    private static final String TAG = AsyncTaskTest.class.getSimpleName();

    public void testAsyncTaskRetrievesNonemptyString() {
        Joker myJoker = new Joker();
        String joke = myJoker.getJoke();
        try {
            joke = new EndpointsAsyncTask().execute(new Pair<Context, String>(getContext(), joke)).get();
            Log.d(TAG, "Joke text: " + joke);
            assertNotNull(joke);
            assertTrue(joke.length() > 0);
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

}
