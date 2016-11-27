package io.github.akueisara.popularmoviesappp2.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import io.github.akueisara.popularmoviesappp2.MovieFragment;

public class PopMovieSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static PopMovieSyncAdapter sPopMovieSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("PopMovieSyncService", "onCreate - PopMovieSyncService");
        synchronized (sSyncAdapterLock) {
            if (sPopMovieSyncAdapter == null) {
                sPopMovieSyncAdapter = new PopMovieSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sPopMovieSyncAdapter.getSyncAdapterBinder();
    }
}
