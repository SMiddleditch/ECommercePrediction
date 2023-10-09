package com.example.ecommerceprediction.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.ecommerceprediction.R;
import com.example.ecommerceprediction.adapter.ProductsAdapter;
import com.example.ecommerceprediction.holders.Products;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AllProductActivity extends AppCompatActivity {
    // Declare UI components and data structures
    private FirebaseFirestore fStore;
    private RecyclerView allProductsRecyclerView;
    private List<Products> allProductsList;
    private ProductsAdapter allProductsAdapter;
    private TextView heading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand_products);

        // Get section or brand name from the intent
        String section = getIntent().getStringExtra("section");
        String brand = getIntent().getStringExtra("brand");

        // Associate TextView with its counterpart in the layout
        heading = findViewById(R.id.heading);

        // Initialize Firestore instance
        fStore = FirebaseFirestore.getInstance();

        // Initialize RecyclerView, its adapter and data structures
        allProductsRecyclerView = findViewById(R.id.allProductsRecycler);
        allProductsList = new ArrayList<>();
        allProductsAdapter = new ProductsAdapter(this, allProductsList);

        // Set up RecyclerView with a GridLayoutManager and the adapter
        allProductsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // '2' represents the number of columns
        allProductsRecyclerView.setAdapter(allProductsAdapter);

        // Fetch products based on section or brand
        if (brand != null && !brand.isEmpty()) {
            heading.setText(brand);
            fetchProducts("Brand", brand, null, null, -1);
        } else if (section.equals("browse")) {
            heading.setText("All Products");
            fetchProducts(null, null, null, null, -1);
        } else if (section.equals("popular")) {
            heading.setText("All Popular Products");
            fetchProducts(null, null, "averageRating", 3.3, 40);
        }
    }
    // This method fetches products from Firestore depending on certain parameters
    private void fetchProducts(String field, String value, String orderBy, Double rating, int limit) {
        // Construct the appropriate query based on the passed parameters
        Query query;
        if (field != null) {
            query = fStore.collection("Products").whereEqualTo(field, value);
        } else if (orderBy != null) {
            query = fStore.collection("Products").orderBy(orderBy, Query.Direction.DESCENDING).whereGreaterThanOrEqualTo("averageRating", rating).limit(limit);
        } else {
            query = fStore.collection("Products");
        }
        // Run the query and handle the resul
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Products product = document.toObject(Products.class);
                    allProductsList.add(product);
                    allProductsAdapter.notifyDataSetChanged();
                }
            } else {
                Log.w("AllProductActivity", "Error getting documents.", task.getException());
            }
        });
    }
}
