package com.example.bakingapp.ui.recipe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.bakingapp.databinding.FragmentRecipeBinding;

public class RecipeFragment extends Fragment {

    private RecipeViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(RecipeViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentRecipeBinding binding = FragmentRecipeBinding.inflate(inflater);

        viewModel.recipes.observe(getViewLifecycleOwner(), apiResponse -> {
            // show the recipe list
        });

        return binding.getRoot();
    }

}
