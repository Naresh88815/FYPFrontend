package com.application.expensemanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.application.expensemanager.R;

public class RegisterEmployee extends AppCompatActivity {

    EditText employeeName, mobileNumber, email, accNumber, ifscCode, upiCode;
    AppCompatButton submitBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regsiter_employee);
        initView();

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                checkIsLogin();
                        Intent intent = new Intent(RegisterEmployee.this, RegisterThanking.class);
                        startActivity(intent);

                    }
                }, 500);
            }
        });
    }

    public void initView(){
        employeeName = findViewById(R.id.employeeName);
        mobileNumber = findViewById(R.id.mobileNumber);
        email = findViewById(R.id.email);
        accNumber = findViewById(R.id.accNumber);
        ifscCode = findViewById(R.id.ifscCode);
        upiCode = findViewById(R.id.upiCode);
        submitBtn = findViewById(R.id.submitBtn);
    }
}
