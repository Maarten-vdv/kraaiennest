package com.kraaiennest.opvang.activities.register;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.kraaiennest.opvang.R;
import com.kraaiennest.opvang.activities.main.ExceptionHandler;
import com.kraaiennest.opvang.activityContracts.InputChildId;
import com.kraaiennest.opvang.databinding.ActivityRegisterBinding;
import com.kraaiennest.opvang.model.FoundChildIdType;
import com.kraaiennest.opvang.model.PartOfDay;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RegisterActivity extends AppCompatActivity {

    private RegisterViewModel model;
    private ActivityResultLauncher<FoundChildIdType> findChildActivityLauncher;
    private boolean chainRegistrations;
    private FoundChildIdType lastFindType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler("Register"));
        super.onCreate(savedInstanceState);

        String partOfDay = getIntent().getStringExtra("partOfDay");

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        chainRegistrations = preferences.getBoolean("chainRegistrations", false);

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

        binding.registerRegisterBtn.setOnClickListener(click -> {
            CompletableFuture<Void> registration = model.createRegistration();
            registration.thenAccept(result -> {
                if (chainRegistrations) {
                    switch (lastFindType) {
                        case QR:
                            startScan();
                            break;
                        case PIN:
                            startPin();
                            break;
                        case NFC:
                            startNfc();
                            break;
                    }
                }
            });
        });

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
        binding.registerNfcBtn.setOnClickListener(click -> startNfc());
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

        findChildActivityLauncher = registerForActivityResult(new InputChildId(), foundChildId -> {
            if (foundChildId == null) {
                return;
            }

            lastFindType = foundChildId.getType();
            switch (foundChildId.getType()) {
                case QR:
                    if (foundChildId.getId() == null) {
                        Toast.makeText(this, R.string.not_valid_qr, Toast.LENGTH_SHORT).show();
                    } else {
                        new ViewModelProvider(this).get(RegisterViewModel.class).loadChildByQrId(foundChildId.getId());
                    }
                    break;
                case PIN:
                    new ViewModelProvider(this).get(RegisterViewModel.class).loadChildByPIN(foundChildId.getId());
                    break;
                case NFC:
                    new ViewModelProvider(this).get(RegisterViewModel.class).loadChildByNFC(foundChildId.getId());
                    break;
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.model.clearChild();
    }

    private void startNfc() {
        this.findChildActivityLauncher.launch(FoundChildIdType.NFC);
    }

    private void startScan() {
        this.findChildActivityLauncher.launch(FoundChildIdType.QR);
    }

    private void startPin() {
        this.findChildActivityLauncher.launch(FoundChildIdType.PIN);
    }
}
