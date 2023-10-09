package com.example.ecommerceprediction.activity.settings;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ecommerceprediction.R;
import com.example.ecommerceprediction.activity.HomeActivity;
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

public class CheckRecActivity extends AppCompatActivity {

    // Constants for Firestore Collection field name

    private static final String consent = "isAllowed";

    // Declare variables for checkbox and Firestore instance
    private CheckBox allowRecSystemChk;
    private FirebaseFirestore fStore;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_rec);

        // Initialize Firestore and get the hashed current userId
        fStore = FirebaseFirestore.getInstance();
        try {
            userId = hashFunction(FirebaseAuth.getInstance().getCurrentUser().getUid());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        // Initialize the checkbox
        allowRecSystemChk = findViewById(R.id.allowRecSystemChk);

        // Load the user consent status and setup checkbox listener
        loadUserConsent();
        setupCheckboxListener();
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

    private void loadUserConsent() {
        // Fetch user's consent status from Firestore
        fStore.collection("ConsentCheck").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Boolean isAllowed = document.getBoolean("isAllowed");
                        allowRecSystemChk.setChecked(isAllowed != null && isAllowed);
                    } else {
                        Log.d("CheckRecActivity", "No such document");
                    }
                } else {
                    Log.d("CheckRecActivity", "get failed with ", task.getException());
                }
            }
        });
    }

    private void setupCheckboxListener() {
        // Set up a listener for changes in checkbox status
        allowRecSystemChk.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // If user consented, show a confirmation dialog
                showConsentDialog();
            } else {
                // If user denied consent, save this information
                saveUserConsent(false);
            }
        });
    }

    private void showConsentDialog() {
        // Show a dialog explaining data usage and ask for user consent
        new AlertDialog.Builder(this)
                .setTitle("Consent to use your data")
                .setMessage("By using this application, you acknowledge and agree that data pertaining to your interaction with and viewing of various products will be collected and analyzed. This information will be utilized solely for the purpose of enhancing and personalizing your product recommendations within this application, aiming to improve your user experience.\n" +
                        "\n" +
                        "Be assured that this information will be kept strictly confidential and will not be disclosed, distributed, or otherwise shared with any third-party entities.\n" +
                        "\n" +
                        "Please signify your understanding of, and consent to, these terms and conditions by proceeding with the use of this application. Your continuation constitutes your acceptance of these terms.")
                .setPositiveButton("Yes", (dialog, id) -> {
                    saveUserConsent(true);
                    Toast.makeText(this, "Recommendation system on", Toast.LENGTH_SHORT).show();
                    goToHomeActivity();
                })
                .setNegativeButton("No", (dialog, id) -> {
                    allowRecSystemChk.setChecked(false);
                    Toast.makeText(this, "Recommendation system off", Toast.LENGTH_SHORT).show();
                    saveUserConsent(false);
                    goToHomeActivity();
                })
                .create()
                .show();
    }

    private void goToHomeActivity() {
        Intent intent = new Intent(CheckRecActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void saveUserConsent(boolean isAllowed) {
        // Save the user's consent status to Firestore
        Map<String, Object> userConsent = new HashMap<>();
        userConsent.put(consent, isAllowed);
        fStore.collection("ConsentCheck").document(userId).set(userConsent);
    }
}
