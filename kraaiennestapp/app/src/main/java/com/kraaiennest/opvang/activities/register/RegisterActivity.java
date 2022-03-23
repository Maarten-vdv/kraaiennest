package com.kraaiennest.opvang.activities.register;

import static com.kraaiennest.opvang.activities.pin.PinActivity.CHILD_PIN;
import static com.kraaiennest.opvang.activities.scan.ScanActivity.SCANNED_USER_ID;

import android.content.Intent;
import android.content.res.Configuration;
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
import com.kraaiennest.opvang.activities.scan.ScanActivity;
import com.kraaiennest.opvang.databinding.ActivityRegisterBinding;
import com.kraaiennest.opvang.model.PartOfDay;

import java.util.HashMap;
import java.util.Map;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RegisterActivity extends AppCompatActivity {

    public static final int REGISTER_SCAN_REQUEST = 2;
    public static final int REGISTER_PIN_REQUEST = 3;
    private RegisterViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler("Register"));
        super.onCreate(savedInstanceState);

        String partOfDay = getIntent().getStringExtra("partOfDay");

        ActivityRegisterBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_register);
        binding.setLifecycleOwner(this);
        View view = binding.getRoot();
        setContentView(view);

        Toolbar toolbar = findViewById(R.id.toolbar_register);
        toolbar.setBackgroundColor(partOfDay.equals("A") ?
                getResources().getColor(R.color.register_btn_color_A, getTheme())
                : getResources().getColor(R.color.register_btn_color_O, getTheme()));
        toolbar.setTitle(getText(partOfDay.equals("A") ? R.string.action_register_A : R.string.action_register_O));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(e -> finish());

        model = new ViewModelProvider(this).get(RegisterViewModel.class);
        binding.setViewmodel(model);

        Map<Integer, String> strings = new HashMap<>();
        strings.put(R.string.scan_child, getString(R.string.scan_child));
        int orientation = getResources().getConfiguration().orientation;
        strings.put(R.string.half_hours, (orientation == Configuration.ORIENTATION_LANDSCAPE ? " " : "\n") + getString(R.string.half_hours));
        model.loadExtra(strings);

        binding.registerRegisterBtn.setOnClickListener(click -> model.createRegistration());

        model.getRegistrationState().observe(this, state -> {
            if (state.equals(ApiCallState.BUSY)) {
                binding.registerRegisterBtn.showLoading();
            } else {
                binding.registerRegisterBtn.hideLoading();
            }

            if (state.equals(ApiCallState.SUCCESS)) {
                Toast.makeText(this, R.string.registration_success, Toast.LENGTH_SHORT).show();
                model.registrationDone();
            }

            if (state.equals(ApiCallState.ERROR)) {
                Toast.makeText(this, getString(R.string.registration_failed, model.getErrorMessage()), Toast.LENGTH_SHORT).show();
            }
        });

        binding.registerScanBtn.setOnClickListener(click -> startScan());
        binding.registerPinBtn.setOnClickListener(click -> startPin());
        binding.registerLower.setOnClickListener(click -> model.addHalfHours(-1));
        binding.registerHigher.setOnClickListener(click -> model.addHalfHours(1));

        model.getPartOfDay().observe(this, part -> {
            if (part.equals(PartOfDay.O)) {
                binding.clock1.setVisibility(View.VISIBLE);
                binding.cutoff1.setVisibility(View.INVISIBLE);
                binding.clock2.setVisibility(View.INVISIBLE);
                binding.cutoff2.setVisibility(View.VISIBLE);
            } else if (part.equals(PartOfDay.A)) {
                binding.clock1.setVisibility(View.INVISIBLE);
                binding.cutoff1.setVisibility(View.VISIBLE);
                binding.clock2.setVisibility(View.VISIBLE);
                binding.cutoff2.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.model.clearChild();
    }

    private void startScan() {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivityForResult(intent, REGISTER_SCAN_REQUEST);
    }

    private void startPin() {
        Intent intent = new Intent(this, PinActivity.class);
        startActivityForResult(intent, REGISTER_PIN_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REGISTER_SCAN_REQUEST && resultCode == RESULT_OK) {
            String userId = data.getStringExtra(SCANNED_USER_ID);
            if (userId != null) {
                new ViewModelProvider(this).get(RegisterViewModel.class).loadChildByQrId(userId);
            } else {
                Toast.makeText(this, R.string.not_valid_qr, Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REGISTER_PIN_REQUEST && resultCode == RESULT_OK) {
            String pin = data.getStringExtra(CHILD_PIN);
            new ViewModelProvider(this).get(RegisterViewModel.class).loadChildByPIN(pin);
        }
    }
}
