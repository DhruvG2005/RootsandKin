package com.example.rootskin;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


@SuppressLint("CustomSplashScreen")
@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class  SplashActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int SPLASH_TIME_OUT = 2000; // 2 seconds

    private String[] permissions = {
            Manifest.permission.INTERNET,
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.WAKE_LOCK
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Check if the app has all permissions
        if (!hasPermissions()) {
            // Request permissions
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
        } else {
            // All permissions granted, proceed to splash screen
            proceedToSplashScreen();
        }
    }

    private boolean hasPermissions() {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void proceedToSplashScreen() {
        new Handler().postDelayed(() -> {
            Intent i = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }, SPLASH_TIME_OUT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allPermissionsGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            if (allPermissionsGranted) {
                Toast.makeText(SplashActivity.this, "Permissions Granted", Toast.LENGTH_SHORT).show();
                proceedToSplashScreen();
            } else {
                Toast.makeText(SplashActivity.this, "Some Permissions Denied", Toast.LENGTH_SHORT).show();
                // Handle permission denied for specific permissions if needed
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        handlePermissionDenied(permissions[i]);
                    }
                }
            }
        }
    }

    private void handlePermissionDenied(String permission) {
        // Implement custom logic for denied permission
        Toast.makeText(SplashActivity.this, "Permission Denied: " + permission + " This permission will not work on your device!", Toast.LENGTH_SHORT).show();
        proceedToSplashScreen(); // Proceed anyway, or handle as needed
    }
}
