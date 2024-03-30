//package com.application.expensemanager.fragments;
//
//import static com.application.expensemanager.utils.MyApplication.apinetwork;
//
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.AppCompatButton;
//import androidx.fragment.app.Fragment;
//
//import com.application.expensemanager.R;
//import com.application.expensemanager.activity.LoginActivity;
//import com.application.expensemanager.network.VolleyResponse;
//import com.application.expensemanager.utils.Constants;
//import com.application.expensemanager.utils.MyApplication;
//import com.application.expensemanager.utils.SPCsnstants;
//import com.application.expensemanager.utils.Utils;
//import com.google.android.material.bottomsheet.BottomSheetDialog;
//
//import org.json.JSONObject;
//
//import java.util.HashMap;
//
//public class AdminUserProfile extends Fragment {
//    View view;
//
//    ImageView editUPI;
//    AppCompatButton logoutUserBtn;
//    TextView name, email, phone;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        view = inflater.inflate(R.layout.admin_profile, container, false);
//        initView();
//        getProfileData();
//        editUPI.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openUpdateUPIBottomSheet();
//            }
//        });
//
//        logoutUserBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//
//                // Set the dialog title and message
//                builder.setTitle("Confirm Logout")
//                        .setMessage("Are you sure you want to logout?");
//
//                // Add buttons to the dialog
//                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        logoutAndRemoveCredentials();
//                        dialog.dismiss();
//                    }
//                });
//
//                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // Handle the "No" button click (do nothing and dismiss the dialog)
//                        dialog.dismiss(); // Dismiss the dialog
//                    }
//                });
//
//                // Create and show the AlertDialog
//                AlertDialog dialog = builder.create();
//                dialog.show();
//            }
//        });
//
//        return view;
//    }
//
//    public void initView(){
//        editUPI = view.findViewById(R.id.editUPI);
//        logoutUserBtn = view.findViewById(R.id.logoutUserBtn);
//        name = view.findViewById(R.id.name);
//        email = view.findViewById(R.id.email);
//        phone = view.findViewById(R.id.phone);
//    }
//
//    private void openUpdateUPIBottomSheet() {
//        EditText updateUPIET;
//        TextView UPISubmitBtn;
//        final BottomSheetDialog sheet = new BottomSheetDialog(getActivity());
//        sheet.setContentView(R.layout.bottomsheet_edit_upi);
//        updateUPIET = sheet.findViewById(R.id.updateUPIET);
//        UPISubmitBtn = sheet.findViewById(R.id.UPISubmitBtn);
//        sheet.show();
//
//
//        UPISubmitBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String label = updateUPIET.getText().toString();
//
//                if (label.isEmpty()){
//                    updateUPIET.setError("");
//                }
//                else {
//                    Toast.makeText(getContext(), "UPI code updated", Toast.LENGTH_SHORT).show();
//                    sheet.cancel();
//                }
//
//            }
//        });
//
//    }
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
//                        String user_name = json.getString("name");
//                        String user_email = json.getString("email");
//                        String user_phone = json.getString("user_phone");
////                        Toast.makeText(getContext(), "" + json.getString("msg"), Toast.LENGTH_SHORT).show();
//                        if (status) {
//                            name.setText(user_name);
//                            email.setText(user_email);
//                            phone.setText(user_phone);
//                        }
//
//                    }
//                    else {
//                        Toast.makeText(getContext(), "Error Getting Data", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                // Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        }
//
//    };
//
//    private void logoutAndRemoveCredentials() {
//        MyApplication.mSp.setKey(SPCsnstants.IS_LOGGED_IN, SPCsnstants.NO);
//        MyApplication.mSp.setKey(SPCsnstants.id, "");
//        MyApplication.mSp.setKey(SPCsnstants.name, "");
//        MyApplication.mSp.setKey(SPCsnstants.mobile, "");
//        MyApplication.mSp.setKey(SPCsnstants.email, "");
//        MyApplication.mSp.setKey(SPCsnstants.profile_img, "");
//        Intent intent = new Intent(getActivity(), LoginActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//    }
//
//    private void getProfileData(){
//        Utils.showProgressDialog(getActivity(),false);
//        HashMap<String, String> params = new HashMap<>();
//        params.put("type", "viewprofile");
//        apinetwork.requestWithJsonObject(Constants.VIEW_PROFILE, params, vr, "viewprofile");
//    }
//}
