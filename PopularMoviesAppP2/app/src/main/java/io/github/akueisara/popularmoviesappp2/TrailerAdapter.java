package io.github.akueisara.popularmoviesappp2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.akueisara.popularmoviesappp2.models.Trailer;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {
    private Context mContext;
    private List<Trailer> mTrailerList;
    private TrailerAdapter.OnItemClickListener mListener;

    public TrailerAdapter(Context context, List<Trailer> trailerList, TrailerAdapter.OnItemClickListener listener) {
        mContext = context;
        mTrailerList = trailerList;
        mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Trailer trailer);
    }

    @Override
    public TrailerAdapter.TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View movieView = LayoutInflater.from(context).inflate(R.layout.item_trailer, parent, false);
        TrailerAdapter.TrailerViewHolder movieViewHolder = new TrailerAdapter.TrailerViewHolder(movieView);
        return movieViewHolder;
    }

    @Override
    public void onBindViewHolder(TrailerAdapter.TrailerViewHolder holder, int position) {
        Trailer trailer = mTrailerList.get(position);
        holder.mTextView.setText(mContext.getString(R.string.trailer_title) + " " + Integer.toString(position+1));
        holder.mImageView.setVisibility(View.VISIBLE);
        holder.bind(trailer, mListener);
    }

    @Override
    public int getItemCount() {
        return mTrailerList.size();
    }

    public static class TrailerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.trailer_textview)
        TextView mTextView;
        @BindView(R.id.trailer_play_button)
        ImageView mImageView;

        public TrailerViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        public void bind(final Trailer trailer, final TrailerAdapter.OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(trailer);
                }
            });
        }
    }

    public void clear(){
        mTrailerList.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Trailer> trailerList){
        mTrailerList.addAll(trailerList);
        notifyDataSetChanged();
    }

}
