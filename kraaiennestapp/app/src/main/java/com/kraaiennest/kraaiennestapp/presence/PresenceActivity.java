package com.kraaiennest.kraaiennestapp.presence;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.kraaiennest.kraaiennestapp.R;
import com.kraaiennest.kraaiennestapp.model.PresenceSortOrder;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class PresenceActivity extends AppCompatActivity implements SortOptionsFragment.SortDialogListener {

    private PresenceViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presence);

        Toolbar toolbar = findViewById(R.id.toolbar_presence);
        setSupportActionBar(toolbar);

        ViewModelProvider.Factory factory = new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull @NotNull Class<T> modelClass) {
                Map<Integer, String> strings = new HashMap<>();
                strings.put(R.string.error_load_presence, getString(R.string.error_load_presence));
                return (T) new PresenceViewModel(strings);
            }
        };

        model = new ViewModelProvider(this, factory).get(PresenceViewModel.class);
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
