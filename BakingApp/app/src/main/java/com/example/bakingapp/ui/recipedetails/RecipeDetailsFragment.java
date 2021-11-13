package com.example.bakingapp.ui.recipedetails;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bakingapp.R;
import com.example.bakingapp.databinding.FragmentRecipeDetailsBinding;
import com.example.bakingapp.model.Recipe;

public class RecipeDetailsFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentRecipeDetailsBinding binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_recipe_details,
                container,
                false
        );
        if (getArguments() != null) {
            Recipe recipe = RecipeDetailsFragmentArgs.fromBundle(getArguments()).getRecipe();
            requireActivity().setTitle(recipe.name);
            binding.setRecipe(recipe);
        }
        return binding.getRoot();
    }

}
