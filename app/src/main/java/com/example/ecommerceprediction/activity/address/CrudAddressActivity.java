package com.example.ecommerceprediction.activity.address;

// Import necessary libraries and dependencies
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.ecommerceprediction.R;
import com.example.ecommerceprediction.activity.HomeActivity;
import com.example.ecommerceprediction.activity.login.LoginActivity;
import com.example.ecommerceprediction.activity.login.MainActivity;
import com.example.ecommerceprediction.adapter.AccountAdapter;
import com.example.ecommerceprediction.holders.Account;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class CrudAddressActivity extends AppCompatActivity {
    // Declare all the private variables used in the activity
    private AccountAdapter aAccountAdapter;
    private RecyclerView aAccountRecyclerView;
    private List<Account> aAccountList;
    private Button sAddAccount;
    private FirebaseFirestore aStore;
    private FirebaseAuth aAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crud_address);

        // Bind the UI components to the corresponding objects
        aAccountRecyclerView = findViewById(R.id.account_recyclerView);
        sAddAccount = findViewById(R.id.new_address_btn);

        // Initialize Firebase Auth and Firestore instances
        aAuth = FirebaseAuth.getInstance();
        aStore = FirebaseFirestore.getInstance();

        // Initialize Account list and AccountAdapter
        aAccountList = new ArrayList<>();
        aAccountAdapter = new AccountAdapter(getApplicationContext(), aAccountList, this);
        aAccountRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        aAccountRecyclerView.setAdapter(aAccountAdapter);

        // Set up the button to navigate to the AddAddressActivity
        sAddAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CrudAddressActivity.this, AddAddressActivity.class);
                startActivity(intent); // Start the activity
            }
        });

        // Fetch the accounts when activity is created
        fetchAccounts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Fetch the accounts when activity resumes
        fetchAccounts();
    }

    public void fetchAccounts() {
        // Get the hashed user ID
        String originalUserId = aAuth.getCurrentUser().getUid();
        String hashedUserId;
        try {
            hashedUserId = hashFunction(originalUserId);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return;
        }

        // Fetch the AccountDetails from Firestore for the hashed user ID
        aStore.collection("User").document(hashedUserId)
                .collection("AccountDeats").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            aAccountList.clear();
                            for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                                Account account = doc.toObject(Account.class);
                                account.setId(doc.getId());  // Set the ID of the account
                                aAccountList.add(account);
                            }
                            aAccountAdapter.notifyDataSetChanged();
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


    public void deleteAccount(String accountId) {
        // Show an alert dialog to confirm deletion of account
        new AlertDialog.Builder(this)
                .setTitle("Delete Address")
                .setMessage("Are you sure you want to delete this address?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        // Get the hashed user ID
                        String originalUserId = aAuth.getCurrentUser().getUid();
                        String hashedUserId;
                        try {
                            hashedUserId = hashFunction(originalUserId);
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                            return;
                        }
                        aStore.collection("User").document(hashedUserId)
                                .collection("AccountDeats").document(accountId)
                                .delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(CrudAddressActivity.this, "Account details deleted", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(CrudAddressActivity.this, MainActivity.class);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(CrudAddressActivity.this, "Failed to delete account", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                })
                .setNegativeButton("No", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    public void editAccount(String accountId) {
        // Navigate to EditAccountActivity with the selected account id
        Intent intent = new Intent(CrudAddressActivity.this, EditAccountActivity.class);
        intent.putExtra("accountId", accountId);
        startActivity(intent);
    }
}
