package com.kraaiennest.opvang.activities.nfc;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;

import com.kraaiennest.opvang.model.ndef.ParsedNdefRecord;
import com.kraaiennest.opvang.model.ndef.TextRecord;

import java.util.ArrayList;
import java.util.List;

public class NdefMessageParser {

    private NdefMessageParser() {
    }

    public static List<ParsedNdefRecord> parse(NdefMessage message) {
        return getRecords(message.getRecords());
    }

    public static List<ParsedNdefRecord> getRecords(NdefRecord[] records) {
        List<ParsedNdefRecord> elements = new ArrayList<>();

        for (final NdefRecord record : records) {
            if (TextRecord.isText(record)) {
                elements.add(TextRecord.parse(record));
            } else {
                elements.add(() -> new String(record.getPayload()));
            }
        }

        return elements;
    }
}