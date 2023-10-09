package com.example.ecommerceprediction.activity.settings;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ecommerceprediction.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.material.textfield.TextInputEditText;

public class ChangePasswordActivity extends AppCompatActivity {

    // Define the TAG for logging
    private static final String TAG = "ChangePasswordActivity";

    // Declare the UI elements
    private TextInputEditText emailField;
    private TextInputEditText currentPasswordField;
    private TextInputEditText newPasswordField;
    private Button changePasswordBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Initialize the UI elements
        emailField = findViewById(R.id.email);
        currentPasswordField = findViewById(R.id.currentPassword);
        newPasswordField = findViewById(R.id.newPassword);
        changePasswordBtn = findViewById(R.id.changePasswordBtn);

        // Set an onClickListener on the changePassword button
        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve and trim the input data
                String email = emailField.getText().toString().trim();
                String currentPassword = currentPasswordField.getText().toString().trim();
                String newPassword = newPasswordField.getText().toString().trim();

                // Check if any field is empty
                if (!email.isEmpty() && !currentPassword.isEmpty() && !newPassword.isEmpty()) {
                    // Get a reference to the current user
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    // Create an authentication credential object
                    AuthCredential credential = EmailAuthProvider.getCredential(email, currentPassword);

                    // Re-authenticate the user
                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Update the user password
                                user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // Log and show success message
                                            Log.d(TAG, "User password updated.");
                                            Toast.makeText(ChangePasswordActivity.this, "Password updated successfully!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            // Log and show error message
                                            Log.d(TAG, "Error password not updated", task.getException());
                                            Toast.makeText(ChangePasswordActivity.this, "Password update failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                // Log and show error message
                                Log.d(TAG, "Error auth failed", task.getException());
                                Toast.makeText(ChangePasswordActivity.this, "Authentication failed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    // Show error message if any field is empty
                    Toast.makeText(ChangePasswordActivity.this, "All fields must be filled out", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
