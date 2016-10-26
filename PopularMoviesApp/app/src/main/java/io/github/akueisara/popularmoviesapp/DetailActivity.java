package io.github.akueisara.popularmoviesapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by akueisara on 10/24/2016.
 */

public class DetailActivity extends AppCompatActivity {
    @BindView(R.id.movie_title)
    TextView mMovieTitleTextView;
    @BindView(R.id.movie_date)
    TextView mMovieDateTextView;
    @BindView(R.id.movie_image)
    ImageView mMovieImageView;
    @BindView(R.id.movie_vote_average)
    TextView mMovieVoteAverageTextView;
    @BindView(R.id.movie_overview)
    TextView mMoviePlotSynopsisTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        Movie movie = (Movie) getIntent().getParcelableExtra("myData");

        mMovieTitleTextView.setText(movie.getTitle());
        mMovieDateTextView.setText(movie.getReleaseDate().substring(0,4));
        Picasso.with(mMovieImageView.getContext())
                .load(movie.getPosterImageURL())
                .into(mMovieImageView);
        mMovieVoteAverageTextView.setText(movie.getVoteAverage()+ "/10");
        mMoviePlotSynopsisTextView.setText(movie.getPlotSynopsis());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
