package com.kraaiennest.kraaiennestapp;

import android.app.Activity;
import android.content.Intent;
import android.net.UrlQuerySanitizer;
import android.os.Bundle;
import android.util.Size;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.zxing.Result;
import com.kraaiennest.kraaiennestapp.qr.QrCodeAnalyzer;
import com.kraaiennest.kraaiennestapp.qr.QrCodeListener;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ScanActivity extends AppCompatActivity {

    public static final String SCANNED_USER_ID = "userid";

    private PreviewView previewView;
    private Executor cameraExecutor = Executors.newSingleThreadExecutor();
    private QrCodeAnalyzer analyzer;

    private boolean found;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        previewView = findViewById(R.id.preview_view);

        analyzer = new QrCodeAnalyzer();
        analyzer.register(result -> {
            if(!found) {
                String url = result.getText();
                String userId = new UrlQuerySanitizer(url).getValue("userId");
                Intent resultIntent = new Intent();
                resultIntent.putExtra(SCANNED_USER_ID, userId);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });

        startCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.found = false;
    }

    private void goToRegistration(String url) {
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra(Constants.URL, url);
        startActivity(intent);
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {

            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreviewAnsAnalysis(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future
                // This should never be reached
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindPreviewAnsAnalysis(ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().setTargetAspectRatio(AspectRatio.RATIO_4_3).
                setTargetRotation(previewView.getDisplay().getRotation())
                .build();


        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();
        imageAnalysis.setAnalyzer(cameraExecutor, analyzer);

        cameraProvider.bindToLifecycle(this, cameraSelector, imageAnalysis, preview);
    }
}
