package com.kraaiennest.opvang.activities.scan;

import static com.kraaiennest.opvang.activityContracts.InputChildId.FOUND_CHILD_ID;

import android.app.Activity;
import android.content.Intent;
import android.net.UrlQuerySanitizer;
import android.os.Bundle;
import android.util.Size;
import android.view.Surface;
import android.webkit.URLUtil;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.kraaiennest.opvang.R;
import com.kraaiennest.opvang.model.FoundChildId;
import com.kraaiennest.opvang.model.FoundChildIdType;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ScanActivity extends AppCompatActivity {

    private final Executor cameraExecutor = Executors.newSingleThreadExecutor();
    private QrCodeAnalyzer analyzer;

    private boolean found;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        analyzer = new QrCodeAnalyzer();
        analyzer.register(result -> {
            if (!found) {
                String url = result.getText();
                String userId = null;

                if (URLUtil.isValidUrl(url)) {
                    userId = new UrlQuerySanitizer(url).getValue(FOUND_CHILD_ID);
                } else if (url.startsWith(FOUND_CHILD_ID + "=")) {
                    userId = url.replace(FOUND_CHILD_ID + "=", "");
                }

                Intent resultIntent = new Intent();
                resultIntent.putExtra(FOUND_CHILD_ID, new FoundChildId(userId, FoundChildIdType.QR));
                setResult(Activity.RESULT_OK, resultIntent);
            }
            found = true;
            finish();
        });

        startCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.found = false;
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {

            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreviewAnalysis(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future
                // This should never be reached
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindPreviewAnalysis(ProcessCameraProvider cameraProvider) {
        PreviewView previewView = findViewById(R.id.preview_view);
        Preview preview = new Preview.Builder().setTargetAspectRatio(AspectRatio.RATIO_4_3).
                setTargetRotation(previewView == null || previewView.getDisplay() == null ?
                        Surface.ROTATION_0 : previewView.getDisplay().getRotation())
                .build();


        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(previewView != null ? previewView.getSurfaceProvider() : null);

        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(800, 600))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();
        imageAnalysis.setAnalyzer(cameraExecutor, analyzer);

        cameraProvider.bindToLifecycle(this, cameraSelector, imageAnalysis, preview);
    }
}
