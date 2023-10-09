package com.example.ecommerceprediction.activity;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ecommerceprediction.R;
import com.example.ecommerceprediction.activity.address.AddressActivity;
import com.example.ecommerceprediction.activity.login.LoginActivity;
import com.example.ecommerceprediction.holders.Products;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DescriptActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    // Initialize View elements and Firebase Firestore
    private ImageView pImage;
    private TextView pBrand, pModel, pPrice, pVoteCount, pId, pStarCount, pGender, pColor;
    private Button pCartAdd, pBuyBtn;
    private Products products;
    private FirebaseFirestore fStore;
    private String userId;
    private Spinner sizeSpinner;
    private int selectedSize;

    // Available sizes for products
    ArrayList<Integer> pSpinnerNumbers = new ArrayList<>(Arrays.asList(3, 4, 5, 6, 7, 8, 9, 10, 11, 12));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descript);

        // Initialize views
        initializeViewElements();

        // Fetch the product data passed through intent
        Object obj = getIntent().getSerializableExtra("descript");
        if(obj instanceof Products){
            products = (Products) obj;
            populateProductDetails();
        }

        // Initialize Firestore
        fStore = FirebaseFirestore.getInstance();

        // Get current user's ID
        userId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (checkUserLogin()) {
            try {
                // Hash the user ID
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] hash = md.digest(userId.getBytes(StandardCharsets.UTF_8));
                String hashedUserId = String.format("%064x", new BigInteger(1, hash));

                // Map to store user data for Firestore
                Map<String, Object> userData = new HashMap<>();
                userData.put("UserID", hashedUserId); // Storing hashed UserID in the Recommend document

                // Write hashed user ID to Firestore's Recommend collection
                fStore.collection("Recommend").document(hashedUserId).set(userData);

                // Check if user consent is given and store clicked product details
                getUserConsent(hashedUserId, isAllowed -> {
                    Log.d("DescriptActivity", "Checkbox status: " + isAllowed);
                    if (isAllowed && products != null) {
                        Map<String, Object> productData = new HashMap<>();
                        productData.put("ProductID", products.getProductID());
                        productData.put("Brand", products.getBrand());
                        productData.put("Color", products.getColor());
                        productData.put("Gender", products.getGender());
                        productData.put("Material", products.getMaterial());
                        productData.put("Timestamp", FieldValue.serverTimestamp());

                        // Store product details in Firestore's Recommend -> ClickedProducts collection
                        fStore.collection("Recommend").document(hashedUserId).collection("ClickedProducts").document().set(productData)
                                .addOnSuccessListener(aVoid -> Log.d("DescriptActivity", "DocumentSnapshot successfully written!"))
                                .addOnFailureListener(e -> Log.w("DescriptActivity", "Error writing document", e));
                    }
                });
            } catch (Exception e) {
                // Handle any errors in hashing the user ID
                Log.w("DescriptActivity", "Error hashing user ID", e);
            }
        }

        // Setup click listeners for buy and review buttons
        setupOnClickListeners();

        // Set up the spinner for size selection
        ArrayAdapter<Integer> ad = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, pSpinnerNumbers);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sizeSpinner.setAdapter(ad);
    }

    // Handle item selection in spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedSize = pSpinnerNumbers.get(position);
    }

    // Handle no item selected in spinner
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    // Initialize views by binding with layout
    private void initializeViewElements() {
        pImage = findViewById(R.id.imageProduct);
        pBrand = findViewById(R.id.textBrandName);
        pModel = findViewById(R.id.textModelName);
        pPrice = findViewById(R.id.textPrice);
        pVoteCount = findViewById(R.id.voteCount);
        pId = findViewById(R.id.b_id);
        pCartAdd = findViewById(R.id.buttonCartAdd);
        pBuyBtn = findViewById(R.id.buttonBuy);
        pStarCount = findViewById(R.id.starRating);
        pGender = findViewById(R.id.b_gender);
        pColor = findViewById(R.id.textColor);
        sizeSpinner = findViewById(R.id.sizeSpinner);
        sizeSpinner.setOnItemSelectedListener(this);
    }

    // Populate the UI with product details
    private void populateProductDetails() {
        Glide.with(getApplicationContext()).load(products.getP_image_url()).into(pImage);
        pBrand.setText(products.getBrand());
        pColor.setText("Colour: " + products.getColor());
        pModel.setText((CharSequence) products.getModel());
        pId.setText(products.getProductID());
        pPrice.setText(String.format("£" + "%.2f", products.getPrice()));
        pVoteCount.setText("(" + products.getRatingCount() + " votes)");
        setStarCount(products.getAverageRating());
        pGender.setText(products.getGender().equals("Men") ? "Mens" : "Womens");
    }

    // Update star rating based on the average rating
    private void setStarCount(double avgRating) {
        if (avgRating > 4) {
            pStarCount.setText("5 star");
        } else if (avgRating > 3) {
            pStarCount.setText("4 star");
        } else if (avgRating > 2) {
            pStarCount.setText("3 star");
        } else if (avgRating > 1) {
            pStarCount.setText("2 star");
        } else {
            pStarCount.setText("1 star");
        }
    }

    // Setup button click listeners
    private void setupOnClickListeners() {
        // Handle buy button click
        pBuyBtn.setOnClickListener(view -> {
            if (checkUserLogin()) {
                Intent intent = new Intent(DescriptActivity.this, AddressActivity.class);
                if (products != null) {
                    intent.putExtra("descript", products);
                }
                startActivity(intent);
            }
        });

        // Handle review button click
        findViewById(R.id.reviewsButton).setOnClickListener(view -> {
            if (checkUserLogin()) {
                Intent intent = new Intent(DescriptActivity.this, ReviewsActivity.class);
                if (products != null) {
                    intent.putExtra("product_id", products.getProductID());  // pass only the product ID
                }
                startActivity(intent);
            }
        });


        // Handle add to cart button click
        pCartAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkUserLogin()) {
                    // Retrieve product details
                    String productId = products.getProductID();
                    String productBrand = products.getBrand();
                    String productModel = products.getModel().toString();
                    String productImage = products.getP_image_url();
                    String productGender = products.getGender();
                    int productSize = selectedSize; // Selected size
                    double productPrice = products.getPrice();

                    // Retrieve the user ID
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    // Add the product to Firebase
                    addToCart(userId, productId, productBrand, productModel, productImage, productGender, productSize, productPrice);

                    Toast.makeText(DescriptActivity.this, "Item added to cart", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void addToCart(String userId, String productId, String productBrand, String productModel, String productImage, String productGender, int productSize, double productPrice) {
        // Create a new product in the "cart" collection
        Map<String, Object> product = new HashMap<>();
        product.put("id", productId);
        product.put("brand", productBrand);
        product.put("model", productModel);
        product.put("image", productImage);
        product.put("gender", productGender);
        product.put("size", productSize);
        product.put("price", productPrice);

        FirebaseFirestore.getInstance().collection("User").document(userId).collection("Cart").document().set(product)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Item successfully added to cart!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding item to cart", e);
                    }
                });
    }


    // Method to check if the user is logged in
    private boolean checkUserLogin() {
        if (userId == null) {
            Toast.makeText(DescriptActivity.this, "You need to login to perform this action", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(DescriptActivity.this, LoginActivity.class);
            startActivity(intent);
            return false;
        }
        return true;
    }

    private void getUserConsent(String userId, final FirestoreCallback firestoreCallback) {
        Log.d("DescriptActivity", "Getting user consent for UserID: " + userId);
        fStore.collection("ConsentCheck").document(userId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d("DescriptActivity", "Document data: " + document.getData());
                                Boolean isAllowedObject = document.getBoolean("isAllowed");
                                boolean isAllowed = isAllowedObject != null ? isAllowedObject : false;
                                Log.d("DescriptActivity", "Fetched isAllowed value: " + isAllowed);
                                firestoreCallback.onCallback(isAllowed);
                            } else {
                                Log.d("DescriptActivity", "No such document");
                                firestoreCallback.onCallback(false);
                            }
                        } else {
                            Log.d("DescriptActivity", "get failed with ", task.getException());
                            firestoreCallback.onCallback(false);
                        }
                    }
                });
    }



    private interface FirestoreCallback {
        void onCallback(boolean isAllowed);
    }
}

