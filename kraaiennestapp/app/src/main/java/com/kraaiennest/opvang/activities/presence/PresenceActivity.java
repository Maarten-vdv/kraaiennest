package com.kraaiennest.opvang.activities.presence;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import com.kraaiennest.opvang.R;
import com.kraaiennest.opvang.model.PresenceSortOrder;
import dagger.hilt.android.AndroidEntryPoint;

import java.util.HashMap;
import java.util.Map;

@AndroidEntryPoint
public class PresenceActivity extends AppCompatActivity implements SortOptionsFragment.SortDialogListener {

    private PresenceViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presence);

        Toolbar toolbar = findViewById(R.id.toolbar_presence);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(e -> finish());

        model = new ViewModelProvider(this).get(PresenceViewModel.class);

        Map<Integer, String> strings = new HashMap<>();
        strings.put(R.string.error_load_presence, getString(R.string.error_load_presence));

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String scriptId = sharedPreferences.getString("scriptId", "");
        model.loadExtra(scriptId, strings);
    }

    @Override
    public void onDialogPositiveClick(PresenceSortOrder sortOrder) {
        this.model.setSortOrder(sortOrder);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_sort) {
            SortOptionsFragment dialog = new SortOptionsFragment();
            dialog.show(getSupportFragmentManager(), "SortOptionsFragment");
            return true;
        } else if (item.getItemId() == R.id.action_refresh) {
            model.refreshPresences();
        } else if (item.getItemId() == R.id.action_filter) {
            model.refreshPresences();
        }

        return super.onOptionsItemSelected(item);
    }


    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_presence, menu);
        return true;
    }
}
