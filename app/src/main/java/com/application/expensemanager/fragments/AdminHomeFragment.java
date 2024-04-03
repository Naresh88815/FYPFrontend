package com.application.expensemanager.fragments;

import static com.application.expensemanager.utils.MyApplication.apinetwork;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.application.expensemanager.R;
import com.application.expensemanager.network.VolleyResponse;
import com.application.expensemanager.utils.Constants;
import com.application.expensemanager.utils.MyApplication;
import com.application.expensemanager.utils.SPCsnstants;
import com.application.expensemanager.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.datepicker.MaterialDatePicker;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class AdminHomeFragment extends Fragment {

    View view;
    TextView total_exp_money,total_pending_money,total_approved_money,total_cancel_money,total_decline_money,transfer_money;
    AppCompatButton dateFilter,createExpenseHeadBtn, addUserBtn;
    TextView filterDates;
    String head;
    String name,email,user_phone,account_no,khalti_id,user_role,super_user;


    String startDateString = "";
     String endDateString = "";
    String is_role;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.admin_home_fragment, container, false);
        initview();
        dateFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog();
            }
        });

        createExpenseHeadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreateHeadBottomSheet();
            }
        });

        addUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddUserBottomSheet();
            }
        });
        return view;
    }



    private void DatePickerDialog() {
        // Creating a MaterialDatePicker builder for selecting a date range
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Select a date range");

        // Building the date picker dialog
        MaterialDatePicker<Pair<Long, Long>> datePicker = builder.build();
        datePicker.addOnPositiveButtonClickListener(selection -> {

            // Retrieving the selected start and end dates
            Long startDate = selection.first;
            Long endDate = selection.second;

            // Formating the selected dates as strings
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
             startDateString = sdf.format(new Date(startDate));
             endDateString = sdf.format(new Date(endDate));

            // Creating the date range string
            String selectedDateRange = startDateString + " - " + endDateString;

            // Displaying the selected date range in the TextView
            filterDates.setText(selectedDateRange);
            filterDates.setVisibility(View.VISIBLE);

            FetchExpenseCount();
        });

        // Showing the date picker dialog
        datePicker.show(getParentFragmentManager(), "DATE_PICKER");

    }

    public void   initview(){
        total_exp_money = view.findViewById(R.id.total_money);
        total_approved_money = view.findViewById(R.id.approved_money);
        total_pending_money = view.findViewById(R.id.pending_money);
        total_cancel_money = view.findViewById(R.id.cancel_money);
        total_decline_money = view.findViewById(R.id.decline_money);
        transfer_money = view.findViewById(R.id.transfer_money);
        FetchExpenseCount();
        dateFilter = view.findViewById(R.id.dateFilter);
        filterDates = view.findViewById(R.id.filterDates);
        createExpenseHeadBtn=view.findViewById(R.id.createExpenseHeadBtn);
        addUserBtn= view.findViewById(R.id.AddUserBtn);
    }

    private void FetchExpenseCount() {
        Utils.showProgressDialog(getContext(),false);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("type", "count_expense");
        params.put("start_date",startDateString);
        params.put("end_date",endDateString);
        apinetwork.requestWithJsonObject(Constants.COUNT_EXPENSE, params, vr, "count_expense");
    }
    VolleyResponse vr = new VolleyResponse() {
        @Override
        public void onResponse(JSONObject obj) throws Exception {
            Utils.dismisProgressDialog();
        }

        @Override
        public void onResponse2(String url_type, JSONObject json) throws Exception {
            Utils.dismisProgressDialog();
//            Log.d("jsonobject", json.toString());
            try {
                if (url_type.equals("count_expense")) {
                    if (json != null) {
                        boolean status = json.getBoolean("status");
                        //  Toast.makeText(getContext(), "" + json.getString("msg"), Toast.LENGTH_SHORT).show();
                        if (status) {
                            JSONObject countObject = json.getJSONObject("summary");
                            total_exp_money.setText("₹ "+countObject.getString("total_request"));
                            total_approved_money.setText("₹ "+countObject.getString("total_approved"));
                            total_cancel_money.setText("₹ "+countObject.getString("total_cancel"));
                            total_pending_money.setText("₹ "+countObject.getString("total_pending"));
                            total_decline_money.setText("₹ "+countObject.getString("total_decline"));
                            transfer_money.setText("₹ "+countObject.getString("total_transfer"));
                        }
                    }
                } else if (url_type.equals("add_exp_heads")) {
                    if (json != null) {

                        boolean status = json.getBoolean("status");
                        String message = json.getString("message");
                        Toast.makeText(getContext(), "" + message, Toast.LENGTH_SHORT).show();
                        if (status) {
//                            refreshFragment();
                            Toast.makeText(getContext(), "" + message, Toast.LENGTH_SHORT).show();
                        }

                    }
                }

                else if (url_type.equals("add_user")) {
                    if (json != null) {
                        Log.d("user",json.toString());
                        boolean status = json.getBoolean("status");
                        String message = json.getString("message");
                        Toast.makeText(getContext(), "" + message, Toast.LENGTH_SHORT).show();
                        if (status) {
//                            refreshFragment();
                            Toast.makeText(getContext(), "" + message, Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    };

    private void openCreateHeadBottomSheet() {
        EditText expenseHeadTxt;
        TextView submit_btn;
        final BottomSheetDialog sheet = new BottomSheetDialog(getActivity());
        sheet.setContentView(R.layout.bottomsheet_add_head);
        expenseHeadTxt = sheet.findViewById(R.id.expenseHeadTxt);
        submit_btn = sheet.findViewById(R.id.submit_head_btn);
        sheet.show();

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                head = expenseHeadTxt.getText().toString();
                if (head.isEmpty()) {
                    expenseHeadTxt.setError("");
                } else {
                    sheet.cancel();
                    addExpenseHead();
                }

            }
        });

    }

    private void addExpenseHead() {
        Utils.showProgressDialog(getContext(), false);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("type", "add_exp_heads");
        params.put("name", head);
        apinetwork.requestWithJsonObject(Constants.ADD_HEAD, params, vr, "add_exp_heads");
    }


    private void openAddUserBottomSheet() {
        EditText userName;
        EditText user_email;
        EditText phoneNumber;
        EditText accountNo;
        EditText khaltiId;
        EditText userRole;
        EditText superUser;

        TextView submit_btn;
        final BottomSheetDialog sheet = new BottomSheetDialog(getActivity());
        sheet.setContentView(R.layout.bottomsheet_add_user);
        userName = sheet.findViewById(R.id.userName);
        user_email = sheet.findViewById(R.id.email);
        phoneNumber = sheet.findViewById(R.id.phoneNumber);
        accountNo = sheet.findViewById(R.id.accountNo);
        khaltiId = sheet.findViewById(R.id.khaltiId);
        userRole = sheet.findViewById(R.id.userRole);
        superUser = sheet.findViewById(R.id.superUser);
        submit_btn = sheet.findViewById(R.id.submit_user_btn);
        sheet.show();

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = userName.getText().toString();
                email=user_email.getText().toString();
                user_phone=phoneNumber.getText().toString();
                account_no=accountNo.getText().toString();
                khalti_id=khaltiId.getText().toString();
                user_role=userRole.getText().toString();
                super_user=superUser.getText().toString();

                if (name.isEmpty()) {
                    userName.setError("");
                } else if (email.isEmpty()) {
                    user_email.setError("");
                } else if (user_phone.isEmpty()) {
                    phoneNumber.setError("");
                } else if (account_no.isEmpty()) {
                    accountNo.setError("");
                } else if (khalti_id.isEmpty()) {
                    khaltiId.setError("");
                } else if (user_role.isEmpty()) {
                    userRole.setError("");
                } else if (super_user.isEmpty()) {
                    superUser.setError("");
                } else {
                    sheet.cancel();
                    addUser();
                }

            }
        });

    }
    private void addUser(){
        Utils.showProgressDialog(getContext(),false);
        HashMap<String, String> params = new HashMap<String,String>();
        params.put("type", "add_user");
        params.put("name",name);
        params.put("email",email);
        params.put("user_phone",user_phone);
        params.put("account_no",account_no);
        params.put("khalti_id",khalti_id);
        params.put("user_role",user_role);
        params.put("super_user", super_user);
//        params.put("login_status","");
//        params.put("last_login","");
        apinetwork.requestWithJsonObject(Constants.ADD_USER,params,vr,"add_user");
    }

//    public void refreshFragment() {
//        is_role = MyApplication.mSp.getKey(SPCsnstants.IS_screen);
//        if (is_role.equals(SPCsnstants.user)) {
//            FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
//            ft.replace(R.id.fragment_container, new HomeFragment()); // Replace with your fragment class
//            ft.addToBackStack(null);
//            ft.commit();
//        } else if (is_role.equals(SPCsnstants.Admin)) {
//            FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
//            ft.replace(R.id.admin_fragment_container, new HomeFragment()); // Replace with your fragment class
//            ft.addToBackStack(null);
//            ft.commit();
//        }
//
//    }
}