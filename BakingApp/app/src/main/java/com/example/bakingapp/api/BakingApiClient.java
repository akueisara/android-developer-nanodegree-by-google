package com.example.bakingapp.api;

import com.example.bakingapp.util.LiveDataCallAdapterFactory;
import com.squareup.moshi.Moshi;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class BakingApiClient {

    private static BakingApiService mBakingApiService;

    public static BakingApiService getInstance() {
        if (mBakingApiService == null) {
            synchronized (BakingApiService.class) {
                if (mBakingApiService == null) {
                    mBakingApiService = getBakingApiService();
                }
            }
        }
        return mBakingApiService;
    }

    private static BakingApiService getBakingApiService() {
        final Moshi moshi = new Moshi.Builder().build();
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.addConverterFactory(MoshiConverterFactory.create(moshi));
        builder.addCallAdapterFactory(new LiveDataCallAdapterFactory());
        builder.baseUrl(BakingApiService.BASE_URL);
        final Retrofit retrofit = builder
                .build();

        return retrofit.create(BakingApiService.class);
    }
}
