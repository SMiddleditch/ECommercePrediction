package com.example.ecommerceprediction.activity.login;

// Necessary imports
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.ecommerceprediction.R;
import com.example.ecommerceprediction.activity.HomeActivity;
import com.example.ecommerceprediction.activity.settings.CheckRecActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    // Declare input fields and register button
    private EditText eName;
    private EditText eEmail;
    private EditText ePassword;
    private TextView eRegBtn;
    private Toolbar aToolbar;

    // Declare Firebase Auth object
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        // Link declared variables to their respective IDs in the layout
        eName = findViewById(R.id.regName);
        eEmail = findViewById(R.id.regEmail);
        ePassword = findViewById(R.id.regPassword);
        eRegBtn = findViewById(R.id.loginBtn);
        aToolbar = findViewById(R.id.toolbar_reg);
        setSupportActionBar(aToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();


        // Set an onClick listener on the registration button
        eRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get input data
                String name = eName.getText().toString();
                String email = eEmail.getText().toString();
                String password = ePassword.getText().toString();

                // Check if no field is empty
                if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                    // Try to create a new user with Firebase Auth
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // If registration is successful, log the user in
                            if (task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Account successfully created", Toast.LENGTH_SHORT).show();
                                Intent consentIntent = new Intent(RegisterActivity.this, CheckRecActivity.class);
                                startActivity(consentIntent);
                            } else {
                                // If registration fails, log the error and notify the user
                                Log.e("RegistrationError", "Error : " + task.getException(), task.getException());
                                Toast.makeText(RegisterActivity.this, "Failed to register. Please try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(RegisterActivity.this, "Please fill empty field", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Method to navigate to Login activity
    public void signIn(View view) {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
