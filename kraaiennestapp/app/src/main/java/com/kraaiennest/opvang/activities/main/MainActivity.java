package com.kraaiennest.opvang.activities.main;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import com.kraaiennest.opvang.R;
import com.kraaiennest.opvang.activities.checkin.CheckInActivity;
import com.kraaiennest.opvang.activities.presence.PresenceActivity;
import com.kraaiennest.opvang.activities.register.RegisterActivity;
import com.kraaiennest.opvang.databinding.ActivityMainBinding;
import com.kraaiennest.opvang.repository.ChildRepository;
import dagger.hilt.android.AndroidEntryPoint;

import javax.inject.Inject;
import java.util.concurrent.ExecutionException;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 10;
    private static final int RC_SIGN_IN = 404;
    private ActivityMainBinding binding;

    @Inject
    ChildRepository repository;

    private boolean disabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler("Main"));

        // Make sure this is before calling super.onCreate
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // Request camera permissions
        if (isCameraPermissionGranted()) {
            binding.mainLayout.setVisibility(View.VISIBLE);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }

        if (!hasInternet(this)) {
            Toast.makeText(this, "Geen internet connectie.", Toast.LENGTH_LONG).show();
            disabled = true;
        }

        try {
            repository.getChildren().get();
        } catch (ExecutionException | InterruptedException e) {
            Toast.makeText(this, "Kan de lijst met kinderen niet inladen. " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            disabled = true;
        }

        View registerA = findViewById(R.id.register_btn_A);
        registerA.setOnClickListener(event -> startRegister("A"));
        registerA.setEnabled(!disabled);

        View registerO = findViewById(R.id.register_btn_O);
        registerO.setOnClickListener(event -> startRegister("O"));
        registerO.setEnabled(!disabled);

        View checkIn = findViewById(R.id.check_in_btn);
        checkIn.setOnClickListener(event -> startCheckIn());
        checkIn.setEnabled(!disabled);

        View presence = findViewById(R.id.presence_btn);
        presence.setOnClickListener(event -> startPresence());
        presence.setEnabled(!disabled);

    }

    public static boolean hasInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            Network network = connectivityManager.getActiveNetwork();
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            if (capabilities != null)
                if (capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET))
                    return true;
        }
        return false;
    }

    private void startRegister(String partOfDay) {
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra("partOfDay", partOfDay);
        startActivity(intent);
    }

    private void startPresence() {
        Intent intent = new Intent(this, PresenceActivity.class);
        startActivity(intent);
    }

    private void startCheckIn() {
        Intent intent = new Intent(this, CheckInActivity.class);
        startActivity(intent);
    }

    private boolean isCameraPermissionGranted() {
        int selfPermission = ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA);
        return selfPermission == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (isCameraPermissionGranted()) {
                binding.mainLayout.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this, "Camera permission is required.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public boolean isDisabled() {
        return disabled;
    }
}
