package com.application.expensemanager.activity;

import static com.application.expensemanager.utils.MyApplication.apinetwork;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.application.expensemanager.R;
import com.application.expensemanager.network.VolleyResponse;
import com.application.expensemanager.utils.Constants;
import com.application.expensemanager.utils.MyApplication;
import com.application.expensemanager.utils.SPCsnstants;
import com.application.expensemanager.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class AdminFinalApprove extends AppCompatActivity {

    AppCompatButton submitBtnFinal;
    String exp_id;
    EditText transferNote;
    String note;
    AppCompatButton uploadImgBtn;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.approve_request_final);

        initView();
        getIntentData();

        submitBtnFinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateExpenseStatus("5");
//                Toast.makeText(AdminFinalApprove.this, "Submitted", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        note = transferNote.getText().toString();

    }

    public void initView(){
        submitBtnFinal = findViewById(R.id.submitBtnFinal);
        transferNote = findViewById(R.id.transferNote);
        uploadImgBtn = findViewById(R.id.uploadImgBtn);
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
                if (url_type.equals("expense_status_update")) {
                    if (json != null) {
                        boolean status = json.getBoolean("status");

                        Toast.makeText(AdminFinalApprove.this, "" + json.getString("msg"), Toast.LENGTH_SHORT).show();
                        if (status) {

                        }
                    }
                }
            }

            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


    public void updateExpenseStatus(String status_id) {
        Utils.showProgressDialog(AdminFinalApprove.this, false);
        HashMap<String, String> params = new HashMap<>();
        params.put("type", "expense_status_update");
        params.put("emp_id",""+ MyApplication.mSp.getKey(SPCsnstants.id));
        params.put("exp_id", exp_id);
        params.put("status_id",status_id);
        apinetwork.requestWithJsonObject(Constants.EXPENSE_STATUS_UPDATE, params, vr, "expense_status_update");
    }

    private void getIntentData(){
        if (getIntent().hasExtra("exp_id")){
            exp_id = getIntent().getStringExtra("exp_id");

        }
    }
}


