package com.kraaiennest.kraaiennestapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.kraaiennest.kraaiennestapp.api.APIInterface;
import com.kraaiennest.kraaiennestapp.api.APIService;
import com.kraaiennest.kraaiennestapp.checkin.CheckInActivity;
import com.kraaiennest.kraaiennestapp.model.Child;
import com.kraaiennest.kraaiennestapp.presence.PresenceActivity;
import com.kraaiennest.kraaiennestapp.register.RegisterActivity;
import org.parceler.Parcels;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_CAMERA_PERMISSION = 10;

    private ConstraintLayout container;
    private List<Child> children;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Make sure this is before calling super.onCreate
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        container = findViewById(R.id.main_grid);

        View register = findViewById(R.id.register_btn);
        register.setBackgroundColor(Color.parseColor("#F79922"));
        register.setOnClickListener(event -> startRegister());

        View checkIn = findViewById(R.id.register_scan_btn);
        checkIn.setBackgroundColor(Color.parseColor("#5133AB"));
        checkIn.setOnClickListener(event -> startCheckIn());

        View presence = findViewById(R.id.presence_btn);
        presence.setBackgroundColor(Color.parseColor("#AC193D"));
        presence.setOnClickListener(event -> startPresence());

        // Request camera permissions
        if (isCameraPermissionGranted()) {
            container.setVisibility(View.VISIBLE);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }

        if (children == null) {
            children = loadChildren();
        }
    }

    private List<Child> loadChildren() {
        APIInterface api = APIService.getClient().create(APIInterface.class);
        try {
            List<Child> children = api.doGetChildren().get();
            System.out.println(children.size());
            return children;
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        return Collections.emptyList();
    }

    private void startRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        Bundle bundle = new Bundle();
        Parcelable wrapped = Parcels.wrap(children);
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
        Parcelable wrapped = Parcels.wrap(children);
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
                container.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this, "Camera permission is required.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
