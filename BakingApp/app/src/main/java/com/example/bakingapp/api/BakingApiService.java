package com.example.bakingapp.api;

import com.example.bakingapp.util.LiveDataCallAdapterFactory;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class BakingApiService {

    private static BakingApi mBakingApi;

    public static BakingApi getInstance() {
        if (mBakingApi == null) {
            synchronized (BakingApi.class) {
                if (mBakingApi == null) {
                    mBakingApi = getBakingApi();
                }
            }
        }
        return mBakingApi;
    }

    private static BakingApi getBakingApi() {
        final Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<Object> adapter = moshi.adapter(Object.class);
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.addConverterFactory(MoshiConverterFactory.create(moshi));
        builder.addCallAdapterFactory(new LiveDataCallAdapterFactory());
        builder.baseUrl(BakingApi.BASE_URL);
        final Retrofit retrofit = builder
                .build();

        return retrofit.create(BakingApi.class);
    }
}
