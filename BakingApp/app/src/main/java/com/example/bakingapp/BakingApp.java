package com.example.bakingapp;

import android.app.Application;

import androidx.viewbinding.BuildConfig;

import timber.log.Timber;

public class BakingApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}