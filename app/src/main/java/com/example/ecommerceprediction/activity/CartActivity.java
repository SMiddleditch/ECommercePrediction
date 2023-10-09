package com.example.ecommerceprediction.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.ecommerceprediction.R;
import com.example.ecommerceprediction.adapter.CartAdapter;
import com.example.ecommerceprediction.holders.Products;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    // Declare views and variables
    private ListView cartListView; // ListView to display items in the cart
    private Button clearCartButton; // Button to clear the cart
    private List<Products> displayItems; // List to store items in the cart
    private CartAdapter itemsAdapter; // Adapter for the ListView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        Log.i("CartActivity", "Entered onCreate method");

        // Initialize views
        cartListView = findViewById(R.id.cartListView);
        clearCartButton = findViewById(R.id.clearCartButton);

        // Initialize list and adapter
        displayItems = new ArrayList<>();
        itemsAdapter = new CartAdapter(this, displayItems);
        cartListView.setAdapter(itemsAdapter);

        // Load items in the cart
        loadCartItems();

        // Set click listener for the ListView items
        cartListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // This code will run when an item in the ListView is clicked. Implement as per requirement.
            }
        });

        // Set click listener for the clear cart button
        clearCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear the cart when the button is clicked
                clearCart();
                Toast.makeText(CartActivity.this, "Cart cleared", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to load items in the cart from Firebase Firestore
    private void loadCartItems() {
        // Get the ID of the current user
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("CartActivity", "User ID: " + userId);

        // Fetch the cart items from Firestore
        FirebaseFirestore.getInstance().collection("User").document(userId).collection("Cart")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // If the task is successful, add each fetched item to the list and notify the adapter
                        displayItems.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            Products product = document.toObject(Products.class);
                            displayItems.add(product);
                        }
                        itemsAdapter.notifyDataSetChanged();
                        Log.d("CartActivity", "Number of items fetched: " + displayItems.size());
                        Log.d("CartActivity", "Items fetched successfully");
                    } else {
                        // If the task is unsuccessful, log the exception
                        Log.d("CartActivity", "Error getting cart items: ", task.getException());
                    }
                });
    }

    // Method to clear the cart
    private void clearCart() {
        displayItems.clear(); // Clear the list
        itemsAdapter.notifyDataSetChanged(); // Notify the adapter
        // You may want to also remove the items from the Firebase collection here
    }
}
