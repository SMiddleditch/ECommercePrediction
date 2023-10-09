package com.example.ecommerceprediction.activity.address;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ecommerceprediction.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

// This class represents an activity where the user can add a new address
public class AddAddressActivity extends AppCompatActivity {

    // Declare the UI elements and Firebase instances
    private EditText aName, aAddress, aCityTown, aPost, aNumber;
    private Button aAddressButton;
    private Toolbar aToolbar;
    private FirebaseFirestore aStore;
    private FirebaseAuth aAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);

        // Initialize the UI elements
        aName = findViewById(R.id.addName);
        aAddress = findViewById(R.id.addLineAddress);
        aCityTown = findViewById(R.id.addTown);
        aPost = findViewById(R.id.addPostcode);
        aNumber = findViewById(R.id.addNum);
        aAddressButton = findViewById(R.id.addAddressBtn);

        // Set up the toolbar
        aToolbar = findViewById(R.id.toolbar_add_account);
        setSupportActionBar(aToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize the Firebase Firestore and Auth instances
        aStore = FirebaseFirestore.getInstance();
        aAuth = FirebaseAuth.getInstance();

        // Set an onClickListener on the Add Address Button
        aAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the input from the fields
                String name = aName.getText().toString();
                String address = aAddress.getText().toString();
                String town = aCityTown.getText().toString();
                String post = aPost.getText().toString();
                String numberString = aNumber.getText().toString();

                // Check if any field is left empty
                if (name.isEmpty()) {
                    aName.setError("Name is required");
                    return;
                }
                if (address.isEmpty()) {
                    aAddress.setError("Address is required");
                    return;
                }
                if (town.isEmpty()) {
                    aCityTown.setError("Town is required");
                    return;
                }
                if (post.isEmpty()) {
                    aPost.setError("Post is required");
                    return;
                }
                if (numberString.isEmpty()) {
                    aNumber.setError("Number is required");
                    return;
                }

                // Convert the number to Integer
                int number = Integer.parseInt(numberString);

                // Prepare the data to be added to Firestore
                Map<String, Object> aMap = new HashMap<>();
                aMap.put("name", name);
                aMap.put("address", address);
                aMap.put("town", town);
                aMap.put("post", post);
                aMap.put("number", number);

                // Get the hashed user ID
                String originalUserId = aAuth.getCurrentUser().getUid();
                String hashedUserId;
                try {
                    hashedUserId = hashFunction(originalUserId);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    return;
                }

            // Add the data to Firestore and show a toast if the operation is successful
                aStore.collection("User").document(hashedUserId).collection("AccountDeats").add(aMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(AddAddressActivity.this, "Account details added", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
            }
        });

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
}

