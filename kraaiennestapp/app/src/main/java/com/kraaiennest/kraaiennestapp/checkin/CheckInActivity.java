package com.kraaiennest.kraaiennestapp.checkin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import com.kraaiennest.kraaiennestapp.R;
import com.kraaiennest.kraaiennestapp.ScanActivity;
import com.kraaiennest.kraaiennestapp.databinding.ActivityCheckInBinding;
import com.kraaiennest.kraaiennestapp.register.ApiCallState;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.kraaiennest.kraaiennestapp.ScanActivity.SCANNED_USER_ID;

public class CheckInActivity extends AppCompatActivity {

    public static final int CHECK_IN_SCAN_REQUEST = 1;

    private ActivityCheckInBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_check_in);
        binding.setLifecycleOwner(this);
        View view = binding.getRoot();
        setContentView(view);

        ViewModelProvider.Factory factory = new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull @NotNull Class<T> modelClass) {
                Map<Integer, String> strings = new HashMap<>();
                strings.put(R.string.scan_child, getString(R.string.scan_child));
                return (T) new CheckInViewModel(strings);
            }
        };

        CheckInViewModel model = new ViewModelProvider(this, factory).get(CheckInViewModel.class);
        binding.setViewmodel(model);
        model.loadExtra(getIntent());
        model.loadChild(null);

        binding.checkInBtn.setOnClickListener(click -> model.createCheckIn());
        binding.checkInBackBtn.setOnClickListener(click -> finish());
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
