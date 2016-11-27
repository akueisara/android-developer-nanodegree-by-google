package io.github.akueisara.popularmoviesappp2;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.akueisara.popularmoviesappp2.data.MovieContract.MovieEntry;

public class MovieCursorAdapter extends CursorRecyclerViewAdapter<MovieCursorAdapter.MovieViewHolder>{
    private MovieCursorAdapter.OnItemClickListener mListener;

    public MovieCursorAdapter(Context context, Cursor cursor, MovieCursorAdapter.OnItemClickListener listener){
        super(context,cursor);
        mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Cursor cursor, int pos);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder viewHolder, Cursor cursor) {
        int idx_poster_url = cursor.getColumnIndex(MovieEntry.COLUMN_POSTER_IMG_URL);
        Picasso.with(viewHolder.mImageView.getContext())
                .load(cursor.getString(idx_poster_url))
                .into(viewHolder.mImageView);
        viewHolder.bind(cursor, cursor.getPosition(), mListener);
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View movieView = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        MovieViewHolder movieViewHolder = new MovieViewHolder(movieView);
        return movieViewHolder;
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imageview)
        ImageView mImageView;

        public MovieViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        public void bind(final Cursor cursor, final int pos, final MovieCursorAdapter.OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(cursor, pos);
                }
            });
        }
    }
}
