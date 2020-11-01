package com.kraaiennest.kraaiennestapp.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.kraaiennest.kraaiennestapp.PresenceActivity;
import com.kraaiennest.kraaiennestapp.R;
import com.kraaiennest.kraaiennestapp.ScanActivity;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_CAMERA_PERMISSION = 10;

    private ConstraintLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        container = findViewById(R.id.main_grid);

        View register = findViewById(R.id.register_btn);
        register.setBackgroundColor(Color.parseColor("#F79922"));
        register.setOnClickListener(event -> startScanner());

        View checkIn = findViewById(R.id.check_in_btn);
        checkIn.setBackgroundColor(Color.parseColor("#5133AB"));

        View presence = findViewById(R.id.presence_btn);
        presence.setBackgroundColor(Color.parseColor("#AC193D"));
        presence.setOnClickListener(event -> startPresence());

        // Request camera permissions
        if (isCameraPermissionGranted()) {
            container.setVisibility(View.VISIBLE);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }

    private void startScanner() {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
    }

    private void startPresence() {
        Intent intent = new Intent(this, PresenceActivity.class);
        startActivity(intent);
    }

    private boolean isCameraPermissionGranted() {
        int selfPermission = ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA);
        return selfPermission == PackageManager.PERMISSION_GRANTED;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (isCameraPermissionGranted()) {
                container.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this, "Camera permission is required.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
