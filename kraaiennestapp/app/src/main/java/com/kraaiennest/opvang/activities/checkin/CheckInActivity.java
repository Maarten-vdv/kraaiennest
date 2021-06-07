package com.kraaiennest.opvang.activities.checkin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import com.kraaiennest.opvang.R;
import com.kraaiennest.opvang.activities.main.ExceptionHandler;
import com.kraaiennest.opvang.activities.pin.PinActivity;
import com.kraaiennest.opvang.activities.register.ApiCallState;
import com.kraaiennest.opvang.activities.scan.ScanActivity;
import com.kraaiennest.opvang.databinding.ActivityCheckInBinding;
import dagger.hilt.android.AndroidEntryPoint;

import java.util.HashMap;
import java.util.Map;

import static com.kraaiennest.opvang.activities.pin.PinActivity.CHILD_PIN;
import static com.kraaiennest.opvang.activities.scan.ScanActivity.SCANNED_USER_ID;

@AndroidEntryPoint
public class CheckInActivity extends AppCompatActivity {

    public static final int CHECK_IN_SCAN_REQUEST = 10;
    public static final int CHECK_IN_PIN_REQUEST = 11;
    private CheckInViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler("CheckIn"));

        super.onCreate(savedInstanceState);

        ActivityCheckInBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_check_in);
        binding.setLifecycleOwner(this);
        View view = binding.getRoot();
        setContentView(view);

        Toolbar toolbar = findViewById(R.id.toolbar_checkin);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(e -> finish());

        model = new ViewModelProvider(this).get(CheckInViewModel.class);
        binding.setViewmodel(model);
        Map<Integer, String> strings = new HashMap<>();
        strings.put(R.string.scan_child, getString(R.string.scan_child));
        model.loadExtra(strings);
        binding.checkInBtn.setEnabled(true);

        binding.checkInBtn.setOnClickListener(click -> model.createCheckIn());
        binding.checkInScanBtn.setOnClickListener(click -> startScan());
        binding.checkInPinBtn.setOnClickListener(click -> startPin());

        model.getCheckInState().observe(this, state -> {
            if (state.equals(ApiCallState.BUSY)) {
                binding.checkInBtn.setLoading(true);
            } else {
                binding.checkInBtn.setLoading(false);
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

    private void startPin() {
        Intent intent = new Intent(this, PinActivity.class);
        startActivityForResult(intent, CHECK_IN_PIN_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHECK_IN_SCAN_REQUEST && resultCode == RESULT_OK) {
            String userId = data.getStringExtra(SCANNED_USER_ID);
            if (userId != null) {
                new ViewModelProvider(this).get(CheckInViewModel.class).loadChildById(userId);
            } else {
                Toast.makeText(this, R.string.not_valid_qr, Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == CHECK_IN_PIN_REQUEST && resultCode == RESULT_OK) {
            String pin = data.getStringExtra(CHILD_PIN);
            new ViewModelProvider(this).get(CheckInViewModel.class).loadChildByPIN(pin);
        }
    }
}
