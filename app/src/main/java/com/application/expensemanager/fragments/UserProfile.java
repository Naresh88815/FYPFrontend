package com.application.expensemanager.fragments;

import static com.application.expensemanager.utils.MyApplication.apinetwork;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.application.expensemanager.R;
import com.application.expensemanager.activity.LoginActivity;
import com.application.expensemanager.network.VolleyResponse;
import com.application.expensemanager.utils.Constants;
import com.application.expensemanager.utils.MyApplication;
import com.application.expensemanager.utils.SPCsnstants;
import com.application.expensemanager.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONObject;

import java.util.HashMap;

public class UserProfile extends Fragment {
    View view;

    String userUpiID, userAccountNo, userIfscCode;
    ImageView editUPI, editBankDetails;
    AppCompatButton logoutUserBtn, addUpiBtn, addBankDetailsBtn;
    TextView name, email, phone, upiId, accountNo, ifscCode;
    LinearLayout userBankDetailsLayout;
    RelativeLayout userUpiIdLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.user_profile, container, false);
        initView();
        getProfileData();

        addUpiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUpdateUPIBottomSheet();
            }
        });

        addBankDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUpdateBankDetailsBottomSheet();
            }
        });

        editUPI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUpdateUPIBottomSheet();
            }
        });

        editBankDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUpdateBankDetailsBottomSheet();
            }
        });

        logoutUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                // Set the dialog title and message
                builder.setTitle("Confirm Logout")
                        .setMessage("Are you sure you want to logout?");

                // Add buttons to the dialog
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logoutAndRemoveCredentials();
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle the "No" button click (do nothing and dismiss the dialog)
                        dialog.dismiss(); // Dismiss the dialog
                    }
                });

                // Create and show the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        return view;
    }

    private void getProfileData() {
        name.setText(MyApplication.mSp.getKey(SPCsnstants.name));
        email.setText(MyApplication.mSp.getKey(SPCsnstants.email));
        phone.setText(MyApplication.mSp.getKey(SPCsnstants.user_phone));
    }

    public void initView() {
        editUPI = view.findViewById(R.id.editUPI);
        editBankDetails = view.findViewById(R.id.editBankDetails);
        logoutUserBtn = view.findViewById(R.id.logoutUserBtn);
        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);
        phone = view.findViewById(R.id.phone);
        upiId = view.findViewById(R.id.userUPIID);
        accountNo = view.findViewById(R.id.userAccountNo);
        ifscCode = view.findViewById(R.id.userIfscCode);
        addUpiBtn = view.findViewById(R.id.userUpiAddBtn);
        addBankDetailsBtn = view.findViewById(R.id.userBankDetailsAddBtn);
        userUpiIdLayout = view.findViewById(R.id.userUpiIdLayout);
        userBankDetailsLayout = view.findViewById(R.id.userBankDetailsLayout);
    }

    private void openUpdateUPIBottomSheet() {
        EditText updateUPIET;
        TextView UPISubmitBtn;
        final BottomSheetDialog sheet = new BottomSheetDialog(getActivity());
        sheet.setContentView(R.layout.bottomsheet_edit_upi);
        updateUPIET = sheet.findViewById(R.id.updateUPIET);
        UPISubmitBtn = sheet.findViewById(R.id.UPISubmitBtn);

        if (userUpiID != null && !userUpiID.equals("null") && !userUpiID.isEmpty()) {
            updateUPIET.setText(userUpiID);
        } else {
            updateUPIET.setText("");
            updateUPIET.setHint("Enter UPI ID");
        }
        sheet.show();

        UPISubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userUpiID = updateUPIET.getText().toString();
                if (!Utils.isUpiIdValid(userUpiID)) {
                    updateUPIET.setError("Please Enter Valid UPI");
                } else if (userUpiID.isEmpty()) {
                    updateUPIET.setError("This field is required");
                    updateUPIET.requestFocus();
                } else {
//                    setUpiID();
                    sheet.cancel();
                }
            }
        });
    }

    private void openUpdateBankDetailsBottomSheet() {
        EditText updateAccountNoET, updateIfscCodeET;
        TextView bankDetailsSubmitBtn;
        final BottomSheetDialog sheet = new BottomSheetDialog(getActivity());
        sheet.setContentView(R.layout.bottomsheet_edit_bank_details);
        updateAccountNoET = sheet.findViewById(R.id.updateAccountNoET);
        updateIfscCodeET = sheet.findViewById(R.id.updateIfscCodeET);
        bankDetailsSubmitBtn = sheet.findViewById(R.id.bankDetailsSubmitBtn);
        assert updateAccountNoET != null;
        updateAccountNoET.setInputType(InputType.TYPE_CLASS_NUMBER);
        assert updateIfscCodeET != null;
        updateIfscCodeET.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);

        if (userAccountNo != null && !userAccountNo.equals("null") && !userAccountNo.isEmpty() &&
                userIfscCode != null && !userIfscCode.equals("null") && !userIfscCode.isEmpty()) {
            updateAccountNoET.setText(userAccountNo);
            updateIfscCodeET.setText(userIfscCode);
        } else {
            updateAccountNoET.setText("");
            updateAccountNoET.setHint("Enter Account Number");
            updateIfscCodeET.setText("");
            updateIfscCodeET.setHint("Enter IFSC Code");
        }


        sheet.show();

        bankDetailsSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userAccountNo = updateAccountNoET.getText().toString();
                userIfscCode = updateIfscCodeET.getText().toString();

                if (userAccountNo.isEmpty()) {
                    updateAccountNoET.setError("This field is required");
                    updateAccountNoET.requestFocus();
                } else if (userIfscCode.isEmpty()) {
                    updateIfscCodeET.setError("This field is required");
                    updateIfscCodeET.requestFocus();
                } else if (!Utils.isValidAccountNumber(userAccountNo)) {
                    updateAccountNoET.setError("Please Enter Valid Account Number");
                    updateAccountNoET.requestFocus();
                } else if (!Utils.isValidIFSCCode(userIfscCode)) {
                    updateIfscCodeET.setError("Please Enter Valid IFSC Code");
                    updateIfscCodeET.requestFocus();
                } else {
//                    setBankDetails();
                    sheet.cancel();
                }
            }
        });
    }


