package com.kraaiennest.kraaiennestapp.register;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.kraaiennest.kraaiennestapp.R;
import com.kraaiennest.kraaiennestapp.ScanActivity;
import com.kraaiennest.kraaiennestapp.databinding.ActivityRegisterBinding;
import com.kraaiennest.kraaiennestapp.model.Child;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.kraaiennest.kraaiennestapp.ScanActivity.SCANNED_USER_ID;

public class RegisterActivity extends AppCompatActivity {

    public static final int REGISTER_SCAN_REQUEST = 2;
    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register);
        binding.setLifecycleOwner(this);
        View view = binding.getRoot();
        setContentView(view);

        ViewModelProvider.Factory factory = new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull @NotNull Class<T> modelClass) {
                Map<Integer, String> strings = new HashMap<>();
                strings.put(R.string.scan_child, getString(R.string.scan_child));
                strings.put(R.string.half_hours, getString(R.string.half_hours));
                return (T) new RegisterViewModel(strings);
            }
        };
        RegisterViewModel model = new ViewModelProvider(this, factory).get(RegisterViewModel.class);
        binding.setViewmodel(model);
        model.loadExtra(getIntent());

        binding.registerBtn.setOnClickListener(click -> model.createRegistration());

        model.getRegistrationState().observe(this, state -> {
            if (state.equals(RegistrationState.BUSY)) {
                binding.registerBtn.startAnimation();
            } else {
                binding.registerBtn.revertAnimation();
            }

            if(state.equals(RegistrationState.SUCCESS)) {
                Toast.makeText(this, R.string.registration_success , Toast.LENGTH_SHORT).show();
                model.registrationDone();
            }
        });

        model.getChild().observe(this, this::loadChild);

        binding.registerScanBtn.setOnClickListener(click -> startScan());
        binding.registerBackBtn.setOnClickListener(click -> finish());
        binding.registerLower.setOnClickListener(click -> model.addHalfHours(-1));
        binding.registerHigher.setOnClickListener(click -> model.addHalfHours(1));
    }

    private void startScan() {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivityForResult(intent, REGISTER_SCAN_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == REGISTER_SCAN_REQUEST && resultCode == RESULT_OK) {
            String userId = data.getStringExtra(SCANNED_USER_ID);
            if (userId != null) {
                RegisterViewModel model = new ViewModelProvider(this).get(RegisterViewModel.class);
                model.loadChild(userId);
            }
        }
    }

    private void loadChild(Child child) {
        binding.registerChildName.setText(child != null ? child.getFirstName() + " " + child.getLastName() : getString(R.string.scan_code_message));
        binding.registerChildGroup.setText(child != null ? child.getGroup() : "");
    }
}
