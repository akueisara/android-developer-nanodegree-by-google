package com.example.bakingapp.ui.recipe;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bakingapp.api.ApiResponse;
import com.example.bakingapp.api.BakingApiService;
import com.example.bakingapp.model.Recipe;

import java.util.List;

public class RecipeViewModel extends ViewModel {

    LiveData<ApiResponse<List<Recipe>>> recipes = BakingApiService.getInstance().getRecipes();
}
