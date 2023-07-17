package com.kraaiennest.opvang.activityContracts;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kraaiennest.opvang.activities.nfc.NfcWriteActivity;
import com.kraaiennest.opvang.model.Child;

import org.parceler.Parcels;

public class WriteChildInfo extends ActivityResultContract<Child, Boolean> {

    public static final String CHILD = "child";

    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, @NonNull Child child) {
        Intent intent = new Intent(context, NfcWriteActivity.class);
        intent.putExtra(CHILD, Parcels.wrap(child));
        return intent;
    }

    @Override
    public Boolean parseResult(int resultCode, @Nullable Intent result) {
        return resultCode == Activity.RESULT_OK && result != null;
    }
}