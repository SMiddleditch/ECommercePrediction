package com.example.ecommerceprediction.activity;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.ecommerceprediction.R;
import com.example.ecommerceprediction.activity.address.CrudAddressActivity;
import com.example.ecommerceprediction.activity.login.MainActivity;
import com.example.ecommerceprediction.activity.settings.SettingsActivity;
import com.example.ecommerceprediction.fragments.HomeFragment;
import com.example.ecommerceprediction.holders.Products;
import com.example.ecommerceprediction.adapter.ProductsAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

// This class represents the Home Activity of the application
public class HomeActivity extends AppCompatActivity {
    // Declare variables and views
    private HomeFragment homeFragment; // fragment displayed in this activity
    private Toolbar aToolbar; // toolbar of this activity
    private FirebaseAuth aAuth; // Firebase authentication instance
    private EditText aTextSearch; // EditText to input search queries
    private FirebaseFirestore fStore; // Firebase Firestore instance
    private RecyclerView searchRecyclerView; // RecyclerView to display search results
    private ProductsAdapter productsAdapter; // Adapter for the RecyclerView
    private List<Products> productsList = new ArrayList<>(); // List to store products data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Initialize variables and views
        homeFragment = new HomeFragment();
        aAuth = FirebaseAuth.getInstance();
        aToolbar = findViewById(R.id.toolbar_home);
        setSupportActionBar(aToolbar);
        aTextSearch = findViewById(R.id.text_search);
        searchRecyclerView = findViewById(R.id.search_recycler);
        fStore = FirebaseFirestore.getInstance();
        productsList = new ArrayList<>();
        productsAdapter = new ProductsAdapter(this, productsList);

        // Set a LinearLayoutManager with HORIZONTAL orientation for the RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        searchRecyclerView.setLayoutManager(linearLayoutManager);

        searchRecyclerView.setAdapter(productsAdapter);

        // Add text change listener to the search EditText
        aTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // If search text is empty, hide the RecyclerView. Else, perform search and show RecyclerView.
                if (s.toString().isEmpty()) {
                    searchRecyclerView.setVisibility(View.GONE);
                } else {
                    searchProduct(s.toString());
                    searchRecyclerView.setVisibility(View.VISIBLE);
                }
            }

            // Method to search products based on input text
            private void searchProduct(String text) {
                fStore.collection("Products")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                // If task is successful, filter products based on search text and update adapter
                                if(task.isSuccessful() && task.getResult() != null) {
                                    List<Products> tempList = new ArrayList<>();
                                    for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                                        Products product = doc.toObject(Products.class);
                                        if (product != null && product.getModel().toString().toLowerCase().contains(text.toLowerCase())) {
                                            tempList.add(product);
                                        }
                                    }
                                    productsAdapter.updateData(tempList);
                                }
                            }
                        });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Initialize fragment if it's null and add it to the activity
        if (homeFragment == null) {
            homeFragment = new HomeFragment();
        }
        runFragment(homeFragment);
    }

    // Method to add the fragment to the activity
    private void runFragment(HomeFragment fragment) {
        if (!fragment.isAdded()) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.homeContainer, homeFragment);
            transaction.commit();
        }
    }

    // Inflate the menu for this activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);

        // Set click listener for the cart menu item
        MenuItem item = menu.findItem(R.id.action_cart);
        View actionView = item.getActionView();
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When clicked, navigate to the CartActivity
                Log.d(TAG, "Cart menu item selected");
                Intent intentCart = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(intentCart);
            }
        });

        return true;
    }

    // Handle clicks on the menu items
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout_btn:
                // If logout button is clicked, sign out and navigate to MainActivity
                aAuth.signOut();
                Intent intentLogout = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intentLogout);
                finish();
                break;
            case R.id.manage_addresses_btn:
                // If manage addresses button is clicked, navigate to CrudAddressActivity
                Intent intentAddress = new Intent(HomeActivity.this, CrudAddressActivity.class);
                startActivity(intentAddress);
                break;
            case R.id.account_setting_btn:
                // If account settings button is clicked, navigate to SettingsActivity
                Intent intentSettings = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(intentSettings);
                break;
            case R.id.action_cart:
                // If cart button is clicked, navigate to CartActivity
                Log.i(TAG, "Cart menu item selected");
                Intent intentCart = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(intentCart);
                break;
        }
        return true;
    }
}
