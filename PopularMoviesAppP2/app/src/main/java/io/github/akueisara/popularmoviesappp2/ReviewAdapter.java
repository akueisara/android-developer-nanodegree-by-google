package io.github.akueisara.popularmoviesappp2;

import io.github.akueisara.popularmoviesappp2.models.Review;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private Context mContext;
    private List<Review> mReviewList;
    private ReviewAdapter.OnItemClickListener mListener;

    public ReviewAdapter(Context context, List<Review> reviewList, ReviewAdapter.OnItemClickListener listener) {
        mContext = context;
        mReviewList = reviewList;
        mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Review review);
    }

    @Override
    public ReviewAdapter.ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View movieView = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
        ReviewAdapter.ReviewViewHolder reviewViewHolder = new ReviewAdapter.ReviewViewHolder(movieView);
        return reviewViewHolder;
    }

    @Override
    public void onBindViewHolder(ReviewAdapter.ReviewViewHolder holder, int position) {
        Review review = mReviewList.get(position);
        holder.mReviewTitle.setText(mContext.getString(R.string.review_title) + " " + Integer.toString(position+1));
        holder.mAuthor.setText(mContext.getString(R.string.author));
        holder.mAuthorName.setText(review.getAuthor());
        holder.mReview.setText(review.getContent());
        holder.bind(review, mListener);
    }

    @Override
    public int getItemCount() {
        return mReviewList.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.review_title)
        TextView mReviewTitle;
        @BindView(R.id.author)
        TextView mAuthor;
        @BindView(R.id.author_name)
        TextView mAuthorName;
        @BindView(R.id.review)
        TextView mReview;

        public ReviewViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        public void bind(final Review review, final ReviewAdapter.OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(review);
                }
            });
        }
    }

    public void clear(){
        mReviewList.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Review> reviewList){
        mReviewList.addAll(reviewList);
        notifyDataSetChanged();
    }
}
