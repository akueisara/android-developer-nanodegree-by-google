package com.example.bakingapp.ui.recipe;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.bakingapp.api.ApiResponse;
import com.example.bakingapp.api.BakingApiClient;
import com.example.bakingapp.model.Recipe;

import java.util.List;

public class RecipeViewModel extends ViewModel {

    public LiveData<ApiResponse<List<Recipe>>> recipes = BakingApiClient.getInstance().getRecipes();
}
