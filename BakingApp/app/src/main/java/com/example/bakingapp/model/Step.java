package com.example.bakingapp.model;

import kotlinx.android.parcel.Parcelize;

@Parcelize
public class Step {
    public Integer id;
    public String shortDescription;
    public String description;
    public String videoURL;
    public String thumbnailURL;
}