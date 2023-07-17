package com.kraaiennest.opvang.activities.nfc;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.kraaiennest.opvang.R;
import com.kraaiennest.opvang.activityContracts.WriteChildInfo;
import com.kraaiennest.opvang.model.Child;
import com.kraaiennest.opvang.model.ndef.ChildNdefMessage;

import org.parceler.Parcels;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class NfcWriteActivity extends AppCompatActivity {
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private Child child;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter == null) {
            Toast.makeText(this, "No NFC", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, this.getClass())
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                , PendingIntent.FLAG_MUTABLE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        child = Parcels.unwrap(getIntent().getParcelableExtra(WriteChildInfo.CHILD));

        if (nfcAdapter != null) {
            if (!nfcAdapter.isEnabled())
                showWirelessSettings();

            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }
    }

    private void showWirelessSettings() {
        Toast.makeText(this, "You need to enable NFC", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        resolveIntent(intent);
        finish();
    }

    private void resolveIntent(Intent intent) {
        String action = intent.getAction();

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            this.writeTag(tag, child);

            Intent resultIntent = new Intent();
            setResult(Activity.RESULT_OK, resultIntent);
        }
    }

    public void writeTag(Tag tag, Child child) {
        if (tag != null) {
            try {
                ChildNdefMessage message = new ChildNdefMessage(child.getQrId(), child.getFullName(), child.getId());
                NdefRecord textRecord = this.createTextRecord(message.getSource(), Locale.getDefault());

                Ndef ndefTag = Ndef.get(tag);
                if (ndefTag == null) {
                    // Let’s try to format the Tag in NDEF
                    NdefFormatable nForm = NdefFormatable.get(tag);
                    if (nForm != null) {
                        nForm.connect();
                        nForm.format(new NdefMessage(textRecord));
                        nForm.close();
                    }
                } else {
                    ndefTag.connect();
                    ndefTag.writeNdefMessage(new NdefMessage(textRecord));
                    ndefTag.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public NdefRecord createTextRecord(String payload, Locale locale) {
        byte[] langBytes = locale.getLanguage().getBytes(StandardCharsets.UTF_8);
        Charset utfEncoding = StandardCharsets.UTF_8;
        byte[] textBytes = payload.getBytes(utfEncoding);
        int utfBit = 0;
        char status = (char) (utfBit + langBytes.length);
        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        data[0] = (byte) status;
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);
        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
                NdefRecord.RTD_TEXT, new byte[0], data);
    }
}