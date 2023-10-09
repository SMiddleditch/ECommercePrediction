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
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DeleteUserActivity extends AppCompatActivity {

    // Tag used for logging
    private static final String TAG = "DeleteUserActivity";

    // UI elements
    private Button deleteAccountBtn;
    private TextInputEditText deleteEmail;
    private TextInputEditText deletePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_user);

        // Associate UI elements with their counterparts in the layout
        deleteAccountBtn = findViewById(R.id.confirmDeleteBtn);
        deleteEmail = findViewById(R.id.deleteEmail);
        deletePassword = findViewById(R.id.deletePassword);

        // Setup delete account button click listener
        deleteAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the email and password from the text inputs
                String email = deleteEmail.getText().toString().trim();
                String password = deletePassword.getText().toString().trim();

                // Get the current Firebase user
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                // Create the credentials for re-authentication
                AuthCredential credential = EmailAuthProvider.getCredential(email, password);

                // Re-authenticate the user
                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // If re-authentication is successful, log it
                            Log.d(TAG, "User re-authenticated.");

                            // Delete the user
                            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // If deletion is successful, log it and display a toast
                                        Log.d(TAG, "User account deleted.");
                                        Toast.makeText(DeleteUserActivity.this, "User account deleted.", Toast.LENGTH_SHORT).show();

                                        // Then redirect to the login screen or something...
                                    }
                                }
                            });
                        } else {
                            // If re-authentication fails, log it and display a toast
                            Log.d(TAG, "User could not be re-authenticated.", task.getException());
                            Toast.makeText(DeleteUserActivity.this, "Re-authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
