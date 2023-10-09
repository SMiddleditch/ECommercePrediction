package com.example.ecommerceprediction.activity;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceprediction.R;
import com.example.ecommerceprediction.adapter.ReviewAdapter;
import com.example.ecommerceprediction.holders.Review;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ReviewsActivity extends AppCompatActivity {
    private String ProductId;  // Product ID to be reviewed
    private List<Review> reviewsList;  // List to hold reviews
    private ReviewAdapter reviewAdapter;  // Adapter to handle review display
    private Button leaveReview;  // Button to initiate review creation

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        // Retrieve the product ID from the intent
        ProductId = getIntent().getStringExtra("product_id");

        // Handle the situation when productId is null
        if (ProductId == null) {
            Log.e("ReviewsActivity", "Product ID not found sorry");
            finish();
            return;
        }

        // Initialize the reviews list and the review adapter
        reviewsList = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(this, reviewsList);

        // Set up the RecyclerView to display reviews
        RecyclerView recyclerView = findViewById(R.id.reviews_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(reviewAdapter);

        // Fetch the product reviews
        fetchReviews(ProductId);

        // Handle the click on the "leave review" button
        leaveReview = findViewById(R.id.leave_review);
        leaveReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an intent to navigate to the LeaveReviewActivity
                Intent intent = new Intent(ReviewsActivity.this, LeaveReviewActivity.class);
                // Pass the product ID to the next activity
                intent.putExtra("product_id", ProductId);
                startActivity(intent);
            }
        });
    }

    // Method to fetch product reviews from Firestore
    private void fetchReviews(String productId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference productRef = db.collection("Products").document(productId);

        productRef.collection("Reviews")
                .orderBy("reviewDate", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Review review = document.toObject(Review.class);
                                reviewsList.add(review);
                            }
                            // Notify the adapter that the data has changed
                            reviewAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
