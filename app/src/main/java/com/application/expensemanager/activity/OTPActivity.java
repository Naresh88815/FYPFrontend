package com.application.expensemanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.application.expensemanager.R;

public class OTPActivity extends AppCompatActivity {
    AppCompatButton verifyBtn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_layout);

        verifyBtn = findViewById(R.id.verifyButton);
        verifyBtn = findViewById(R.id.verifyButton);

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoNextPage();
            }
        });
    }

    private void gotoNextPage() {
        Intent intent = new Intent(OTPActivity.this, AdminMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

}
