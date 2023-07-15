package com.kraaiennest.opvang.activityContracts;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kraaiennest.opvang.activities.nfc.NfcActivity;
import com.kraaiennest.opvang.activities.pin.PinActivity;
import com.kraaiennest.opvang.activities.scan.ScanActivity;
import com.kraaiennest.opvang.model.FoundChildId;
import com.kraaiennest.opvang.model.FoundChildIdType;

public class InputChildId extends ActivityResultContract<FoundChildIdType, FoundChildId> {

    public static final String FOUND_CHILD_ID = "foundChildId";

    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, @NonNull FoundChildIdType type) {
        switch (type) {
            case QR:
                return new Intent(context, ScanActivity.class);
            case NFC:
                return new Intent(context, NfcActivity.class);
            default:
            case PIN:
                return new Intent(context, PinActivity.class);
        }
    }

    @Override
    public FoundChildId parseResult(int resultCode, @Nullable Intent result) {
        if (resultCode != Activity.RESULT_OK || result == null) {
            return null;
        }
        return result.getParcelableExtra(FOUND_CHILD_ID);
    }
}