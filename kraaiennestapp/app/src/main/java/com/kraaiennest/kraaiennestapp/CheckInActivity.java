package com.kraaiennest.kraaiennestapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import com.kraaiennest.kraaiennestapp.api.APIInterface;
import com.kraaiennest.kraaiennestapp.api.APIService;
import com.kraaiennest.kraaiennestapp.model.Child;
import okhttp3.ResponseBody;
import org.parceler.Parcels;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.kraaiennest.kraaiennestapp.ScanActivity.SCANNED_USER_ID;

public class CheckInActivity extends AppCompatActivity {

    public static final int CHECK_IN_SCAN_REQUEST = 1;

    private List<Child> children;
    private Child child;
    private APIInterface api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);

        children = Parcels.unwrap(getIntent().getParcelableExtra("children"));
        api = APIService.getClient().create(APIInterface.class);

        CircularProgressButton checkInBtn = findViewById(R.id.check_in_btn);
        checkInBtn.setOnClickListener(click -> {
            checkInBtn.startAnimation();
            if (child != null) {
                Call<ResponseBody> checkIn = api.doPostCheckIn(child);

                checkIn.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        checkInBtn.revertAnimation();
                        loadChild(null);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                    }
                });
            }
        });

        CircularProgressButton scanBtn = findViewById(R.id.scan_btn);
        scanBtn.setOnClickListener(click -> {
            startScan();
        });

        CircularProgressButton backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(click -> {
            finish();
        });

        startScan();
    }

    private void startScan() {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivityForResult(intent, CHECK_IN_SCAN_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == CHECK_IN_SCAN_REQUEST) {
            if (resultCode == RESULT_OK) {
                String userId = data.getStringExtra(SCANNED_USER_ID);
                if (userId != null) {
                    Child child = children.stream().filter(c -> c.getQrId().equals(userId)).findFirst().orElse(null);
                    loadChild(child);
                }
            }
        }
    }

    private void loadChild(Child child) {
        this.child = child;
        TextView childName = findViewById(R.id.child_name);
        childName.setText(child != null ? child.getFirstName() + " " + child.getLastName() : getString(R.string.scan_code_message));
        TextView childGroup = findViewById(R.id.child_group);
        childGroup.setText(child != null ? child.getGroup() : "");

    }
}
