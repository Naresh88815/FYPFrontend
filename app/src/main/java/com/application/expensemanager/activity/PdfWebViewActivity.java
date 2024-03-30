package com.application.expensemanager.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.application.expensemanager.R;

public class PdfWebViewActivity extends AppCompatActivity {
    public static final String PDF_URL_EXTRA = "pdf_url";
    private WebView pdfWebView;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_web_view);

        pdfWebView = findViewById(R.id.pdfWebView);
        WebSettings webSettings = pdfWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("PDF is Loading...");
        progressDialog.setCancelable(true);
        progressDialog.show();

        String pdfUrl = getIntent().getStringExtra(PDF_URL_EXTRA);
        String pdfURL = "https://docs.google.com/gview?embedded=true&url=" + pdfUrl;
        pdfWebView.loadUrl(pdfURL);

        pdfWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressDialog.dismiss();
            }
        });
    }

}