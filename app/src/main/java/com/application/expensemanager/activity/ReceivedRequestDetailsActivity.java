package com.application.expensemanager.activity;

import static com.application.expensemanager.utils.MyApplication.apinetwork;
import static com.application.expensemanager.utils.Utils.CompressImage;

import android.Manifest;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.application.expensemanager.R;
import com.application.expensemanager.adapter.OptionsAdapter;
import com.application.expensemanager.adapter.SelectedImageAdapter;
import com.application.expensemanager.model.ChooseFileOptionModel;
import com.application.expensemanager.network.VolleyResponse;
import com.application.expensemanager.utils.Constants;
import com.application.expensemanager.utils.MyApplication;
import com.application.expensemanager.utils.SPCsnstants;
import com.application.expensemanager.utils.Utils;
import com.application.expensemanager.utils.VolleyMultipartRequest;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ReceivedRequestDetailsActivity extends AppCompatActivity {
    AppCompatButton approveBtn, rejectBtn, transferBtn, cancelBtn;
    AppCompatButton uploadTransferImg, submitBtn;

    TextView requestedByTV, requestLabelTV, statusTv, amountTV, requestDate, transferdby, note, transferDate;
    Context context;
    ImageView billImg, transfer_img;
    String paid_type = "", paid_upi = "", paid_ac_owner = "", paid_ac_number = "", paid_ac_ifsc = "", company_bank_name = "", payment_type = "", account_type = "";
    String payment_status, exp_id, amount, image, label, dateStr, requestedBy, str_note, str_transferdby, str_transfer_image, transferDate_str, userNote, image_status, str_approveby, str_approve_date, rejectedDate, rejectedBy, str_reason, emp_id, str_emp_id;
    String formattedDate, t_formattedDate, app_formateDate, reject_formateDate, strnote, str_sub_headname, str_headsname, str_paymenttype, str_accounttype;
    ImageView close_icon, close_icon_t, IVPreviewImage_t;
    RelativeLayout after_transfer_image, uploadImgButton;
    RelativeLayout paymentTypeLayout, upiLayout, paymenttypeLayout, companyBankNameLayout;
    LinearLayout bankAccountDetailsLayout, prepaidDetailsLayout;
    TextView paid_typeTV, paid_upiTV, paid_ac_ownerTV, paid_ac_numberTV, paid_ac_ifscTV, payment_typeTV, account_typeTV, company_bank_nameTV;
    Boolean is_img = false;
    Boolean is_approve_btn = false;
    LinearLayout transferview;
    Bitmap bitmap = null;
    ImageView IVPreviewImage;
    private boolean imageSelected = false;
    EditText transferNote;
    TextView transferSubmitBtn, sub_headname, headname;

    ImageView backArrow;
    Uri outputFileUri;
    String IMAGE_UPLOAD = "";
    LinearLayout user_note_layout, approvedview, rejectedview;
    RelativeLayout imgview, img_gone, uploadbill;
    TextView user_note, approvedby, approve_Date, rejected_by, rejected_Date, reason, paymenttype_Tv, accounttype_Tv;
    String is_role;
    RelativeLayout rejectedByLayout;
    TextView rejectedDateTitle;
    private static final int SELECT_PDF = 001;
    private static final int SELECT_IMAGE = 002;
    private static final int SELECT_MULTIPLE_IMAGES = 003;
    private static final int CAMERA_SELECT_IMAGE = 004;
    RecyclerView imageRecyclerView, responseImagePreviewRecview;
    SelectedImageAdapter imageAdapter;
    List<Uri> selectedImagesList = new ArrayList<>();
    RelativeLayout imageRecvRelativeLayput, responseImagePreviewlayout;
    List<ChooseFileOptionModel> optionItems = new ArrayList<>();
    List<Bitmap> selectedBitmapsList = new ArrayList<>();
    List<String> imageUrlList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_received_request_detail);

        initView();
        getIntentData();
        getExpenseDetail();
        Log.d("Accounts_Details", "after response : " + paid_type + " : " + paid_upi + " : " + paid_ac_owner + " : " + paid_ac_number + " : " + paid_ac_ifsc + " : " + company_bank_name);
        // updateUIWithData();
        approveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApproveStatus("2");
