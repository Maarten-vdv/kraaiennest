package com.kraaiennest.opvang.activities.nfc;

import static com.kraaiennest.opvang.model.FoundChildIdType.NFC;
import static com.kraaiennest.opvang.model.FoundChildIdType.PIN;
import static com.kraaiennest.opvang.model.FoundChildIdType.QR;

import android.content.SharedPreferences;
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
import com.kraaiennest.opvang.activities.checkin.CheckInViewModel;
import com.kraaiennest.opvang.activities.main.ExceptionHandler;
import com.kraaiennest.opvang.activities.register.ApiCallState;
import com.kraaiennest.opvang.activityContracts.InputChildId;
import com.kraaiennest.opvang.activityContracts.WriteChildInfo;
import com.kraaiennest.opvang.databinding.ActivityNfcWriteOverviewBinding;
import com.kraaiennest.opvang.model.Child;
import com.kraaiennest.opvang.model.FoundChildIdType;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class NfcWriteOverviewActivity extends AppCompatActivity {

    private NfcOverviewViewModel model;
    private ActivityResultLauncher<FoundChildIdType> findChildActivityLauncher;
    private ActivityResultLauncher<Child> findWriteActivityLauncher;
    private boolean chainRegistrations;
    private FoundChildIdType lastFindType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler("NfcWriteOverview"));

        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        chainRegistrations = preferences.getBoolean("chainRegistrations", false);

        ActivityNfcWriteOverviewBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_nfc_write_overview);
        binding.setLifecycleOwner(this);
        View view = binding.getRoot();
        setContentView(view);

//        Toolbar toolbar = findViewById(R.id.toolbar_nfc);
//        setSupportActionBar(toolbar);
//        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
//        toolbar.setNavigationOnClickListener(e -> finish());

        model = new ViewModelProvider(this).get(NfcOverviewViewModel.class);
        binding.setViewmodel(model);
        Map<Integer, String> strings = new HashMap<>();
        strings.put(R.string.scan_child, getString(R.string.scan_child));
        model.loadExtra(strings);
        binding.nfcBtn.setEnabled(true);

        binding.nfcBtn.setOnClickListener(click -> {
            this.findChildActivityLauncher.launch(NFC);
            this.startNfcWrite(this.model.getChild().getValue());
//            CompletableFuture<Void> checkIn = model.createCheckIn();
//            checkIn.thenAccept(result -> {
//                if (chainRegistrations) {
//                    switch (lastFindType) {
//                        case QR:
//                            startScan();
//                            break;
//                        case PIN:
//                            startPin();
//                            break;
//                    }
//                }
//            });
        });
        binding.nfcScanBtn.setOnClickListener(click -> startScan());
        binding.nfcPinBtn.setOnClickListener(click -> startPin());

        model.getState().observe(this, state -> {
            if (state.equals(ApiCallState.BUSY)) {
                binding.nfcBtn.showLoading();
            } else {
                binding.nfcBtn.hideLoading();
            }

            if (state.equals(ApiCallState.SUCCESS)) {
                Toast.makeText(this, R.string.check_in_success, Toast.LENGTH_SHORT).show();
                model.writeDone();
            }

            if (state.equals(ApiCallState.ERROR)) {
                Toast.makeText(this, R.string.check_in_fail, Toast.LENGTH_SHORT).show();
                model.writeDone();
            }
        });

        findWriteActivityLauncher = registerForActivityResult(new WriteChildInfo(), result -> {
            new ViewModelProvider(this).get(NfcOverviewViewModel.class).clearChild();
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
                        new ViewModelProvider(this).get(NfcOverviewViewModel.class).loadChildByQrId(foundChildId.getId());
                    }
                    break;
                case PIN:
                    new ViewModelProvider(this).get(NfcOverviewViewModel.class).loadChildByPIN(foundChildId.getId());
                    break;
                case NFC:
                    new ViewModelProvider(this).get(NfcOverviewViewModel.class).loadChildByNFC(foundChildId.getId());
                    break;
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        model.clearChild();
    }

    private void startScan() {
        this.findChildActivityLauncher.launch(QR);
    }

    private void startPin() {
        this.findChildActivityLauncher.launch(PIN);
    }

    private void startNfcWrite(Child child) {
        this.findWriteActivityLauncher.launch(child);
    }

}
