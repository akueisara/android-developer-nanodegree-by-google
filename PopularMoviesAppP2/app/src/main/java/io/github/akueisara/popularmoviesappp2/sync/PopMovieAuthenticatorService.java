package io.github.akueisara.popularmoviesappp2.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 *  The service which allows the sync adapter framework to access the authenticator.
 */
public class PopMovieAuthenticatorService extends Service {
    // Instance field that stores the authenticator object
    private PopMovieAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new PopMovieAuthenticator(this);
    }

    /*
        * When the system binds to this Service to make the RPC call
        * return the authenticator's IBinder.
        */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
