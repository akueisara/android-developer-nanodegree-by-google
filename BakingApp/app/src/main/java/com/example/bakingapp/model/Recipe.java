package com.example.bakingapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Recipe implements Parcelable {
    public Integer id;
    public String name;
    public List<Ingredient> ingredients;
    public List<Step> steps;
    public Integer servings;
    public String image;

    protected Recipe(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        name = in.readString();
        steps = in.createTypedArrayList(Step.CREATOR);
        if (in.readByte() == 0) {
            servings = null;
        } else {
            servings = in.readInt();
        }
        image = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.id);
        parcel.writeString(this.name);
        parcel.writeList(this.ingredients);
        parcel.writeList(this.steps);
        parcel.writeInt(this.servings);
        parcel.writeString(this.image);
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };
}
