package com.example.ecommerceprediction.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceprediction.R;
import com.example.ecommerceprediction.holders.Review;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    // Declare context and list of reviews
    private final Context context;
    private final List<Review> reviewsList;

    // Constructor to initialize context and reviews list
    public ReviewAdapter(Context context, List<Review> reviewsList) {
        this.context = context;
        this.reviewsList = reviewsList;
    }

    // Method to inflate individual review view
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.individual_review_item, parent, false);
        return new ViewHolder(view);
    }

    // Method to bind data to each review view
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the current review
        Review currentReview = reviewsList.get(position);

        // Format the review date
        Date date = currentReview.getReviewDate().toDate();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = formatter.format(date);

        // Set the review text, date and rating
        holder.reviewText.setText("Comment: " + currentReview.getReviewText());
        holder.reviewDate.setText("Date: " + formattedDate);
        holder.reviewRating.setText(currentReview.getReviewRating() + " out of 5");
    }

    // Method to get count of review items
    @Override
    public int getItemCount() {
        return reviewsList.size();
    }

    // ViewHolder class to hold view components of individual review item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView reviewText;
        TextView reviewDate;
        TextView reviewRating;

        // ViewHolder constructor to initialize the view components
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            reviewText = itemView.findViewById(R.id.review_text);
            reviewDate = itemView.findViewById(R.id.review_date);
            reviewRating = itemView.findViewById(R.id.review_rating);
        }
    }
}

