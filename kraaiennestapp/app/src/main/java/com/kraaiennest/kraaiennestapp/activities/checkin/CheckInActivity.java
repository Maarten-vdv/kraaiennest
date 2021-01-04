package com.kraaiennest.kraaiennestapp.activities.checkin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import com.kraaiennest.kraaiennestapp.R;
import com.kraaiennest.kraaiennestapp.databinding.ActivityCheckInBinding;
import com.kraaiennest.kraaiennestapp.activities.register.ApiCallState;
import com.kraaiennest.kraaiennestapp.activities.scan.ScanActivity;
import dagger.hilt.android.AndroidEntryPoint;

import java.util.HashMap;
import java.util.Map;

import static com.kraaiennest.kraaiennestapp.activities.scan.ScanActivity.SCANNED_USER_ID;

@AndroidEntryPoint
public class CheckInActivity extends AppCompatActivity {

    public static final int CHECK_IN_SCAN_REQUEST = 1;

    private ActivityCheckInBinding binding;
    private CheckInViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_check_in);
        binding.setLifecycleOwner(this);
        View view = binding.getRoot();
        setContentView(view);

        Toolbar toolbar = findViewById(R.id.toolbar_checkin);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(e -> finish());


        model = new ViewModelProvider(this).get(CheckInViewModel.class);
        binding.setViewmodel(model);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        Map<Integer, String> strings = new HashMap<>();
        strings.put(R.string.scan_child, getString(R.string.scan_child));
        model.loadExtra(getIntent(), sharedPreferences.getString("scriptId", ""), strings);

        binding.checkInBtn.setOnClickListener(click -> model.createCheckIn());
        binding.checkInScanBtn.setOnClickListener(click -> startScan());

        model.getCheckInState().observe(this, state -> {
            if (state.equals(ApiCallState.BUSY)) {
                binding.checkInBtn.startAnimation();
            } else {
                binding.checkInBtn.revertAnimation();
            }

            if (state.equals(ApiCallState.SUCCESS)) {
                Toast.makeText(this, R.string.check_in_success, Toast.LENGTH_SHORT).show();
                model.checkInDone();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        model.clearChild();
    }

    private void startScan() {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivityForResult(intent, CHECK_IN_SCAN_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == CHECK_IN_SCAN_REQUEST && resultCode == RESULT_OK) {
            String userId = data.getStringExtra(SCANNED_USER_ID);
            if (userId != null) {
                CheckInViewModel model = new ViewModelProvider(this).get(CheckInViewModel.class);
                model.loadChild(userId);
            }
        }
    }
}