//                refreshActivity();
                transferBtn.setVisibility(View.VISIBLE);

            }
        });

        rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDeclinRequestBottomSheet();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, "Request Cancelled", Toast.LENGTH_SHORT).show();
                openCancelRequestBottomSheet();
            }
        });

        transferBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTransferBottomSheet();
            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        billImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReceivedRequestDetailsActivity.this, ImgFullScreenActivity.class);
                intent.putExtra("billImage", image);
                startActivity(intent);
            }
        });
        transfer_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReceivedRequestDetailsActivity.this, ImgFullScreenActivity.class);
                intent.putExtra("transferImage", str_transfer_image);
                startActivity(intent);
            }
        });

        uploadImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                    // Request permissions
                    Dexter.withContext(getApplicationContext())
                            .withPermissions(
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .withListener(new MultiplePermissionsListener() {
                                @Override
                                public void onPermissionsChecked(MultiplePermissionsReport report) {
                                    if (report.areAllPermissionsGranted()) {
                                        chooseImage();
                                    }

                                    if (report.isAnyPermissionPermanentlyDenied()) {
                                        showSettingsDialog();
                                    }
                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                    token.continuePermissionRequest();
                                }
                            })
                            .onSameThread()
                            .check();
                } else {
                    // Handle permissions for Android 11 and above
                    Dexter.withContext(getApplicationContext())
                            .withPermissions(
                                    Manifest.permission.CAMERA)
                            .withListener(new MultiplePermissionsListener() {
                                @Override
                                public void onPermissionsChecked(MultiplePermissionsReport report) {
                                    if (report.areAllPermissionsGranted()) {
                                        chooseImage();
                                    }
                                    // check for permanent denial of any permission
                                    if (report.isAnyPermissionPermanentlyDenied()) {
                                        showSettingsDialog();
                                    }
                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                    token.continuePermissionRequest();
                                }
                            })
                            .onSameThread()
                            .check();
                }
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageSelected == false) {
                    Toast.makeText(context, "Please Select an Image", Toast.LENGTH_SHORT).show();
                } else {
                    uploadImg();
                }
            }
        });


        optionItems.add(new ChooseFileOptionModel("Gallery", R.drawable.gallery_icon));
        optionItems.add(new ChooseFileOptionModel("Camera", R.drawable.camera_icon));
        optionItems.add(new ChooseFileOptionModel("PDF", R.drawable.baseline_picture_as_pdf_24));
        imageRecyclerView.setLayoutManager(new LinearLayoutManager(ReceivedRequestDetailsActivity.this, RecyclerView.HORIZONTAL, false));
        imageAdapter = new SelectedImageAdapter(ReceivedRequestDetailsActivity.this, selectedImagesList, imageRecvRelativeLayput, uploadImgButton, imageUrlList, "response");
        imageRecyclerView.setAdapter(imageAdapter);
    }

    private void getprefences() {
        emp_id = MyApplication.mSp.getKey(SPCsnstants.id);
        Log.d("str_emp_id", emp_id);
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

                        Toast.makeText(ReceivedRequestDetailsActivity.this, "" + json.getString("msg"), Toast.LENGTH_SHORT).show();
                        if (status) {
                            getExpenseDetail();
                            str_note = "";
                        }
                    }
                } else if (url_type == "expense_detail") {
                    try {
                        if (json != null) {
                            boolean status = json.getBoolean("status");

//                            Toast.makeText(ReceivedRequestDetailsActivity.this, "" + expense, Toast.LENGTH_SHORT).show();

                            if (status) {
                                JSONObject object = json.getJSONObject("expense");
                                label = object.getString("label_name");
                                payment_status = object.getString("status");
                                amount = object.getString("amount");
                                requestedBy = object.getString("user_name");
                                dateStr = object.getString("date_added");
                                str_emp_id = "" + object.getString("emp_id");
                                userNote = "" + object.getString("note");
//                                is_img = object.getBoolean("is_status");
                                str_headsname = object.getString("heads_name");
                                str_paymenttype = object.getString("payment_type");
                                is_approve_btn = object.getBoolean("is_approve_button");

//                                payment_type = object.getString("payment_type");
//                                paid_type = object.getString("paid_type");
//                                paid_upi = object.getString("paid_upi");
//                                paid_ac_owner = object.getString("paid_ac_owner");
//                                paid_ac_number = object.getString("paid_ac_number");
//                                paid_ac_ifsc = object.getString("paid_ac_ifsc");
//                                company_bank_name = object.getString("company_bank_name");
//                                account_type = object.getString("account_type");
                                image = object.getString("image");
                                showAccountsDetails();

//                                Log.d("Accounts_Details", account_type + " : " + company_bank_name + " : " + payment_type + " : " + paid_type + " : " + paid_upi + " : " + paid_ac_owner + " : " + paid_ac_number + " : " + paid_ac_ifsc + " : " + company_bank_name);


                                if (image.equals("null")) {
                                    imgview.setVisibility(View.GONE);
                                    Log.d("image", image.toString());
                                } else {
                                    imgview.setVisibility(View.GONE);
                                    Log.d("image2", image.toString());
                                    imageUrlList.clear();
                                    String imageUrls = object.getString("image");
                                    String[] imageUrlArray = imageUrls.split(",");
                                    for (String imageUrl : imageUrlArray) {
                                        String trimmedUrl = imageUrl.trim();
                                        imageUrlList.add(trimmedUrl);
                                        Log.d("imageUrlList", "imageUrlList ITEM: "+trimmedUrl.toString());
                                    }
                                    Log.d("imageUrlList", "imageUrlList: "+imageUrlList.toString());
                                    imageRecyclerView.setLayoutManager(new LinearLayoutManager(ReceivedRequestDetailsActivity.this, RecyclerView.HORIZONTAL, false));
                                    imageAdapter = new SelectedImageAdapter(ReceivedRequestDetailsActivity.this, selectedImagesList, imageRecvRelativeLayput, uploadImgButton, imageUrlList, "response");
                                    imageRecyclerView.setAdapter(imageAdapter);
                                    imageRecvRelativeLayput.setVisibility(View.VISIBLE);
                                    imageRecyclerView.setVisibility(View.VISIBLE);
                                    if (!imageUrlList.isEmpty()) {
                                        uploadImgButton.setVisibility(View.GONE);
                                    }
                                }
                                if (is_approve_btn.equals(true)) {
                                    approveBtn.setVisibility(View.VISIBLE);
                                } else {
                                    approveBtn.setVisibility(View.GONE);
                                }


                                SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                try {
                                    Date date = inputDateFormat.parse(dateStr);
                                    SimpleDateFormat outputDateFormat = new SimpleDateFormat("h:mm a, d MMMM yyyy");
                                    formattedDate = outputDateFormat.format(date);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                if (!json.isNull("approve")) {
                                    JSONObject approveobject = json.getJSONObject("approve");
                                    approvedview.setVisibility(View.VISIBLE);
                                    str_approveby = approveobject.getString("user_name");
                                    str_approve_date = "" + approveobject.getString("date_added");
                                    try {
                                        Date t_date = inputDateFormat.parse(str_approve_date);
                                        SimpleDateFormat outputDateFormat = new SimpleDateFormat("h:mm a, d MMMM yyyy");
                                        app_formateDate = outputDateFormat.format(t_date);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
//                                    }
                                }
                                if (!json.isNull("reject")) {
                                    JSONObject rejectobject = json.getJSONObject("reject");
                                    rejectedview.setVisibility(View.VISIBLE);
                                    rejectedBy = rejectobject.getString("user_name");
                                    str_reason = rejectobject.getString("note");
                                    rejectedDate = "" + rejectobject.getString("date_added");
                                    try {
                                        Date t_date = inputDateFormat.parse(rejectedDate);
                                        SimpleDateFormat outputDateFormat = new SimpleDateFormat("h:mm a, d MMMM yyyy");
                                        reject_formateDate = outputDateFormat.format(t_date);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
//                                    }
                                }
                                if (!json.isNull("transfer")) {
                                    JSONObject transferobject = json.getJSONObject("transfer");
                                    transferview.setVisibility(View.VISIBLE);
                                    str_transferdby = transferobject.getString("user_name");
                                    str_note = transferobject.getString("note");

                                    transferDate_str = "" + transferobject.getString("date_added");
                                    str_transfer_image = transferobject.getString("image");
                                    if (str_transfer_image.equals("null")) {
                                        img_gone.setVisibility(View.GONE);
                                    } else {
                                        img_gone.setVisibility(View.VISIBLE);
                                    }

                                    Log.d("tradd", str_transferdby);
                                    try {
                                        Date t_date = inputDateFormat.parse(transferDate_str);
                                        SimpleDateFormat outputDateFormat = new SimpleDateFormat("h:mm a, d MMMM yyyy");
                                        t_formattedDate = outputDateFormat.format(t_date);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
//                                    }
                                }

                                if (!json.isNull("cancelled")) {
                                    JSONObject rejectobject = json.getJSONObject("cancelled");
                                    rejectedview.setVisibility(View.VISIBLE);
                                    rejectedByLayout.setVisibility(View.GONE);
                                    rejectedDateTitle.setText("Cancelled Date");
                                    str_reason = rejectobject.getString("note");
                                    rejectedDate = "" + rejectobject.getString("date_added");
                                    try {
                                        Date t_date = inputDateFormat.parse(rejectedDate);
                                        SimpleDateFormat outputDateFormat = new SimpleDateFormat("h:mm a, d MMMM yyyy");
                                        reject_formateDate = outputDateFormat.format(t_date);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
//                                    }
                                }

                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (payment_status.equals("Cancelled")) {
                        int redColor = ContextCompat.getColor(context, R.color.red);
                        statusTv.setTextColor(redColor);
                        rejectBtn.setVisibility(View.GONE);
                        approveBtn.setVisibility(View.GONE);
                        cancelBtn.setVisibility(View.GONE);
                    } else if (payment_status.equals("Declined")) {
                        int redColor = ContextCompat.getColor(context, R.color.red);
                        statusTv.setTextColor(redColor);
                        rejectBtn.setVisibility(View.GONE);
                        approveBtn.setVisibility(View.GONE);
                        cancelBtn.setVisibility(View.GONE);
                    } else if (payment_status.equals("Approved")) {
                        int orangeColor = ContextCompat.getColor(context, R.color.orange);
                        statusTv.setTextColor(orangeColor);
                        rejectBtn.setVisibility(View.GONE);
                        approveBtn.setVisibility(View.GONE);
                        transferBtn.setVisibility(View.VISIBLE);
                        cancelBtn.setVisibility(View.GONE);
                    } else if (payment_status.equals("Transferred")) {
                        int greenColor = ContextCompat.getColor(context, R.color.green);
                        statusTv.setTextColor(greenColor);
                        rejectBtn.setVisibility(View.GONE);
                        approveBtn.setVisibility(View.GONE);
                        transferBtn.setVisibility(View.GONE);
                        cancelBtn.setVisibility(View.GONE);

                        if (is_img.equals(true)) {
                            uploadImgButton.setVisibility(View.VISIBLE);
                        } else {
                            uploadImgButton.setVisibility(View.GONE);
                        }

                    } else if (payment_status.equals("Pending")) {
                        if (emp_id.equals(str_emp_id)) {
                            cancelBtn.setVisibility(View.VISIBLE);
                            rejectBtn.setVisibility(View.GONE);
                            transferBtn.setVisibility(View.GONE);
                            if (is_approve_btn.equals(true)) {
                                approveBtn.setVisibility(View.VISIBLE);
                            } else {
                                approveBtn.setVisibility(View.GONE);
                            }
                        }
                        int yellowColor = ContextCompat.getColor(context, R.color.dark_yellow);
                        statusTv.setTextColor(yellowColor);
                    }
                    updateUIWithData();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private void updateUIWithData() {
        requestedByTV.setText(requestedBy);
        requestLabelTV.setText(label);
        statusTv.setText(payment_status);
        sub_headname.setText(str_sub_headname);
        headname.setText(str_headsname);
        amountTV.setText("₹ " + amount);

        paymenttype_Tv.setText(str_paymenttype);
        accounttype_Tv.setText(str_accounttype);

        if (!userNote.isEmpty()) {
            user_note_layout.setVisibility(View.VISIBLE);
            user_note.setText(userNote);
        }

        approvedby.setText(str_approveby);
        rejected_by.setText(rejectedBy);
        reason.setText(str_reason);
        note.setText(str_note);
        transferdby.setText(str_transferdby);

        requestDate.setText(formattedDate);
        approve_Date.setText(app_formateDate);
        transferDate.setText(t_formattedDate);
        rejected_Date.setText(reject_formateDate);

        if (!image.equals("null")) {
            Glide.with(context)
                    .load(image)
                    .placeholder(R.drawable.img_placeholder)
                    .into(billImg);
        }

        if (!str_transfer_image.equals("null")) {
            Glide.with(context)
                    .load(str_transfer_image)
                    .placeholder(R.drawable.img_placeholder)
                    .into(transfer_img);
        }

    }


    private void getIntentData() {
        if (getIntent().hasExtra("exp_id")) {
            exp_id = getIntent().getStringExtra("exp_id");

        }
    }

    public void initView() {
        accounttype_Tv = findViewById(R.id.accounttype_Tv);
        paymenttype_Tv = findViewById(R.id.paymenttype_Tv);
        approveBtn = findViewById(R.id.approveBtn);
        rejectBtn = findViewById(R.id.rejectBtn);
        requestLabelTV = findViewById(R.id.requestLabel);
        requestedByTV = findViewById(R.id.requestedBy);
        transferdby = findViewById(R.id.transferBy);
        note = findViewById(R.id.note);
        statusTv = findViewById(R.id.status);
        transferview = findViewById(R.id.transferview);
        amountTV = findViewById(R.id.amount);
        requestDate = findViewById(R.id.date);
        transferDate = findViewById(R.id.trans_Date);
        billImg = findViewById(R.id.IVBillImg);
        transfer_img = findViewById(R.id.IVPaymentImg);
        transferBtn = findViewById(R.id.transferBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        headname = findViewById(R.id.headname);
        context = this;
        backArrow = findViewById(R.id.backArrow);
        user_note_layout = findViewById(R.id.user_note_layout);
        approvedview = findViewById(R.id.approvedview);
        rejectedview = findViewById(R.id.rejectedview);
        approvedby = findViewById(R.id.approved_by);
        approve_Date = findViewById(R.id.approve_Date);
        rejected_by = findViewById(R.id.rejected_by);
        rejected_Date = findViewById(R.id.rejected_Date);
        reason = findViewById(R.id.reason);
        user_note = findViewById(R.id.user_note);
        imgview = findViewById(R.id.imgview);
        img_gone = findViewById(R.id.img_gone);
//      uploadbill = findViewById(R.id.uploadbill);
        getprefences();
        uploadImgButton = findViewById(R.id.uploadImgButton);
        close_icon_t = findViewById(R.id.close_icon);
        IVPreviewImage_t = findViewById(R.id.IVPreviewImage);
        after_transfer_image = findViewById(R.id.previewimage);
        submitBtn = findViewById(R.id.submitBtn);

        paymentTypeLayout = findViewById(R.id.paymentTypeLayout);
        upiLayout = findViewById(R.id.upiLayout);
        bankAccountDetailsLayout = findViewById(R.id.bankAccountDetailsLayout);
        paid_typeTV = findViewById(R.id.paid_type);
        paid_upiTV = findViewById(R.id.paid_upi);
        paid_ac_ownerTV = findViewById(R.id.paid_ac_owner);
        paid_ac_numberTV = findViewById(R.id.paid_ac_number);
        paid_ac_ifscTV = findViewById(R.id.paid_ac_ifsc);
        paymenttypeLayout = findViewById(R.id.paymenttypeLayout);
        payment_typeTV = findViewById(R.id.payment_type);
        prepaidDetailsLayout = findViewById(R.id.prepaidDetailsLayout);
        account_typeTV = findViewById(R.id.account_type);
        company_bank_nameTV = findViewById(R.id.company_bank_name);
        companyBankNameLayout = findViewById(R.id.companyBankNameLayout);

        rejectedByLayout = findViewById(R.id.rejectedByLayout);
        rejectedDateTitle = findViewById(R.id.rejectedDateTitle);

        imageRecvRelativeLayput = findViewById(R.id.imageRecvRelativeLayout);
        imageRecyclerView = findViewById(R.id.imagePreviewRecyclerView);
        responseImagePreviewlayout = findViewById(R.id.responseImagePreviewlayout);
        responseImagePreviewRecview = findViewById(R.id.responseImagePreviewRecview);


        close_icon_t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bitmap = null;
                IVPreviewImage_t.setVisibility(View.GONE);
                after_transfer_image.setVisibility(View.GONE);
                close_icon_t.setVisibility(View.GONE);
                submitBtn.setVisibility(View.GONE);
                uploadImgButton.setVisibility(View.VISIBLE);
            }
        });
    }

    private void openCancelRequestBottomSheet() {
        EditText reasonET;
        TextView reason_submit_btn;
        final BottomSheetDialog sheet = new BottomSheetDialog(context);
        sheet.setContentView(R.layout.bottomsheet_cancel_reason);
        reasonET = sheet.findViewById(R.id.reasonET);
        reason_submit_btn = sheet.findViewById(R.id.reason_submit_btn);
        sheet.show();

        reason_submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                str_note = reasonET.getText().toString();
                if (str_note.isEmpty()) {
                    reasonET.setError("");
                } else {
//                    Toast.makeText(getContext(), "Expense Label Created", Toast.LENGTH_SHORT).show();

                    sheet.cancel();
                    cancelExpense();
                    finish();
                }

            }
        });

    }

    public void cancelExpense() {
        Utils.showProgressDialog(ReceivedRequestDetailsActivity.this, false);
        HashMap<String, String> params = new HashMap<>();
        params.put("type", "cancel_expense");
        params.put("emp_id", "" + MyApplication.mSp.getKey(SPCsnstants.id));
        params.put("exp_id", exp_id);
        Log.d("CanclledReason", str_note);
        params.put("note", str_note);
        apinetwork.requestWithJsonObject(Constants.EXPENSE_STATUS_UPDATE, params, vr, "cancel_expense");
    }

    public void updateExpenseStatus(String status_id) {
        Utils.showProgressDialog(ReceivedRequestDetailsActivity.this, false);
        HashMap<String, String> params = new HashMap<>();
        params.put("type", "expense_status_update");
        params.put("emp_id", "" + MyApplication.mSp.getKey(SPCsnstants.id));
        params.put("exp_id", exp_id);
        params.put("status_id", status_id);
        params.put("note", strnote);
        apinetwork.requestWithJsonObject(Constants.EXPENSE_STATUS_UPDATE, params, vr, "expense_status_update");
    }

    public void ApproveStatus(String status_id) {
        Utils.showProgressDialog(ReceivedRequestDetailsActivity.this, false);
        HashMap<String, String> params = new HashMap<>();
        params.put("type", "expense_status_update");
        params.put("emp_id", "" + MyApplication.mSp.getKey(SPCsnstants.id));
        params.put("exp_id", exp_id);
        params.put("status_id", status_id);
        apinetwork.requestWithJsonObject(Constants.EXPENSE_STATUS_UPDATE, params, vr, "expense_status_update");
    }

    public void getExpenseDetail() {
        Utils.showProgressDialog(ReceivedRequestDetailsActivity.this, false);
        HashMap<String, String> params = new HashMap<>();
        params.put("type", "expense_detail");
        params.put("exp_id", exp_id);
        apinetwork.requestWithJsonObject(Constants.EXPENSE_DETAIL, params, vr, "expense_detail");
    }

    private void openDeclinRequestBottomSheet() {
        EditText reasonET;
        TextView reason_submit_btn;
        final BottomSheetDialog sheet = new BottomSheetDialog(context);
        sheet.setContentView(R.layout.bottomsheet_cancel_reason);
        reasonET = sheet.findViewById(R.id.reasonET);
        TextView title = sheet.findViewById(R.id.title);
        reasonET.setHint("Enter The Reason To Reject Request");
        reason_submit_btn = sheet.findViewById(R.id.reason_submit_btn);
        sheet.show();

        reason_submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strnote = reasonET.getText().toString();
                if (strnote.isEmpty()) {
                    reasonET.setError("Please enter reason");
                } else {
                    sheet.cancel();
                    updateExpenseStatus("4");
                }

            }
        });

    }

    private void openTransferBottomSheet() {

        final BottomSheetDialog sheet = new BottomSheetDialog(context);
        sheet.setContentView(R.layout.bottomsheet_transfer);
        uploadTransferImg = sheet.findViewById(R.id.uploadTransferImg);
        transferNote = sheet.findViewById(R.id.transferNote);
        transferSubmitBtn = sheet.findViewById(R.id.transferSubmitBtn);
        close_icon = sheet.findViewById(R.id.close_icon);
        IVPreviewImage = sheet.findViewById(R.id.IVPreviewImage);
        sheet.show();

        uploadTransferImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                    Dexter.withActivity(ReceivedRequestDetailsActivity.this)
                            .withPermissions(
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .withListener(new MultiplePermissionsListener() {
                                @Override
                                public void onPermissionsChecked(MultiplePermissionsReport report) {
                                    if (report.areAllPermissionsGranted()) {
                                        chooseTransferImage();
                                    }

                                    if (report.isAnyPermissionPermanentlyDenied()) {

                                        showSettingsDialog();
                                    }

                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                    token.continuePermissionRequest();
                                }
                            })
                            .onSameThread()
                            .check();
                } else {
                    Dexter.withActivity(ReceivedRequestDetailsActivity.this)
                            .withPermissions(
                                    Manifest.permission.CAMERA)
                            .withListener(new MultiplePermissionsListener() {
                                @Override
                                public void onPermissionsChecked(MultiplePermissionsReport report) {
                                    if (report.areAllPermissionsGranted()) {
                                        chooseTransferImage();
                                    }
                                    // check for permanent denial of any permission
                                    if (report.isAnyPermissionPermanentlyDenied()) {
                                        showSettingsDialog();
                                    }
                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                    token.continuePermissionRequest();
                                }
                            })
                            .onSameThread()
                            .check();
                }
            }
        });

        close_icon.setOnClickListener(new View.OnClickListener() {
            //            HomeFragment homeFragment = new HomeFragment();
            @Override
            public void onClick(View v) {
                cancelImage();
            }
        });
        transferSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageSelected) {
                    transferFund();
                    sheet.cancel();
                } else {
                    Toast.makeText(ReceivedRequestDetailsActivity.this, "Please select an image for transfer", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private byte[] getPdfFileBytes(Uri pdfUri) {
        try (InputStream inputStream = ReceivedRequestDetailsActivity.this.getContentResolver().openInputStream(pdfUri)) {
            if (inputStream != null) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                }
                return byteArrayOutputStream.toByteArray();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void transferFund() {
        String note = transferNote.getText().toString();
        Log.d("note", note);

        if (Utils.isNetworkAvailable(context)) {
            Utils.showProgressDialog(context, false);
            String url = Constants.CREATE_EXPENSE;
            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                            Utils.dismisProgressDialog();
                            try {
                                JSONObject json = new JSONObject(new String(response.data));
                                if (json != null) {
                                    boolean status = json.getBoolean("status");
                                    String msg = json.getString("msg");
                                    if (status) {

                                        getExpenseDetail();
                                    }
                                    Toast.makeText(context, "" + msg, Toast.LENGTH_LONG).show();
                                }

                            } catch (Exception e) {
                                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Utils.dismisProgressDialog();
                            Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }) {


                @Override
                protected Map<String, String> getParams() throws Error {
                    Map<String, String> params = new HashMap<>();
                    params.put("type", "expense_status_update");
                    params.put("exp_id", exp_id);
                    params.put("status_id", "5");
                    params.put("emp_id", "" + MyApplication.mSp.getKey(SPCsnstants.id));
                    params.put("note", note);
                    params.put("image", amount);
                    Log.d("expense params", "" + params);
                    return params;
                }

                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    if (bitmap != null) {
                        String imagename = Constants.getcustomer_id(context) + System.currentTimeMillis();
                        params.put("image", new DataPart(imagename + ".jpg", getFileDataFromDrawable(bitmap)));
                    }
                    return params;

                }

            };
            Volley.newRequestQueue(context).add(volleyMultipartRequest);
        } else {
            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
        }

    }

    public void cancelImage() {
        IVPreviewImage.setImageResource(android.R.color.transparent);
        IVPreviewImage.setVisibility(View.GONE);
        uploadTransferImg.setVisibility(View.VISIBLE);
        imageSelected = false;
        close_icon.setVisibility(View.GONE);
    }

    private void showSettingsDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ReceivedRequestDetailsActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void uploadImg() {

        if (Utils.isNetworkAvailable(context)) {
            Utils.showProgressDialog(context, false);
            String url = Constants.EXP_EXPENSE;
            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                            Utils.dismisProgressDialog();
                            try {
                                JSONObject json = new JSONObject(new String(response.data));
                                if (json != null) {
                                    boolean status = json.getBoolean("status");
                                    String msg = json.getString("msg");
                                    if (status) {
                                        amount = "";
                                        bitmap = null;
                                        close_icon_t.setVisibility(View.GONE);
                                        submitBtn.setVisibility(View.GONE);
                                        uploadImgButton.setVisibility(View.GONE);
                                        imageRecvRelativeLayput.setVisibility(View.GONE);
                                        IVPreviewImage_t.setVisibility(View.GONE);
                                        after_transfer_image.setVisibility(View.GONE);
                                        getExpenseDetail();

                                    }
                                    Toast.makeText(context, "" + msg, Toast.LENGTH_LONG).show();
                                }

                            } catch (Exception e) {
                                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Utils.dismisProgressDialog();
                            if (error instanceof TimeoutError) {
                                Log.e("VolleyTimeoutError", "Volley timeout error occurred");
                            } else {
                                Toast.makeText(ReceivedRequestDetailsActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }) {


                @Override
                protected Map<String, String> getParams() throws Error {
                    Map<String, String> params = new HashMap<>();
                    params.put("type", "upload_img");
                    params.put("exp_id", exp_id);
                    return params;
                }

                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    Log.d("selectedImagesList", "Images: " + selectedImagesList.toString());
                    for (int i = 0; i < selectedImagesList.size(); i++) {
                        if (isPdfFile(selectedImagesList.get(i))) {
                            Uri pdfUri = selectedImagesList.get(i);
                            byte[] pdfByteArray = getPdfFileBytes(pdfUri);
                            if (pdfByteArray != null) {
                                String imagename = Constants.getcustomer_id(ReceivedRequestDetailsActivity.this) + System.currentTimeMillis() + i;
                                Log.d("selectedImagesList", "Pdf after uploding: " + pdfByteArray.toString());
                                params.put("image[" + i + "]", new DataPart(imagename + ".pdf", pdfByteArray));
                            } else {
                                Toast.makeText(ReceivedRequestDetailsActivity.this, "Failed to convert PDF file", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Bitmap bitmap1 = null;
                            try {
                                Log.d("selectedImagesList", "selectedImagesList before uploding images: " + selectedImagesList.toString() + "\n Size: " + selectedImagesList.size());
                                bitmap1 = MediaStore.Images.Media.getBitmap(ReceivedRequestDetailsActivity.this.getContentResolver(), selectedImagesList.get(i));
                                bitmap1 = CompressImage(bitmap1);
                                String imagename = Constants.getcustomer_id(ReceivedRequestDetailsActivity.this) + System.currentTimeMillis() + i;
                                params.put("image[" + i + "]", new DataPart(imagename + ".jpg", getFileDataFromDrawable(bitmap1)));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }

//                    if (bitmap != null) {
//                        String imagename = Constants.getcustomer_id(context) + System.currentTimeMillis();
//                        params.put("image", new DataPart(imagename + ".jpg", getFileDataFromDrawable(bitmap)));
//                    }
                    return params;

                }

            };
            Volley.newRequestQueue(context).add(volleyMultipartRequest);
        } else {
            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void chooseMultipleImages() {
        Intent pickImages = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        pickImages.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        pickImages.addCategory(Intent.CATEGORY_OPENABLE);
        pickImages.setType("image/*");

        startActivityForResult(pickImages, SELECT_MULTIPLE_IMAGES);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_MULTIPLE_IMAGES) {
                if (data != null) {
                    Log.d("onActivityResultData", "onActivityResultData: handleSelectedMultipleImages Method Call");
                    handleSelectedMultipleImages(data);
                }
            } else if (requestCode == CAMERA_SELECT_IMAGE) {
                if (outputFileUri != null) {
                    Log.d("onActivityResultData", "onActivityResultData: Camera Method Call");
                    handleCameraResult(outputFileUri);
                } else {
                    // Handle the case where outputFileUri is null
                    Log.e("onActivityResultData", "onActivityResultData: outputFileUri is null");
                }
            } else if (requestCode == SELECT_PDF) {
                if (data != null) {
                    // PDF result
                    Log.d("onActivityResultData", "onActivityResultData: handlePdfResult Method Call");

                    handlePdfResult(data);
                } else {
                    // Handle the case where data is null for SELECT_PDF
                    Log.e("onActivityResultData", "onActivityResultData: data is null for SELECT_PDF");
                }
            } else if (requestCode == SELECT_IMAGE) {
                if (data != null) {
                    Log.d("onActivityResultData", "onActivityResultData: handleSelectedImage1 Method Call");
                    Uri selectedImage = data.getData();
                    handleSelectedImage1(selectedImage);
                } else if (outputFileUri != null) {
                    handleSelectedImage1(outputFileUri);
                } else {
                    // Handle the case where data is null for SELECT_IMAGE
                    Log.e("onActivityResultData", "onActivityResultData: data is null for SELECT_IMAGE");
                }
            }
        }
    }

    private void handlePdfResult(Intent data) {
        if (data != null) {
            Uri selectedPdf = data.getData();
            selectedImagesList.add(selectedPdf);
            imageSelected = true;
            imageAdapter.notifyDataSetChanged();
            if (selectedImagesList.isEmpty()) {
                imageRecvRelativeLayput.setVisibility(View.GONE);
            } else {
                imageRecvRelativeLayput.setVisibility(View.VISIBLE);
                submitBtn.setVisibility(View.VISIBLE);
            }
            Log.d("afterNotify", selectedImagesList.toString());
        }
    }

    private byte[] getFileDataFromFile(File mediaFile) {
        try {
            FileInputStream fileInputStream = new FileInputStream(mediaFile);
            byte[] data = new byte[(int) mediaFile.length()];
            fileInputStream.read(data);
            fileInputStream.close();
            return data;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean isPdfFile(Uri uri) {
        ContentResolver contentResolver = ReceivedRequestDetailsActivity.this.getContentResolver();
        String type = contentResolver.getType(uri);
        Log.d("PDFType", "Detected type: " + type);
        return type != null && type.startsWith("application/pdf");
    }

    private void handleCameraResult(Uri outputFileUri) {
        Log.d("CameraResult", "outputFileUri" + outputFileUri.toString());
        if (outputFileUri != null) {
            Bitmap mainBitmap = null;
            try {
                mainBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), outputFileUri);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            bitmap = CompressImage(mainBitmap);
            selectedBitmapsList.add(bitmap);
            selectedImagesList.add(outputFileUri);
            Log.d("selectedImagesList", "selectedImagesListfromCamera: " + selectedImagesList.toString());
            imageSelected = true;
            imageAdapter.notifyDataSetChanged();
            if (selectedImagesList.isEmpty()) {
                imageRecvRelativeLayput.setVisibility(View.GONE);
            } else {
                imageRecvRelativeLayput.setVisibility(View.VISIBLE);
                submitBtn.setVisibility(View.VISIBLE);
            }
            Log.d("afterNotify", selectedImagesList.toString());
        }
    }

    private void handleSelectedMultipleImages(Intent data) {
        if (data != null) {
            ClipData clipData = data.getClipData();
            if (clipData != null) {
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    Uri selectedImage = clipData.getItemAt(i).getUri();
                    selectedImagesList.add(selectedImage);
                    Log.d("selectedImagesList", "selectedImagesList from handleSelectedMultipleImages: " + selectedImagesList.toString());
                    imageSelected = true;
                    imageAdapter.notifyDataSetChanged();
                    if (selectedImagesList.isEmpty()) {
                        imageRecvRelativeLayput.setVisibility(View.GONE);
                    } else {
                        imageRecvRelativeLayput.setVisibility(View.VISIBLE);
                        submitBtn.setVisibility(View.VISIBLE);
                    }
                    try {
                        Bitmap mainBitmap = null;
                        try {
                            mainBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), outputFileUri);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        bitmap = CompressImage(mainBitmap);
                        selectedBitmapsList.add(bitmap);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            } else {
                // Handle single image
                Uri selectedImage = data.getData();
                try {
                    selectedImagesList.add(selectedImage);
                    imageSelected = true;
                    imageAdapter.notifyDataSetChanged();
                    if (selectedImagesList.isEmpty()) {
                        imageRecvRelativeLayput.setVisibility(View.GONE);
                    } else {
                        imageRecvRelativeLayput.setVisibility(View.VISIBLE);
                        submitBtn.setVisibility(View.VISIBLE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void handleSelectedImage(Uri selectedImage) {
        try {
            Bitmap main_bitmap;

            if (selectedImage.getScheme().equals("file")) {
                String filePath = selectedImage.getPath();
                Log.d("CameraResult", "File path: " + filePath);
                main_bitmap = BitmapFactory.decodeFile(filePath);

                if (main_bitmap == null) {
                    Log.e("CameraResult", "Failed to decode file: " + filePath);
                } else {
                    Log.d("CameraResult", "main_bitmap: " + main_bitmap);
                }
            } else {
                main_bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                Log.d("CameraResult", "main_bitmap: " + main_bitmap.toString());
            }
            bitmap = CompressImage(main_bitmap);
            IVPreviewImage.setImageBitmap(bitmap);
            selectedImagesList.add(selectedImage);
            Log.d("selectedImagesList", "selectedImagesListFromHandle: " + selectedImagesList.toString());

            imageSelected = true;
            imageAdapter.notifyDataSetChanged();

            if (selectedImagesList.isEmpty()) {
                imageRecvRelativeLayput.setVisibility(View.GONE);
            } else {
                imageRecvRelativeLayput.setVisibility(View.VISIBLE);
            }

            Log.d("afterNotify", selectedImagesList.toString());
            IVPreviewImage.setTag(selectedImage.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleSelectedImage1(Uri selectedImage) {
        if (selectedImage != null) {
            try {
                Bitmap main_bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), selectedImage);
                bitmap = CompressImage(main_bitmap);
                IVPreviewImage.setImageBitmap(bitmap);
                imageSelected = true;
                IVPreviewImage.setVisibility(View.VISIBLE);
                close_icon.setVisibility(View.VISIBLE);
                uploadImgButton.setVisibility(View.GONE);
                IVPreviewImage.setTag(selectedImage.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (outputFileUri != null) {
            // Use outputFileUri to get the image
            try {
                Bitmap main_bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), outputFileUri);
                bitmap = CompressImage(main_bitmap);
                IVPreviewImage.setImageBitmap(bitmap);
                imageSelected = true;
                IVPreviewImage.setVisibility(View.VISIBLE);
                close_icon.setVisibility(View.VISIBLE);
                uploadImgButton.setVisibility(View.GONE);
                IVPreviewImage.setTag(outputFileUri.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Handle the case where both selectedImage and outputFileUri are null
            Log.e("handleSelectedImage1", "Both selectedImage and outputFileUri are null");
        }
    }

    private void chooseImage() {
        imageRecyclerView.setLayoutManager(new LinearLayoutManager(ReceivedRequestDetailsActivity.this, RecyclerView.HORIZONTAL, false));
        imageAdapter = new SelectedImageAdapter(ReceivedRequestDetailsActivity.this, selectedImagesList, imageRecvRelativeLayput, uploadImgButton, imageUrlList, "storage");
        imageRecyclerView.setAdapter(imageAdapter);
        showBottomSheetDialog();
    }

    private void chooseTransferImage() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = timeStamp + ".jpg";

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = new File(storageDir, imageFileName);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            outputFileUri = FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()),
                    "com.application.expensemanager" + ".provider", imageFile);

        } else {
            outputFileUri = Uri.fromFile(imageFile);
        }
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent takePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePhoto.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        Intent chooser = Intent.createChooser(pickPhoto, "Select Image");
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePhoto});

        startActivityForResult(chooser, SELECT_IMAGE);
    }

    private void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = LayoutInflater.from(this).inflate(R.layout.file_option_item_layout, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        RecyclerView recyclerViewOptions = bottomSheetView.findViewById(R.id.recyclerViewOptions);
        Log.d("BottomSheetDialog", "Option Items size: " + optionItems.size());

        // Check if recyclerViewOptions is not null before accessing it
        if (recyclerViewOptions != null) {
            OptionsAdapter optionsAdapter = new OptionsAdapter(optionItems, new OptionsAdapter.OnOptionClickListener() {
                @Override
                public void onOptionClick(ChooseFileOptionModel optionItems) {
                    // Handle option click
                    handleOptionClick(optionItems);
                    bottomSheetDialog.dismiss();
                }
            });

            recyclerViewOptions.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
            recyclerViewOptions.setAdapter(optionsAdapter);
        } else {
            // Log an error or handle the case where recyclerViewOptions is null
            Log.e("BottomSheetDialog", "recyclerViewOptions is null");
        }

        bottomSheetDialog.show();
    }

    private void handleOptionClick(ChooseFileOptionModel optionItem) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = timeStamp + ".jpg";

        File storageDir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = new File(storageDir, imageFileName);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            outputFileUri = FileProvider.getUriForFile(this,
                    "com.application.expensemanager" + ".provider", imageFile);
        } else {
            outputFileUri = Uri.fromFile(imageFile);
        }

        switch (optionItem.getText()) {
            case "Gallery":
                // Handle Gallery option
                Intent pickImages = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                pickImages.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                pickImages.addCategory(Intent.CATEGORY_OPENABLE);
                pickImages.setType("image/*");
                startActivityForResult(pickImages, SELECT_MULTIPLE_IMAGES);
                Log.d("CameraResult", "pickImages" + pickImages.toString());
                break;
            case "Camera":
                // Handle Camera option
                Intent takePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePhoto.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                startActivityForResult(takePhoto, CAMERA_SELECT_IMAGE);
                Log.d("CameraResult", "takePhoto" + takePhoto.toString());
                break;
            case "PDF":
                // Handle PDF option
                Intent pickPdf = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                pickPdf.addCategory(Intent.CATEGORY_OPENABLE);
                pickPdf.setType("application/pdf");
                startActivityForResult(pickPdf, SELECT_PDF);
                Log.d("CameraResult", "pickPdf" + pickPdf.toString());
                break;
        }
    }

    private void showAccountsDetails() {
        if (!payment_type.equals("") && payment_type != null) {
            paymenttypeLayout.setVisibility(View.VISIBLE);
            payment_typeTV.setText(payment_type);
        } else {
            paymenttypeLayout.setVisibility(View.VISIBLE);
        }

        if (!paid_type.equals("") && paid_type != null) {
            paymentTypeLayout.setVisibility(View.VISIBLE);
            paid_typeTV.setText(paid_type);
        } else {
            paymentTypeLayout.setVisibility(View.GONE);
        }

        if (!paid_upi.equals("") && paid_upi != null) {
            upiLayout.setVisibility(View.VISIBLE);
            paid_upiTV.setText(paid_upi);
        } else {
            upiLayout.setVisibility(View.GONE);
        }

        if (!paid_ac_owner.equals("") && !paid_ac_number.equals("") && !paid_ac_ifsc.equals("") && paid_ac_owner != null && paid_ac_number != null && paid_ac_ifsc != null) {
            bankAccountDetailsLayout.setVisibility(View.VISIBLE);
            paid_ac_ownerTV.setText(paid_ac_owner);
            paid_ac_numberTV.setText(paid_ac_number);
            paid_ac_ifscTV.setText(paid_ac_ifsc);
        } else {
            bankAccountDetailsLayout.setVisibility(View.GONE);
        }
        if (!payment_type.equals("")) {
            if (!payment_type.equals("")) {
                paymenttypeLayout.setVisibility(View.VISIBLE);
                payment_typeTV.setText(payment_type);
            } else {
                paymenttypeLayout.setVisibility(View.VISIBLE);
            }
            if (!account_type.equals("") && !company_bank_name.equals("")) {
                prepaidDetailsLayout.setVisibility(View.VISIBLE);
                companyBankNameLayout.setVisibility(View.VISIBLE);
                account_typeTV.setText(account_type);
                company_bank_nameTV.setText(company_bank_name);
            } else {
                prepaidDetailsLayout.setVisibility(View.GONE);
            }
            if (!account_type.equals("")) {
                prepaidDetailsLayout.setVisibility(View.VISIBLE);
                account_typeTV.setText(account_type);
                if (company_bank_name.equals(""))
                    companyBankNameLayout.setVisibility(View.GONE);
            } else {
                prepaidDetailsLayout.setVisibility(View.GONE);
            }
        }

    }


}
