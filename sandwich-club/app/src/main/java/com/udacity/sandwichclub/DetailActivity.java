package com.udacity.sandwichclub;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.udacity.sandwichclub.model.Sandwich;
import com.udacity.sandwichclub.utils.JsonUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_POSITION = "extra_position";
    private static final int DEFAULT_POSITION = -1;

    @BindView(R.id.image_pb) ProgressBar mImageProgressBar;
    @BindView(R.id.also_known_layout) LinearLayout mAlsoKnownLayout;
    @BindView(R.id.also_known_tv) TextView mAlsoKnownTextView;
    @BindView(R.id.ingredients_layout) LinearLayout mIngredientsLayout;
    @BindView(R.id.ingredients_tv) TextView mIngredientsTextView;
    @BindView(R.id.origin_tv) TextView mOriginTextView;
    @BindView(R.id.description_tv) TextView mDescriptionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        ImageView ingredientsIv = findViewById(R.id.image_iv);

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        int position = intent.getIntExtra(EXTRA_POSITION, DEFAULT_POSITION);
        if (position == DEFAULT_POSITION) {
            // EXTRA_POSITION not found in intent
            closeOnError();
            return;
        }

        String[] sandwiches = getResources().getStringArray(R.array.sandwich_details);
        String json = sandwiches[position];
        Sandwich sandwich = JsonUtils.parseSandwichJson(json);
        if (sandwich == null) {
            // Sandwich data unavailable
            closeOnError();
            return;
        }

        populateUI(sandwich);
        Picasso.get()
                .load(sandwich.getImage())
                .error(R.drawable.ic_error_loading_image)
                .into(ingredientsIv, new Callback() {
                    @Override
                    public void onSuccess() {
                        mImageProgressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        mImageProgressBar.setVisibility(View.GONE);
                    }
                });

        setTitle(sandwich.getMainName());
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }

    private void populateUI(Sandwich sandwich) {
        setupTextViewForList(sandwich.getAlsoKnownAs().size(), sandwich.getAlsoKnownAs(), mAlsoKnownLayout, mAlsoKnownTextView);
        setupTextViewForList(sandwich.getIngredients().size(), sandwich.getIngredients(), mIngredientsLayout, mIngredientsTextView);
        if(sandwich.getPlaceOfOrigin().isEmpty()) {
            mOriginTextView.setText(R.string.sandwich_none);
        } else {
            mOriginTextView.setText(sandwich.getPlaceOfOrigin());
        }
        mDescriptionTextView.setText(sandwich.getDescription());
    }

    private void setupTextViewForList(int size, List<String> list, LinearLayout layout, TextView textView) {
        if (size > 0) {
            String s = "";
            for(int i = 0; i < size; i++) {
                if(i != size - 1) {
                    s = s.concat(list.get(i) + ", ");
                } else {
                    s = s.concat(list.get(i));
                }
            }
            textView.setText(s);
        } else {
            layout.setVisibility(View.GONE);
        }
    }
}
