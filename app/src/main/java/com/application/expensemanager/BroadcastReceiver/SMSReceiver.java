package com.application.expensemanager.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.application.expensemanager.activity.LoginActivity;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

public class SMSReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);
                switch (status.getStatusCode()) {
                    case CommonStatusCodes.SUCCESS:
                        // Extract one-time code from the message
                        String otp = extras.getString(SmsRetriever.EXTRA_SMS_MESSAGE);
                        // Update your UI or perform any necessary action with the OTP

                        // Send OTP to LoginActivity using an Intent
                        Intent otpIntent = new Intent(context, LoginActivity.class);
                        otpIntent.setAction(LoginActivity. ACTION_RECEIVED_OTP);
                        otpIntent.putExtra(LoginActivity.EXTRA_OTP, otp);
                        context.startActivity(otpIntent);

                        break;

                    case CommonStatusCodes.TIMEOUT:
                        // Handle timeout
                        break;
                }
            }
        }
    }
}
