package com.example.bakingapp.model;

import java.util.List;

public class Recipe {
    Integer id;
    String name;
    List<Ingredient> ingredients;
    List<Step> steps;
    Integer servings;
}
