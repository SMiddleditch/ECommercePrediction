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
import com.example.ecommerceprediction.holders.Account;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

// This class represents an activity where the user can edit an existing account
public class EditAccountActivity extends AppCompatActivity {
    private EditText aName, aAddress, aCityTown, aPost, aNumber;
    private Button aSaveButton;
    private Toolbar aToolbar;
    private FirebaseFirestore aStore;
    private FirebaseAuth aAuth;
    private String accountId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        // Initialize the UI elements
        aName = findViewById(R.id.editName);
        aAddress = findViewById(R.id.editLineAddress);
        aCityTown = findViewById(R.id.editTown);
        aPost = findViewById(R.id.editPostcode);
        aNumber = findViewById(R.id.editNum);
        aSaveButton = findViewById(R.id.saveAccountBtn);

        // Set up the toolbar
        aToolbar = findViewById(R.id.toolbar_edit_account);
        setSupportActionBar(aToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize the Firebase Firestore and Auth instances
        aStore = FirebaseFirestore.getInstance();
        aAuth = FirebaseAuth.getInstance();

        // Retrieve the account id from the intent extras
        accountId = getIntent().getStringExtra("accountId");

        // Fetch the current account details
        fetchAccountDetails();

        // Set an onClickListener on the Save Button
        aSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Update the account details in Firestore
                updateAccountDetails();
            }
        });
    }

    // Fetch the current account details from Firestore and display them in the EditText fields
    private void fetchAccountDetails() {
        // Get the hashed user ID
        String originalUserId = aAuth.getCurrentUser().getUid();
        String hashedUserId;
        try {
            hashedUserId = hashFunction(originalUserId);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return;
        }

// Fetch the current account details from Firestore and display them in the EditText fields
        aStore.collection("User").document(hashedUserId)
                .collection("AccountDeats").document(accountId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                // Set the EditText fields with the current account details
                                aName.setText(document.getString("name"));
                                aAddress.setText(document.getString("address"));
                                aCityTown.setText(document.getString("town"));
                                aPost.setText(document.getString("post"));

                                // Convert the 'number' field to String before setting it to the EditText
                                Integer number = document.getLong("number").intValue();
                                aNumber.setText(String.valueOf(number));
                            }
                        }
                    }
                });
    }

    // Update the account details in Firestore
    private void updateAccountDetails() {
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
        Map<String, Object> accountMap = new HashMap<>();
        accountMap.put("name", name);
        accountMap.put("address", address);
        accountMap.put("town", town);
        accountMap.put("post", post);
        accountMap.put("number", number);

        // Get the hashed user ID
        String originalUserId = aAuth.getCurrentUser().getUid();
        String hashedUserId;
        try {
            hashedUserId = hashFunction(originalUserId);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return;
        }

// Update the account details in Firestore and show a toast if the operation is successful
        aStore.collection("User").document(hashedUserId)
                .collection("AccountDeats").document(accountId)
                .set(accountMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(EditAccountActivity.this, "Account details updated", Toast.LENGTH_SHORT).show();
                            finish();
                        }
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
