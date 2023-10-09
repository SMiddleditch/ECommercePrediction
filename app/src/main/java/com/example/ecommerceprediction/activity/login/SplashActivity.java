package com.example.ecommerceprediction.activity.login;

// Necessary imports
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.example.ecommerceprediction.R;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        int LIMIT = 2000; // Time limit for the splash screen (2000 ms = 2 seconds)

        // Create a new Handler to run a delay of the specified LIMIT
        new Handler().postDelayed(() -> {
            // After the delay, start the MainActivity
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);

            // Close the SplashActivity to prevent it from appearing in the device's recent options
            finish();

        }, LIMIT); // End of the delay
    }
}