//    VolleyResponse vr = new VolleyResponse() {
//        @Override
//        public void onResponse(JSONObject obj) throws Exception {
//            Utils.dismisProgressDialog();
//        }
//
//        @Override
//        public void onResponse2(String url_type, JSONObject json) throws Exception {
//            Utils.dismisProgressDialog();
//            Log.d("jsonobject", json.toString());
//            try {
//                if (url_type.equals("viewprofile")) {
//                    if (json != null) {
//                        boolean status = json.getBoolean("status");
//                        Log.d("viewprofile_response", json.toString());
//                        String user_name = json.getString("name");
//                        String user_email = json.getString("email");
//                        String user_phone = json.getString("user_phone");
//                        String user_upi_id = json.getString("upi_id");
//                        String user_account_number = json.getString("account_no");
//                        String user_ifsc_code = json.getString("ifsc_code");
////                      Toast.makeText(getContext(), "" + json.getString("msg"), Toast.LENGTH_SHORT).show();
//                        if (status) {
//                            name.setText(user_name);
//                            email.setText(user_email);
//                            phone.setText(user_phone);
//                            upiId.setText(user_upi_id);
//                            accountNo.setText(user_account_number);
//                            ifscCode.setText(user_ifsc_code);
//
//                            userUpiID = user_upi_id;
//                            userAccountNo = user_account_number;
//                            userIfscCode = user_ifsc_code;
//                        }
//
//                        //Check UPI ID and Bank Details is null or not
//                        if (user_upi_id.isEmpty() || user_upi_id.equals("null")){
//                            userUpiIdLayout.setVisibility(View.GONE);
//                            addUpiBtn.setVisibility(View.VISIBLE);
//                        } else {
//                            userUpiIdLayout.setVisibility(View.VISIBLE);
//                            addUpiBtn.setVisibility(View.GONE);
//                        } if (user_account_number.isEmpty() || user_account_number.equals("null") || user_ifsc_code.isEmpty() || user_ifsc_code.equals("null")){
//                            userBankDetailsLayout.setVisibility(View.GONE);
//                            addBankDetailsBtn.setVisibility(View.VISIBLE);
//                        } else {
//                            userBankDetailsLayout.setVisibility(View.VISIBLE);
//                            addBankDetailsBtn.setVisibility(View.GONE);
//                        }
//
//                    } else {
//                        Toast.makeText(getContext(), "Error Getting Data", Toast.LENGTH_SHORT).show();
//                    }
//                } else if (url_type.equals("update")) {
//                    Log.d("update_response", json.toString());
//                    boolean status = json.getBoolean("status");
//                    if (status) {
//                        getProfileData();
//                        Toast.makeText(getContext(), "Update successful", Toast.LENGTH_SHORT).show();
//                    } else {
//                        String errorMessage = json.getString("msg");
//                        Toast.makeText(getContext(), "Update failed: " + errorMessage, Toast.LENGTH_SHORT).show();
//                    }
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                // Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        }
//
//    };

    private void logoutAndRemoveCredentials() {
        MyApplication.mSp.setKey(SPCsnstants.IS_LOGGED_IN, SPCsnstants.NO);
        MyApplication.mSp.setKey(SPCsnstants.id, "");
        MyApplication.mSp.setKey(SPCsnstants.name, "");
        MyApplication.mSp.setKey(SPCsnstants.mobile, "");
        MyApplication.mSp.setKey(SPCsnstants.email, "");
        MyApplication.mSp.setKey(SPCsnstants.super_user, "");

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("type", "logout");
        apinetwork.requestWithJsonObject(Constants.LOGOUT, params, vr, "logout");

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);


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
                if (url_type.equals("logout") && json != null) {
                    boolean status = json.getBoolean("status");
                    String message = json.getString("message");
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getContext(), "" + json.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    };


//    private void getProfileData(){
//        Utils.showProgressDialog(getActivity(),false);
//        HashMap<String, String> params = new HashMap<>();
//        params.put("type", "viewprofile");
//        apinetwork.requestWithJsonObject(Constants.VIEW_PROFILE, params, vr, "viewprofile");
//    }

//    private void setUpiID(){
//        Utils.showProgressDialog(getActivity(),false);
//        HashMap<String, String> params = new HashMap<>();
//        params.put("type", "update");
//        params.put("upi_id", userUpiID);
//        apinetwork.requestWithJsonObject(Constants.UPDATE_BANK_DETAILS, params, vr, "update");
//    }
//
//    private void setBankDetails(){
//        Utils.showProgressDialog(getActivity(),false);
//        HashMap<String, String> params = new HashMap<>();
//        params.put("type", "update");
//        params.put("account_no", userAccountNo);
//        params.put("ifsc_code", userIfscCode);
//        apinetwork.requestWithJsonObject(Constants.UPDATE_BANK_DETAILS, params, vr, "update");
//    }

}
