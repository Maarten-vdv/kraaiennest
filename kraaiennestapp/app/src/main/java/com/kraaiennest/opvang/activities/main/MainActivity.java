package com.kraaiennest.opvang.activities.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       // Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler("Main"));

        // Make sure this is before calling super.onCreate
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        View registerA = findViewById(R.id.register_btn_A);
        registerA.setOnClickListener(event -> startRegister("A"));

        View registerO = findViewById(R.id.register_btn_O);
        registerO.setOnClickListener(event -> startRegister("O"));

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

        try {
            repository.getChildren().get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Kan de lijst mwt kinderen niet inladen");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {
                //startActivity(SignedInActivity.createIntent(this, response));
               // finish();
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
//                    showSnackbar(R.string.sign_in_cancelled);
//                    return;
                }

                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
//                    showSnackbar(R.string.no_internet_connection);
//                    return;
                }

//                showSnackbar(R.string.unknown_error);
                Log.e("SIGN_IN", "Sign-in error: ", response.getError());
            }
        }

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
}
