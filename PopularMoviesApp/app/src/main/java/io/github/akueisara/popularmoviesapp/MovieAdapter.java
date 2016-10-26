package io.github.akueisara.popularmoviesapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by akueisara on 10/24/2016.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private List<Movie> mMovieList;
    private OnItemClickListener mListener;

    public MovieAdapter(List<Movie> movieList, OnItemClickListener listener) {
        mMovieList = movieList;
        mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Movie movie);
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View movieView = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
        MovieViewHolder movieViewHolder = new MovieViewHolder(movieView);
        return movieViewHolder;
    }

    @Override
    public void onBindViewHolder(MovieAdapter.MovieViewHolder holder, int position) {
        Movie movie = mMovieList.get(position);
        Picasso.with(holder.mImageView.getContext())
                .load(movie.getPosterImageURL())
                .into(holder.mImageView);
        holder.bind(mMovieList.get(position), mListener);
    }

    @Override
    public int getItemCount() {
        return mMovieList.size();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imageview)
        ImageView mImageView;

        public MovieViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        public void bind(final Movie movie, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(movie);
                }
            });
        }
    }

    public void clear(){
        mMovieList.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Movie> movieList){
        mMovieList.addAll(movieList);
        notifyDataSetChanged();
    }
}
