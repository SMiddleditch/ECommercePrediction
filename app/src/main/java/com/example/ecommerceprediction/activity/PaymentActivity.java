package com.example.ecommerceprediction.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecommerceprediction.R;

public class PaymentActivity extends AppCompatActivity {
    // UI component declarations
    private TextView aProduct;
    private TextView aShipping;
    private TextView aTotal;
    private Button aCheckOut;
    private Toolbar aToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Retrieve the price of the product from the intent
        double amount = getIntent().getDoubleExtra("Price", 0.0);

        // Initialize TextViews, Button and Toolbar by matching with UI elements
        aProduct = findViewById(R.id.product_amount);
        aCheckOut = findViewById(R.id.CheckoutBtn);
        aShipping = findViewById(R.id.shipping_amount);
        aTotal = findViewById(R.id.total_amount);
        aToolbar = findViewById(R.id.toolbar_payment);

        // Display product amount on TextView
        aProduct.setText(String.format("£%.2f", amount));

        // Display static shipping cost on TextView
        aShipping.setText(String.format("£%.2f", 10.0));

        // Calculate and display the total cost
        double totalCost = amount + 10.0;
        aTotal.setText(String.format("£%.2f", totalCost));

        // Set the toolbar as the app bar for this Activity
        setSupportActionBar(aToolbar);
        // Enable the Up button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set up the onClickListener for the checkout button
        aCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Implement the checkout functionality
                Toast.makeText(PaymentActivity.this, "hey there toast", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
