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
import com.kraaiennest.kraaiennestapp.model.PartOfDay;
import org.jetbrains.annotations.NotNull;

import java.time.format.DateTimeFormatter;
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
                return (T) new RegisterViewModel(strings, DateTimeFormatter.ofPattern("HH:mm"));
            }
        };
        RegisterViewModel model = new ViewModelProvider(this, factory).get(RegisterViewModel.class);
        binding.setViewmodel(model);
        model.loadExtra(getIntent());
        model.loadChild(null);

        binding.registerBtn.setOnClickListener(click -> model.createRegistration());

        model.getRegistrationState().observe(this, state -> {
            if (state.equals(ApiCallState.BUSY)) {
                binding.registerBtn.startAnimation();
            } else {
                binding.registerBtn.revertAnimation();
            }

            if(state.equals(ApiCallState.SUCCESS)) {
                Toast.makeText(this, R.string.registration_success , Toast.LENGTH_SHORT).show();
                model.registrationDone();
            }
        });

        model.getChild().observe(this, this::loadChild);

        binding.registerScanBtn.setOnClickListener(click -> startScan());
        binding.registerBackBtn.setOnClickListener(click -> finish());
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
