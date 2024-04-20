package com.application.expensemanager.activity;

import static com.application.expensemanager.utils.Constants.getAppVersion;
import static com.application.expensemanager.utils.Constants.getAppVersionName;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.application.expensemanager.R;
import com.application.expensemanager.network.InternetConnection;
import com.application.expensemanager.utils.MyApplication;
import com.application.expensemanager.utils.SPCsnstants;
import com.application.expensemanager.utils.Utils;

public class SplashActivity extends AppCompatActivity {

    TextView appVersionTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        appVersionTxt = findViewById(R.id.appVersionTxt);
        appVersionTxt.setText("Version "+getAppVersionName(SplashActivity.this));

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkIsLogin();
                finish();

            }
        }, 1000);
    }
    public void checkIsLogin() {

        try {
            String islogin = MyApplication.mSp.getKey(SPCsnstants.IS_LOGGED_IN);
            boolean isNetworkAvailable = InternetConnection.isConnected(SplashActivity.this);
            if (isNetworkAvailable){
                if (islogin.equals(SPCsnstants.YES)) {
                    gotoNextPage();
                    finish();
                } else {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }
            else {
                gotoNoInternet();
            }

        }
        catch (Exception e)
        {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

    }
    private void gotoNextPage() {
        Intent intent = new Intent(SplashActivity.this, AdminMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void gotoNoInternet() {
        Intent intent = new Intent(SplashActivity.this, NoInternet.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}