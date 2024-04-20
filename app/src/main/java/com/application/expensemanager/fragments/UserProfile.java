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

    String newKhaltiId,userKhaltiId, newUserAccountNo,userAccountNo, userIfscCode;
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

    public void initView() {
        editUPI = view.findViewById(R.id.editUPI);
        editBankDetails = view.findViewById(R.id.editBankDetails);
        logoutUserBtn = view.findViewById(R.id.logoutUserBtn);
        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);
        phone = view.findViewById(R.id.phone);
        upiId = view.findViewById(R.id.userUPIID);
        accountNo = view.findViewById(R.id.userAccountNo);
        addUpiBtn = view.findViewById(R.id.userKhaltiAddBtn);
        addBankDetailsBtn = view.findViewById(R.id.userBankDetailsAddBtn);
        userUpiIdLayout = view.findViewById(R.id.userUpiIdLayout);
        userBankDetailsLayout = view.findViewById(R.id.userBankDetailsLayout);
    }

    private void openUpdateUPIBottomSheet() {
        EditText updateUPIET;
        TextView UPISubmitBtn;
        final BottomSheetDialog sheet = new BottomSheetDialog(getActivity());
        sheet.setContentView(R.layout.bottomsheet_edit_khalti);
        updateUPIET = sheet.findViewById(R.id.updateUPIET);
        UPISubmitBtn = sheet.findViewById(R.id.UPISubmitBtn);

        if (userKhaltiId != null && !userKhaltiId.equals("null") && !userKhaltiId.isEmpty()) {
            updateUPIET.setText(userKhaltiId);
        } else {
            updateUPIET.setText("");
            updateUPIET.setHint("Enter UPI ID");
        }
        sheet.show();

        UPISubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newKhaltiId = updateUPIET.getText().toString();
                if (newKhaltiId.isEmpty()) {
                    updateUPIET.setError("This field is required");
                    updateUPIET.requestFocus();
                } else {
                    updateKhaltiId();
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
        bankDetailsSubmitBtn = sheet.findViewById(R.id.bankDetailsSubmitBtn);
        assert updateAccountNoET != null;
        updateAccountNoET.setInputType(InputType.TYPE_CLASS_NUMBER);

        if (userAccountNo != null && !userAccountNo.equals("null") && !userAccountNo.isEmpty()) {
            updateAccountNoET.setText(userAccountNo);
        } else {
            updateAccountNoET.setText("");
            updateAccountNoET.setHint("Enter Account Number");
        }


        sheet.show();

        bankDetailsSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newUserAccountNo = updateAccountNoET.getText().toString();
//                userIfscCode = updateIfscCodeET.getText().toString();

                if (newUserAccountNo.isEmpty()) {
                    updateAccountNoET.setError("This field is required");
                    updateAccountNoET.requestFocus();
                }
                else {
                    updateBankDetails();
                    sheet.cancel();
                }
            }
        });
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
                if (url_type.equals("viewprofile")) {
                    if (json != null) {
                        boolean status = json.getBoolean("status");
                        if (status) {
                            JSONObject userObject = json.getJSONObject("user");
                            String user_name = userObject.getString("name");
                            String user_email = userObject.getString("email");
                            String user_phone = userObject.getString("user_phone");
                            String user_khalti_id = userObject.getString("khalti_id");
                            String user_account_number = userObject.getString("account_no");
                            name.setText(user_name);
                            email.setText(user_email);
                            phone.setText(user_phone);
                            upiId.setText(user_khalti_id);
                            accountNo.setText(user_account_number);

                            userKhaltiId = user_khalti_id;
                            userAccountNo = user_account_number;

                            //Check UPI ID and Bank Details is null or not
                            if (user_khalti_id.isEmpty() || user_khalti_id.equals("null")) {
                                userUpiIdLayout.setVisibility(View.GONE);
                                addUpiBtn.setVisibility(View.VISIBLE);
                            } else {
                                userUpiIdLayout.setVisibility(View.VISIBLE);
                                addUpiBtn.setVisibility(View.GONE);
                            }
                            if (user_account_number.isEmpty() || user_account_number.equals("null")) {
                                userBankDetailsLayout.setVisibility(View.GONE);
                            addBankDetailsBtn.setVisibility(View.VISIBLE);
                            } else {
                                userBankDetailsLayout.setVisibility(View.VISIBLE);
                            addBankDetailsBtn.setVisibility(View.GONE);
                            }

                        } else {
                            Toast.makeText(getContext(), "Error Getting Data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else if (url_type.equals("update")) {
                    Log.d("update_response", json.toString());
                    boolean status = json.getBoolean("status");
                    if (status) {
                        getProfileData();
                        Toast.makeText(getContext(), "Update successful", Toast.LENGTH_SHORT).show();
                    } else {
                        String errorMessage = json.getString("msg");
                        Toast.makeText(getContext(), "Update failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    };

    private void logoutAndRemoveCredentials() {
        MyApplication.mSp.setKey(SPCsnstants.IS_LOGGED_IN, SPCsnstants.NO);
        MyApplication.mSp.setKey(SPCsnstants.id, "");
        MyApplication.mSp.setKey(SPCsnstants.name, "");
        MyApplication.mSp.setKey(SPCsnstants.mobile, "");
        MyApplication.mSp.setKey(SPCsnstants.email, "");
        MyApplication.mSp.setKey(SPCsnstants.super_user, "");

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("type", "logout");
        apinetwork.requestWithJsonObject(Constants.LOGOUT, params, vr1, "logout");

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);


    }

    VolleyResponse vr1 = new VolleyResponse() {
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


    private void getProfileData(){
        Utils.showProgressDialog(getActivity(),false);
        HashMap<String, String> params = new HashMap<>();
        params.put("type", "viewprofile");
        params.put("emp_id",MyApplication.mSp.getKey(SPCsnstants.id));
        apinetwork.requestWithJsonObject(Constants.VIEW_PROFILE, params, vr, "viewprofile");
    }

    private void updateKhaltiId() {
        Utils.showProgressDialog(getActivity(), false);
        HashMap<String, String> params = new HashMap<>();
        params.put("type", "update");
        params.put("new_khalti_id", newKhaltiId);
        params.put("emp_id", MyApplication.mSp.getKey(SPCsnstants.id));
        apinetwork.requestWithJsonObject(Constants.UPDATE_KHALTI_ID, params, vr, "update");
    }

    private void updateBankDetails(){
        Utils.showProgressDialog(getActivity(),false);
        HashMap<String, String> params = new HashMap<>();
        params.put("type", "update");
        params.put("new_account_no", newUserAccountNo);
        params.put("emp_id", MyApplication.mSp.getKey(SPCsnstants.id));
        apinetwork.requestWithJsonObject(Constants.UPDATE_BANK_DETAILS, params, vr, "update");
    }

}
