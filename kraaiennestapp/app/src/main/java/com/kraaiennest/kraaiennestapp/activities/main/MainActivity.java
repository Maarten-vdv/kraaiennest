package com.kraaiennest.kraaiennestapp.activities.main;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import com.kraaiennest.kraaiennestapp.R;
import com.kraaiennest.kraaiennestapp.activities.checkin.CheckInActivity;
import com.kraaiennest.kraaiennestapp.databinding.ActivityMainBinding;
import com.kraaiennest.kraaiennestapp.activities.presence.PresenceActivity;
import com.kraaiennest.kraaiennestapp.activities.register.RegisterActivity;
import dagger.hilt.android.AndroidEntryPoint;
import org.parceler.Parcels;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 10;
    private String scriptId;

    private MainViewModel model;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Make sure this is before calling super.onCreate
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener((c, key) -> {
            scriptId = c.getString("scriptId", "");
        });
        scriptId = sharedPreferences.getString("scriptId", "");

        model = new ViewModelProvider(this).get(MainViewModel.class);
        model.loadExtra(scriptId);

        View register = findViewById(R.id.register_btn);
        register.setOnClickListener(event -> startRegister());

        View checkIn = findViewById(R.id.check_in_btn);
        checkIn.setOnClickListener(event -> startCheckIn());

        View presence = findViewById(R.id.presence_btn);
        presence.setOnClickListener(event -> startPresence());

        // Request camera permissions
        if (isCameraPermissionGranted()) {
            binding.mainLayout.setVisibility(View.VISIBLE);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }

        // force initial load of child list
        model.getChildren();
    }

    private void startRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        Bundle bundle = new Bundle();
        Parcelable wrapped = Parcels.wrap(model.getChildren().getValue());
        bundle.putParcelable("children", wrapped);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void startPresence() {
        Intent intent = new Intent(this, PresenceActivity.class);
        startActivity(intent);
    }

    private void startCheckIn() {
        Intent intent = new Intent(this, CheckInActivity.class);
        Bundle bundle = new Bundle();
        Parcelable wrapped = Parcels.wrap(model.getChildren().getValue());
        bundle.putParcelable("children", wrapped);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private boolean isCameraPermissionGranted() {
        int selfPermission = ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA);
        return selfPermission == PackageManager.PERMISSION_GRANTED;
    }

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
}
