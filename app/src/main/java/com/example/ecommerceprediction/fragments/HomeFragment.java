package com.example.ecommerceprediction.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceprediction.R;
import com.example.ecommerceprediction.activity.AllProductActivity;
import com.example.ecommerceprediction.adapter.CatAdapter;
import com.example.ecommerceprediction.adapter.PopAdapter;
import com.example.ecommerceprediction.adapter.ProductsAdapter;
import com.example.ecommerceprediction.holders.Categories;
import com.example.ecommerceprediction.holders.Products;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FirebaseFirestore fStore;  // Reference to Firestore database
    private FirebaseAuth mAuth;  // Reference to Firebase Authentication

    private RecyclerView recommendedRecyclerView;  // RecyclerView for recommended products
    private RecyclerView tCatRecyclerView;  // RecyclerView for categories
    private RecyclerView sProductsRecyclerView;  // RecyclerView for all products
    private RecyclerView popRecyclerView;  // RecyclerView for popular products

    private List<Products> recommendedProductsList;  // List for recommended products
    private List<Categories> tCategoryList;  // List for categories
    private List<Products> sProductsList;  // List for all products
    private List<Products> popList;  // List for popular products

    private ProductsAdapter recommendedProductsAdapter;  // Adapter for recommended products
    private CatAdapter tCatAdapter;  // Adapter for categories
    private ProductsAdapter sProductsAdapter;  // Adapter for all products
    private PopAdapter popAdapter;  // Adapter for popular products

    private TextView allProductsBrowse;  // TextView for all products
    private TextView allProductsPopular;  // TextView for popular products

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize Firestore and Firebase Authentication references
        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Initialize RecyclerViews
        recommendedRecyclerView = view.findViewById(R.id.recommendedRecycler);
        tCatRecyclerView = view.findViewById(R.id.catRecycler);
        sProductsRecyclerView = view.findViewById(R.id.browseRecycler);
        popRecyclerView = view.findViewById(R.id.popRecycler);

        // Initialize TextViews
        allProductsBrowse = view.findViewById(R.id.allProducts_browse);
        allProductsPopular = view.findViewById(R.id.allProducts_popular);

        // Setup on-click listeners for TextViews to navigate to AllProductActivity
        allProductsBrowse.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AllProductActivity.class);
            intent.putExtra("section", "browse");
            startActivity(intent);
        });

        allProductsPopular.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AllProductActivity.class);
            intent.putExtra("section", "popular");
            startActivity(intent);
        });

        // Initialize product and category lists
        recommendedProductsList = new ArrayList<>();
        tCategoryList = new ArrayList<>();
        sProductsList = new ArrayList<>();
        popList = new ArrayList<>();

        // Initialize product and category adapters
        recommendedProductsAdapter = new ProductsAdapter(getContext(), recommendedProductsList);
        tCatAdapter = new CatAdapter(getContext(), tCategoryList);
        sProductsAdapter = new ProductsAdapter(getContext(), sProductsList);
        popAdapter = new PopAdapter(getContext(), popList);

        // Setup RecyclerViews with adapters and layouts
        setupRecyclerView(recommendedRecyclerView, recommendedProductsAdapter);
        setupRecyclerView(tCatRecyclerView, tCatAdapter);
        setupRecyclerView(sProductsRecyclerView, sProductsAdapter);
        setupRecyclerView(popRecyclerView, popAdapter);

        return view;
    }

    // Helper method to setup RecyclerView with given adapter
    private void setupRecyclerView(RecyclerView recyclerView, RecyclerView.Adapter adapter) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Clear the product lists
        clearLists();

        // Fetch recommended products, categories, top 20 products and top 20 popular products
        fetchRecommendedProducts();
        fetchCategories();
        fetchTop20Products();
        fetchTop20PopularProducts();
    }

    // Helper method to clear product and category lists
    private void clearLists() {
        recommendedProductsList.clear();
        tCategoryList.clear();
        sProductsList.clear();
        popList.clear();
    }

    // Hashing function using SHA-256
    private String hashFunction(String originalString) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedhash = digest.digest(originalString.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(encodedhash);
    }

    // Convert bytes to hex
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private void fetchRecommendedProducts() {
        if (mAuth.getCurrentUser() != null) {
            try {
                String originalUserId = mAuth.getCurrentUser().getUid();
                String hashedUserId = hashFunction(originalUserId);
                fStore.collection("Recommend")
                        .document(hashedUserId)
                        .collection("RecommendedProducts")
                        .get()
                        .addOnCompleteListener(fetchRecommendedProductsOnCompleteListener());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
    }


    // Returns an OnCompleteListener for fetching recommended products
    private OnCompleteListener<QuerySnapshot> fetchRecommendedProductsOnCompleteListener() {
        return task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    // Get the product ID from the document data
                    String productId = document.getString("ProductID");
                    fetchProductById(productId);
                }
            } else {
                Log.w("TAG", "Error getting documents.", task.getException());
            }
        };
    }

    // Fetch a single product by its ID
    private void fetchProductById(String productId) {
        fStore.collection("Products")
                .document(productId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Products product = documentSnapshot.toObject(Products.class);
                        recommendedProductsList.add(product);
                        recommendedProductsAdapter.notifyDataSetChanged();
                    } else {
                        Log.d("TAG", "No such document in Products");
                    }
                })
                .addOnFailureListener(e -> Log.w("TAG", "Error getting product.", e));
    }

    // Fetch Categories
    private void fetchCategories() {
        fStore.collection("Categories")
                .get()
                .addOnCompleteListener(fetchCategoriesOnCompleteListener());
    }

    // Returns an OnCompleteListener for fetching categories
    private OnCompleteListener<QuerySnapshot> fetchCategoriesOnCompleteListener() {
        return task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Categories category = document.toObject(Categories.class);
                    tCategoryList.add(category);
                    tCatAdapter.notifyDataSetChanged();
                }
            } else {
                Log.w("TAG", "Error getting documents.", task.getException());
            }
        };
    }

    // Fetch top 20 Products
    private void fetchTop20Products() {
        fStore.collection("Products")
                .limit(20)
                .get()
                .addOnCompleteListener(fetchTop20ProductsOnCompleteListener());
    }

    // Returns an OnCompleteListener for fetching top 20 products
    private OnCompleteListener<QuerySnapshot> fetchTop20ProductsOnCompleteListener() {
        return task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Products product = document.toObject(Products.class);
                    sProductsList.add(product);
                    sProductsAdapter.notifyDataSetChanged();
                }
            } else {
                Log.w("TAG", "Error getting documents.", task.getException());
            }
        };
    }

    // Fetch top 20 Popular Products
    private void fetchTop20PopularProducts() {
        fStore.collection("Products")
                .whereGreaterThanOrEqualTo("averageRating", 3.3)
                .orderBy("averageRating", Query.Direction.DESCENDING)
                .limit(20)
                .get()
                .addOnCompleteListener(fetchTop20PopularProductsOnCompleteListener());
    }

    // Returns an OnCompleteListener for fetching top 20 popular products
    private OnCompleteListener<QuerySnapshot> fetchTop20PopularProductsOnCompleteListener() {
        return task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Products product = document.toObject(Products.class);
                    popList.add(product);
                    popAdapter.notifyDataSetChanged();
                }
            } else {
                Log.w("TAG", "Error getting documents.", task.getException());
            }
        };

    }


}

