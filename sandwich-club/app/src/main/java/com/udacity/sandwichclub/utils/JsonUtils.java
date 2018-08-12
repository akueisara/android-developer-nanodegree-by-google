package com.udacity.sandwichclub.utils;

import com.udacity.sandwichclub.model.Sandwich;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    public static Sandwich parseSandwichJson(String json) {
        Sandwich sandwich = null;
        try {
            JSONObject sandwichJsonObject = new JSONObject(json);
            JSONObject name = sandwichJsonObject.getJSONObject("name");
            String mainName = name.getString("mainName");
            List<String> alsoKnownAs = JSONArrayToList(name.getJSONArray("alsoKnownAs"));

            String placeOfOrigin = sandwichJsonObject.getString("placeOfOrigin");
            String description = sandwichJsonObject.getString("description");
            String imageURL = sandwichJsonObject.getString("image");

            List<String> ingredients = JSONArrayToList(sandwichJsonObject.getJSONArray("ingredients"));

            sandwich = new Sandwich(mainName, alsoKnownAs, placeOfOrigin, description, imageURL, ingredients);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return sandwich;
    }

    private static List<String> JSONArrayToList(JSONArray jsonArray) {
        ArrayList<String> list = new ArrayList<String>();
        if (jsonArray != null) {
            int len = jsonArray.length();
            for (int i = 0; i < len; i++) {
                try {
                    list.add(jsonArray.get(i).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }
}
