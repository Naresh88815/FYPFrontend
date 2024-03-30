package com.application.expensemanager.activity;

import static com.application.expensemanager.utils.MyApplication.apinetwork;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.application.expensemanager.BroadcastReceiver.SMSReceiver;
import com.application.expensemanager.R;
import com.application.expensemanager.network.VolleyResponse;
import com.application.expensemanager.utils.Constants;
import com.application.expensemanager.utils.MyApplication;
import com.application.expensemanager.utils.SPCsnstants;
import com.application.expensemanager.utils.Utils;
import com.goodiebag.pinview.Pinview;
import com.google.android.gms.auth.api.phone.SmsRetriever;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class LoginActivity extends AppCompatActivity {
    AppCompatButton loginButton;
    EditText login_phoneNumber;
    private long lastBackPressTime = 0;
    private AlertDialog otpDialog;
    Pinview otpPinview;
    TextView reSendTv, cancelTv, mobileTv;

    static Context context;
    String mobile = "";
    AppCompatButton verifyButton;
    public static final String ACTION_RECEIVED_OTP = "com.application.expensemanager.ACTION_RECEIVED_OTP";
    public static final String EXTRA_OTP = "extra_otp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        registerSMSReceiver();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mobile = login_phoneNumber.getText().toString().trim();
                if (mobile.isEmpty() || mobile.length() < 10) {
                    login_phoneNumber.setError("Invalid Mobile Number");
                } else {
                    sendOtpWithMobile();
                }


            }
        });
        handleReceivedOTP();
    }


    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastBackPressTime < 2000) {
            super.onBackPressed();
            finish();
        } else {
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            lastBackPressTime = currentTime;
        }
    }

    private void initView() {
        login_phoneNumber = findViewById(R.id.login_phoneNumber);
        loginButton = findViewById(R.id.loginButton);
        reSendTv = findViewById(R.id.reSendTv);
        cancelTv = findViewById(R.id.cancelTv);
        mobileTv = findViewById(R.id.mobileTv);
    }

    public void otpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        View view = LayoutInflater.from(LoginActivity.this).inflate(R.layout.otp_layout, null, false);
        builder.setView(view);
        builder.setCancelable(true);
        otpDialog = builder.create();
        otpDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        otpDialog.show();
        otpPinview = view.findViewById(R.id.otpPinview);

        verifyButton = view.findViewById(R.id.verifyButton);
        reSendTv = view.findViewById(R.id.reSendTv);
        cancelTv = view.findViewById(R.id.cancelTv);
        mobileTv = view.findViewById(R.id.mobileTv);


        reSendTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otpDialog.dismiss();
                sendOtpWithMobile();
            }
        });

        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otpDialog.dismiss();
            }
        });

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (otpPinview.getValue().equals("")) {
                    Toast.makeText(LoginActivity.this, "Please enter 4 digit otp", Toast.LENGTH_SHORT).show();
                } else {
                    loginWithMobile(otpPinview.getValue().toString());
                }
            }
        });
    }

    private boolean isLoggedIn() {
        return MyApplication.mSp.getKey(SPCsnstants.IS_LOGGED_IN).equals(SPCsnstants.YES);
    }


    private void sendOtpWithMobile() {
        Utils.showProgressDialog(LoginActivity.this, false);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("type", "send_login_otp");
        params.put("user_phone", mobile);
        Log.d("login", "" + Constants.LOGIN);
        apinetwork.requestWithJsonObject(Constants.LOGIN, params, vr, "send_login_otp");

    }

    private void loginWithMobile(String otp) {
        Log.d("otp>>", "" + otp);
        Utils.showProgressDialog(LoginActivity.this, false);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("type", "login_otp");
        params.put("otp", otp);
        params.put("user_phone", mobile);
        apinetwork.requestWithJsonObject(Constants.LOGIN, params, vr, "login_otp");
    }

    VolleyResponse vr = new VolleyResponse() {
        @Override
        public void onResponse(JSONObject obj) throws Exception {
            Utils.dismisProgressDialog();
        }

        @Override
        public void onResponse2(String url_type, JSONObject json) throws Exception {
            Utils.dismisProgressDialog();
            Log.d("jsonobject", json.toString());
            try {
                if (url_type.equals("send_login_otp") && json != null) {
                    boolean status = json.getBoolean("status");
                    String message = json.getString("message");
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();

                    if (status) {
                        otpDialog();
                    }
                } else if (url_type.equals("login_otp")) {
                    if (json != null) {
                        boolean status = json.getBoolean("status");
//                        String super_user = json.getString("super_user");
                        if (status) {
                            gotoNextPage();
                            saveCredentials(json);

                        } else {
                            Toast.makeText(LoginActivity.this, "" + json.getString("msg"), Toast.LENGTH_SHORT).show();

                        }

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }


    };

//    private void saveCredentials(JSONObject user_jsonObject) throws JSONException {
//        MyApplication.mSp.setKey(SPCsnstants.IS_LOGGED_IN, SPCsnstants.YES);
//        MyApplication.mSp.setKey(SPCsnstants.id, user_jsonObject.getString("user_id"));
//        MyApplication.mSp.setKey(SPCsnstants.name, user_jsonObject.getString("name"));
//        MyApplication.mSp.setKey(mobile, user_jsonObject.getString("user_phone"));
//        MyApplication.mSp.setKey(SPCsnstants.email, user_jsonObject.getString("email"));
//        MyApplication.mSp.setKey(SPCsnstants.super_user, user_jsonObject.getString("super_user"));
//    }

    private void saveCredentials(JSONObject user_jsonObject) throws JSONException {
        try {
            JSONObject userObject = user_jsonObject.getJSONObject("user");

            // Extract user details
            String userId = userObject.getString("user_id");
            String name = userObject.getString("name");
            String email = userObject.getString("email");
            String user_phone = userObject.getString("user_phone");
            // Add other user details as needed

            // Save user details using SharedPreferences or any other preferred method
            MyApplication.mSp.setKey(SPCsnstants.IS_LOGGED_IN, SPCsnstants.YES);
            MyApplication.mSp.setKey(SPCsnstants.id, userId);
            MyApplication.mSp.setKey(SPCsnstants.name, name);
            MyApplication.mSp.setKey(SPCsnstants.user_phone,user_phone );
            MyApplication.mSp.setKey(SPCsnstants.email, email);
            MyApplication.mSp.setKey(SPCsnstants.super_user, userObject.getString("super_user"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void gotoNextPage() {
        Intent intent = new Intent(LoginActivity.this, AdminMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void registerSMSReceiver() {
        IntentFilter filter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
        registerReceiver(new SMSReceiver(), filter);
    }

    public void updateUIWithOTP(String otp) {
        otpPinview.setValue(otp);
        verifyButton.performClick();
    }

    private void handleReceivedOTP() {
        Intent intent = getIntent();
        if (intent != null && ACTION_RECEIVED_OTP.equals(intent.getAction())) {
            String otp = intent.getStringExtra(EXTRA_OTP);
            updateUIWithOTP(otp);
        }
    }

}
