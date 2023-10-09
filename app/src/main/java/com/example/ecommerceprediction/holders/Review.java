package com.example.ecommerceprediction.holders;

import com.google.firebase.Timestamp;

import java.io.Serializable;

public class Review implements Serializable {
    private String reviewText;
    private Timestamp reviewDate;
    private int reviewRating;

    // Empty constructor needed for Firestore deserialization
    public Review() {}

    // Getters and setters

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public Timestamp getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(Timestamp reviewDate) {
        this.reviewDate = reviewDate;
    }

    public int getReviewRating() {
        return reviewRating;
    }

    public void setReviewRating(int reviewRating) {
        this.reviewRating = reviewRating;
    }
}
