package com.example.ecommerceprediction.activity.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ecommerceprediction.R;

public class SettingsActivity extends AppCompatActivity {

    // Declare UI elements
    private Button changePasswordBtn;
    private Button deleteAccountBtn;
    private Button recSystemBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Associate UI elements with their counterparts in the layout
        recSystemBtn = findViewById(R.id.recSystemBtn);
        changePasswordBtn = findViewById(R.id.changePasswordBtn);
        deleteAccountBtn = findViewById(R.id.deleteAccountBtn);

        // Set up the onClick listener for the Recommendation System button.
        // It starts the CheckRecActivity when clicked
        recSystemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, CheckRecActivity.class);
                startActivity(intent);
            }
        });

        // Set up the onClick listener for the Change Password button.
        // It starts the ChangePasswordActivity when clicked
        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
            }
        });

        // Set up the onClick listener for the Delete Account button.
        // It starts the DeleteUserActivity when clicked
        deleteAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, DeleteUserActivity.class);
                startActivity(intent);
            }
        });
    }
}
