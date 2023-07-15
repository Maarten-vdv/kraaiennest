package com.kraaiennest.opvang.activities.pin;

import static com.kraaiennest.opvang.activityContracts.InputChildId.FOUND_CHILD_ID;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.kraaiennest.opvang.R;
import com.kraaiennest.opvang.activities.main.ExceptionHandler;
import com.kraaiennest.opvang.databinding.ActivityPinBinding;
import com.kraaiennest.opvang.model.FoundChildId;
import com.kraaiennest.opvang.model.FoundChildIdType;

public class PinActivity extends AppCompatActivity {

    private PinViewModel model;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler("PIN"));
        super.onCreate(savedInstanceState);

        ActivityPinBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_pin);
        binding.setLifecycleOwner(this);
        View view = binding.getRoot();
        setContentView(view);

        model = new ViewModelProvider(this).get(PinViewModel.class);
        binding.setViewmodel(model);

        binding.pinCodeButton0.setOnClickListener(e -> model.addDigit("0"));
        binding.pinCodeButton1.setOnClickListener(e -> model.addDigit("1"));
        binding.pinCodeButton2.setOnClickListener(e -> model.addDigit("2"));
        binding.pinCodeButton3.setOnClickListener(e -> model.addDigit("3"));
        binding.pinCodeButton4.setOnClickListener(e -> model.addDigit("4"));
        binding.pinCodeButton5.setOnClickListener(e -> model.addDigit("5"));
        binding.pinCodeButton6.setOnClickListener(e -> model.addDigit("6"));
        binding.pinCodeButton7.setOnClickListener(e -> model.addDigit("7"));
        binding.pinCodeButton8.setOnClickListener(e -> model.addDigit("8"));
        binding.pinCodeButton9.setOnClickListener(e -> model.addDigit("9"));

        binding.pinCodeButtonDelete.setOnClickListener(e -> model.back());

        model.getPin().observe(this, pin -> binding.pinCodeButtonOk.setEnabled(pin.length() == 5));

        binding.pinCodeButtonOk.setOnClickListener(e -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(FOUND_CHILD_ID, new FoundChildId(model.getPin().getValue(), FoundChildIdType.PIN));
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        });
    }
}
