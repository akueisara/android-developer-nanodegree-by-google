package com.example.bakingapp.api;

import androidx.lifecycle.LiveData;
import com.example.bakingapp.model.Recipe;
import java.util.List;
import retrofit2.http.GET;

public interface BakingApiService {

    String BASE_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/";

    @GET("baking.json")
    LiveData<ApiResponse<List<Recipe>>> getRecipes();
}

