package com.example.ecommerceprediction.activity.address;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.ecommerceprediction.R;
import com.example.ecommerceprediction.activity.PaymentActivity;
import com.example.ecommerceprediction.adapter.AccountAdapter;
import com.example.ecommerceprediction.holders.Account;
import com.example.ecommerceprediction.holders.Products;
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

public class AddressActivity extends AppCompatActivity {

    private AccountAdapter aAccountAdapter;
    private RecyclerView aAccountRecyclerView;
    private List<Account> aAccountList;
    private Button aForwardPaymentBtn;
    private Button sAddAccount;
    private Toolbar aToolbar;
    private FirebaseFirestore aStore;
    private FirebaseAuth aAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_address);

        Object obj = getIntent().getSerializableExtra("descript");

        aAccountRecyclerView = findViewById(R.id.account_recyclerView);
        aForwardPaymentBtn = findViewById(R.id.forward_payment_btn);
        sAddAccount = findViewById(R.id.new_address_btn);

        aAuth = FirebaseAuth.getInstance();
        aStore = FirebaseFirestore.getInstance();

        aAccountList = new ArrayList<>();
        aAccountAdapter = new AccountAdapter(getApplicationContext(), aAccountList);
        aAccountRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        aAccountRecyclerView.setAdapter(aAccountAdapter);

        aToolbar = findViewById(R.id.toolbar_account);
        setSupportActionBar(aToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the hashed user ID
        String originalUserId = aAuth.getCurrentUser().getUid();
        String hashedUserId;
        try {
            hashedUserId = hashFunction(originalUserId);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return;
        }

        // Fetch account details with the hashed user ID
        aStore.collection("User").document(hashedUserId)
                .collection("AccountDeats").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                                Account account = doc.toObject(Account.class);
                                aAccountList.add(account);
                            }
                            aAccountAdapter.notifyDataSetChanged();
                        }
                    }
                });


        sAddAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddressActivity.this, AddAddressActivity.class);
                startActivity(intent);
            }
        });

        aForwardPaymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!aAccountAdapter.isAnyItemSelected()) {
                    Toast.makeText(AddressActivity.this, "Please select an address or add a new one", Toast.LENGTH_SHORT).show();
                    return;
                }
                double amount = 0.0;
                if(obj instanceof Products){
                    Products b = (Products) obj;
                    amount = b.getPrice();
                }

                Intent intent = new Intent(AddressActivity.this, PaymentActivity.class);
                intent.putExtra("Price", amount);
                startActivity(intent);
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
