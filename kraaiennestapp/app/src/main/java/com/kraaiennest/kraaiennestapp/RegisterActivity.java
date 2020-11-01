package com.kraaiennest.kraaiennestapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;
import com.kraaiennest.kraaiennestapp.model.Child;

import static com.kraaiennest.kraaiennestapp.Constants.URL;

public class RegisterActivity extends AppCompatActivity {

    private Child currentChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        View button = findViewById(R.id.backBtn);
        button.setOnClickListener(v -> finish());

        Intent intent = getIntent();
        String url = intent.getStringExtra(URL);

        WebView webView = findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());


        WebSettings settings = webView.getSettings();

        settings.setJavaScriptEnabled(true);
        webView.loadUrl(url);
    }
}
