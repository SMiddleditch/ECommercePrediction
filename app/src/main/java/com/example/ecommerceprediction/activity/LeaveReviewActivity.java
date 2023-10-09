package com.example.ecommerceprediction.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ecommerceprediction.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeaveReviewActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    // UI components
    private Spinner ratingSpinner;
    private EditText reviewEditText;
    private Button submitButton;

    // Review related variables
    private String productId;
    private int reviewRating;
    ArrayList<Integer> spinnerNumbers = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_review);

        // Initialization of UI components
        ratingSpinner = findViewById(R.id.ratingSpinner);
        ratingSpinner.setOnItemSelectedListener(this);
        reviewEditText = findViewById(R.id.reviewInput);
        submitButton = findViewById(R.id.submitButton);

        // ArrayAdapter for rating spinner
        ArrayAdapter<Integer> ad = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerNumbers);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ratingSpinner.setAdapter(ad);

        // Retrieve productId from intent
        productId = getIntent().getStringExtra("product_id");
        if (productId == null) {
            Toast.makeText(this, "Product ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Setting up click listener for submit review button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitReview();
            }
        });
    }

    // Selecting review rating from spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        reviewRating = spinnerNumbers.get(position);
        Toast.makeText(getApplicationContext(), Integer.toString(reviewRating), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    // Method to submit review
    private void submitReview() {
        String reviewText = reviewEditText.getText().toString();
        if (reviewText.isEmpty()) {
            Toast.makeText(this, "Please enter a review.", Toast.LENGTH_SHORT).show();
            return;
        }

        Date reviewDate = new Date();
        Map<String, Object> review = new HashMap<>();
        review.put("reviewText", reviewText);
        review.put("reviewRating", reviewRating);
        review.put("reviewDate", reviewDate);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference productRef = db.collection("Products").document(productId);

        // Adding review to Firestore
        productRef.collection("Reviews")
                .add(review)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LeaveReviewActivity.this, "Review submitted successfully.", Toast.LENGTH_SHORT).show();
                            incrementRatingCountAndUpdateAverageRating();
                        } else {
                            Toast.makeText(LeaveReviewActivity.this, "Failed to submit review.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Method to increment rating count and update average rating
    private void incrementRatingCountAndUpdateAverageRating() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference productRef = db.collection("Products").document(productId);

        // Updating rating count in Firestore
        productRef.update("ratingCount", FieldValue.increment(1))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            updateAverageRating();
                        } else {
                            Toast.makeText(LeaveReviewActivity.this, "Failed to update rating count.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Method to update average rating based on all reviews
    private void updateAverageRating() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference productRef = db.collection("Products").document(productId);

        // Fetching reviews and calculating average rating
        productRef.collection("Reviews")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int totalRating = 0;
                            List<DocumentSnapshot> reviews = task.getResult().getDocuments();
                            for (DocumentSnapshot review : reviews) {
                                Integer rating = review.getLong("reviewRating").intValue();
                                totalRating += rating;
                            }

                            // Calculate average rating and round it
                            int averageRating = Math.round((float) totalRating / reviews.size());

                            // Update average rating of the product
                            productRef.update("averageRating", averageRating)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (!task.isSuccessful()) {
                                                Toast.makeText(LeaveReviewActivity.this, "Failed to update average rating.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(LeaveReviewActivity.this, "Failed to get reviews.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
