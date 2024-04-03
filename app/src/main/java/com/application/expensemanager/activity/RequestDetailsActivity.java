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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class RequestDetailsActivity extends AppCompatActivity {
    TextView requestLabelTV, statusTv, amountTV, paymenttype_Tv, requestDate, transferDate, transferdby, transferNote, user_note,headname;
    String  str_headsname, str_paymenttype;
    Context context;
    ImageView billImg, paymentImg;
    AppCompatButton cancel_btn, submitBtn;
    RelativeLayout uploadImgButton, after_transfer_image;
    String image_status;
    String payment_status, exp_id, amount, image, label, userNote, dateStr, formattedDate, note, str_transferdby, str_note, str_transfer_image, transferDate_str, t_formattedDate, str_approveby, str_approve_date, app_formateDate, rejectedDate, rejectedBy, str_reason, reject_formateDate;
    ImageView backArrow, transfer_img;
    RelativeLayout imgview;
    RelativeLayout rejectedByLayout;
    TextView rejectedDateTitle;
    LinearLayout transferview, user_note_layout, approvedview, rejectedview;
    TextView approvedby, approve_Date, rejected_by, rejected_Date, reason;
    Uri outputFileUri;
    Bitmap bitmap = null;
    private boolean imageSelected = false;
    ImageView close_icon;
    ImageView IVPreviewImage;
    String is_role;
//    Boolean is_img = false;
    String paid_type = "", paid_upi = "", paid_ac_owner = "", paid_ac_number = "", paid_ac_ifsc = "", company_bank_name = "", payment_type = "", account_type = "";
    RelativeLayout paymentTypeLayout, upiLayout, paymenttypeLayout, companyBankNameLayout;
    LinearLayout bankAccountDetailsLayout, prepaidDetailsLayout;
    TextView paid_typeTV, paid_upiTV, paid_ac_ownerTV, paid_ac_numberTV, paid_ac_ifscTV, payment_typeTV, account_typeTV, company_bank_nameTV;

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
        setContentView(R.layout.user_request_detail);
        initView();
        getIntentData();
        getExpenseDetail();
        Log.d("check","This is request details page");
//        Log.d("Accounts_Details", paid_type + " : " + paid_upi + " : " + paid_ac_owner + " : " + paid_ac_number + " : " + paid_ac_ifsc+ " : " +company_bank_name);


        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, "Request Cancelled", Toast.LENGTH_SHORT).show();
                openCancelRequestBottomSheet();
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
                Intent intent = new Intent(RequestDetailsActivity.this, ImgFullScreenActivity.class);
                intent.putExtra("r_billImage", image);
                startActivity(intent);
            }
        });

        transfer_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RequestDetailsActivity.this, ImgFullScreenActivity.class);
                intent.putExtra("r_transfer_image", str_transfer_image);
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

        close_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bitmap = null;
                IVPreviewImage.setVisibility(View.GONE);
                after_transfer_image.setVisibility(View.GONE);
                close_icon.setVisibility(View.GONE);
                submitBtn.setVisibility(View.GONE);
                uploadImgButton.setVisibility(View.VISIBLE);
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
        imageRecyclerView.setLayoutManager(new LinearLayoutManager(RequestDetailsActivity.this, RecyclerView.HORIZONTAL, false));
        imageAdapter = new SelectedImageAdapter(RequestDetailsActivity.this, selectedImagesList, imageRecvRelativeLayput, uploadImgButton, imageUrlList, "response");
        imageRecyclerView.setAdapter(imageAdapter);

    }

    private void getIntentData() {
        if (getIntent().hasExtra("exp_id")) {
            exp_id = getIntent().getStringExtra("exp_id");

        }
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
                if (url_type.equals("cancel_expense")) {
                    if (json != null) {
                        boolean status = json.getBoolean("status");

                        Toast.makeText(RequestDetailsActivity.this, "" + json.getString("msg"), Toast.LENGTH_SHORT).show();
                        if (status) {

                        }
                    }
                } else if (url_type == "expense_detail") {
                    try {
                        if (json != null) {
                            boolean status = json.getBoolean("status");
                            if (status) {
                                JSONObject object = json.getJSONObject("expense");
                                label = object.getString("label_name");
                                payment_status = object.getString("status");
                                amount = object.getString("amount");
                                str_headsname = object.getString("heads_name");
                                dateStr = object.getString("date_added");
                                userNote = "" + object.getString("note");
                                image = object.getString("image");;

                                payment_type = object.getString("payment_type");

                                Log.d("payment_status", String.valueOf(payment_status));

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
                                    imageRecyclerView.setLayoutManager(new LinearLayoutManager(RequestDetailsActivity.this, RecyclerView.HORIZONTAL, false));
                                    imageAdapter = new SelectedImageAdapter(RequestDetailsActivity.this, selectedImagesList, imageRecvRelativeLayput, uploadImgButton, imageUrlList, "response");
                                    imageRecyclerView.setAdapter(imageAdapter);
                                    imageRecvRelativeLayput.setVisibility(View.VISIBLE);
                                    imageRecyclerView.setVisibility(View.VISIBLE);
                                    if (!imageUrlList.isEmpty()) {
                                        uploadImgButton.setVisibility(View.GONE);
                                    }
                                }

                                SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                try {
                                    Date date = inputDateFormat.parse(dateStr);
                                    SimpleDateFormat outputDateFormat = new SimpleDateFormat("h:mm a, d MMMM yyyy");
                                    formattedDate = outputDateFormat.format(date);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                if (!object.isNull("approve")) {
                                    JSONObject approveobject = object.getJSONObject("approve");
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
                                if (!object.isNull("reject")) {
                                    JSONObject rejectobject = object.getJSONObject("reject");
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
                                if (!object.isNull("transfer")) {
                                    JSONObject transferobject = object.getJSONObject("transfer");
                                    transferview.setVisibility(View.VISIBLE);
                                    str_transferdby = transferobject.getString("user_name");
                                    str_note = transferobject.getString("note");
                                    str_transfer_image = transferobject.getString("image");
                                    transferDate_str = "" + transferobject.getString("date_added");
                                    try {
                                        Date t_date = inputDateFormat.parse(transferDate_str);
                                        SimpleDateFormat outputDateFormat = new SimpleDateFormat("h:mm a, d MMMM yyyy");
                                        t_formattedDate = outputDateFormat.format(t_date);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }

                                if (!object.isNull("cancelled")) {
                                    JSONObject rejectobject = object.getJSONObject("cancelled");
                                    Log.d("cancelled", rejectobject.toString());
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
                        cancel_btn.setVisibility(View.GONE);
                        int redColor = ContextCompat.getColor(context, R.color.red);
                        statusTv.setTextColor(redColor);
                    } else if (payment_status.equals("Declined")) {
                        int redColor = ContextCompat.getColor(context, R.color.red);
                        statusTv.setTextColor(redColor);
                        cancel_btn.setVisibility(View.GONE);
                    } else if (payment_status.equals("Approved")) {
                        int greenColor = ContextCompat.getColor(context, R.color.orange);
                        statusTv.setTextColor(greenColor);
                        cancel_btn.setVisibility(View.GONE);

                    } else if (payment_status.equals("Transferred")) {
                        int greenColor = ContextCompat.getColor(context, R.color.green);
                        statusTv.setTextColor(greenColor);
                        cancel_btn.setVisibility(View.GONE);
                        transferview.setVisibility(View.VISIBLE);

//                        if (is_img.equals(true)) {
//                            uploadImgButton.setVisibility(View.VISIBLE);
//                            Log.d("msggg", "Imag ecan be uploaded");
//                        } else {
//                            uploadImgButton.setVisibility(View.GONE);
//                        }

                    } else if (payment_status.equals("Pending")) {
                        cancel_btn.setVisibility(View.VISIBLE);
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


    public void cancelImage() {
        IVPreviewImage.setImageResource(android.R.color.transparent);
        IVPreviewImage.setVisibility(View.GONE);
        after_transfer_image.setVisibility(View.GONE);
        imageSelected = false;
        close_icon.setVisibility(View.GONE);
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
                                        close_icon.setVisibility(View.GONE);
                                        submitBtn.setVisibility(View.GONE);
                                        uploadImgButton.setVisibility(View.GONE);
                                        IVPreviewImage.setVisibility(View.GONE);
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
                                Toast.makeText(RequestDetailsActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
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
                                String imagename = Constants.getcustomer_id(RequestDetailsActivity.this) + System.currentTimeMillis() + i;
                                Log.d("selectedImagesList", "Pdf after uploding: " + pdfByteArray.toString());
                                params.put("image[" + i + "]", new DataPart(imagename + ".pdf", pdfByteArray));
                            } else {
                                Toast.makeText(RequestDetailsActivity.this, "Failed to convert PDF file", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Bitmap bitmap1 = null;
                            try {
                                Log.d("selectedImagesList", "selectedImagesList before uploding images: " + selectedImagesList.toString() + "\n Size: " + selectedImagesList.size());
                                bitmap1 = MediaStore.Images.Media.getBitmap(RequestDetailsActivity.this.getContentResolver(), selectedImagesList.get(i));
                                bitmap1 = CompressImage(bitmap1);
                                String imagename = Constants.getcustomer_id(RequestDetailsActivity.this) + System.currentTimeMillis() + i;
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

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (resultCode == RESULT_OK && requestCode == SELECT_IMAGE) {
//            if (data != null && data.getData() != null) {
//                // Image selected from the gallery
//                Uri selectedImage = data.getData();
//                try {
//                    // Handle the selected image here (e.g., upload it)
//                    handleSelectedImage(selectedImage);
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//            }
//        }
//    }


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
                submitBtn.setVisibility(View.GONE);
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

    private byte[] getPdfFileBytes(Uri pdfUri) {
        try (InputStream inputStream = RequestDetailsActivity.this.getContentResolver().openInputStream(pdfUri)) {
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

    private boolean isPdfFile(Uri uri) {
        ContentResolver contentResolver = context.getContentResolver();
        String type = contentResolver.getType(uri);
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
                submitBtn.setVisibility(View.GONE);
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
                        submitBtn.setVisibility(View.GONE);
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
                        submitBtn.setVisibility(View.GONE);
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
        imageRecyclerView.setLayoutManager(new LinearLayoutManager(RequestDetailsActivity.this, RecyclerView.HORIZONTAL, false));
        imageAdapter = new SelectedImageAdapter(RequestDetailsActivity.this, selectedImagesList, imageRecvRelativeLayput, uploadImgButton, imageUrlList, "storage");
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


    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
    private void showSettingsDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
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
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }


    private void updateUIWithData() {
        amountTV.setText("₹ " + amount);
        requestLabelTV.setText(label);
        headname.setText(str_headsname);
        statusTv.setText(payment_status);
//        paymenttype_Tv.setText(str_paymenttype);
        payment_typeTV.setText(payment_type);
        if (!userNote.isEmpty()) {
            user_note_layout.setVisibility(View.VISIBLE);
            user_note.setText(userNote);
        }

        if (!image.equals("null")) {
            Glide.with(context)
                    .load(image)
                    .placeholder(R.drawable.img_placeholder)
                    .into(billImg);
        }


        approvedby.setText(str_approveby);
        rejected_by.setText(rejectedBy);
        reason.setText(str_reason);
        requestDate.setText(formattedDate);
        transferDate.setText(t_formattedDate);
        transferdby.setText(str_transferdby);
        transferNote.setText(str_note);
        approve_Date.setText(app_formateDate);
        approve_Date.setText(app_formateDate);
        rejected_Date.setText(reject_formateDate);

        if (!str_transfer_image.equals("null")) {
            findViewById(R.id.img_gone).setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(str_transfer_image)
                    .placeholder(R.drawable.img_placeholder)
                    .into(transfer_img);
        } else {
            findViewById(R.id.img_gone).setVisibility(View.GONE);
        }

    }

    private void initView() {
        paymenttype_Tv = findViewById(R.id.paymenttype_Tv);
        statusTv = findViewById(R.id.statusTv);
        requestLabelTV = findViewById(R.id.requestLabelTV);
        billImg = findViewById(R.id.billImg);
        amountTV = findViewById(R.id.amountTV);
        requestDate = findViewById(R.id.requestDate);
        cancel_btn = findViewById(R.id.cancel_btn);
        backArrow = findViewById(R.id.backArrow);
        imgview = findViewById(R.id.imgview);
        context = this;
        user_note = findViewById(R.id.user_note);
        user_note_layout = findViewById(R.id.user_note_layout);
        approvedview = findViewById(R.id.approvedview);
        rejectedview = findViewById(R.id.rejectedview);
        approvedby = findViewById(R.id.approved_by);
        approve_Date = findViewById(R.id.approve_Date);
        rejected_by = findViewById(R.id.rejected_by);
        rejected_Date = findViewById(R.id.rejected_Date);
        reason = findViewById(R.id.reason);
        transfer_img = findViewById(R.id.paymentImg);
        transferDate = findViewById(R.id.transfer_Date);
        transferdby = findViewById(R.id.transfer_by);
        transferview = findViewById(R.id.transferView);
        transferNote = findViewById(R.id.transferNote);
        uploadImgButton = findViewById(R.id.uploadImgButton);
        close_icon = findViewById(R.id.close_icon);
        IVPreviewImage = findViewById(R.id.IVPreviewImage);
        after_transfer_image = findViewById(R.id.previewimage);
        submitBtn = findViewById(R.id.submitBtn);
        headname = findViewById(R.id.headname);

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
    }

    public void cancelExpense(String status_id) {
        Utils.showProgressDialog(RequestDetailsActivity.this, false);
        HashMap<String, String> params = new HashMap<>();
        params.put("type", "expense_status_update");
        params.put("emp_id", "" + MyApplication.mSp.getKey(SPCsnstants.id));
        params.put("exp_id", exp_id);
        params.put("status_id", status_id);
        Log.d("CanclledRequest", note);
        params.put("note", note);
        apinetwork.requestWithJsonObject(Constants.EXPENSE_STATUS_UPDATE, params, vr, "expense_status_update");
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
                note = reasonET.getText().toString();
                if (note.isEmpty()) {
                    reasonET.setError("");
                } else {
                    sheet.cancel();
                    cancelExpense("3");
                    finish();
                }

            }
        });
    }

    public void getExpenseDetail() {
        Utils.showProgressDialog(RequestDetailsActivity.this, false);
        HashMap<String, String> params = new HashMap<>();
        params.put("type", "expense_detail");
        params.put("exp_id", exp_id);
        apinetwork.requestWithJsonObject(Constants.EXPENSE_DETAIL, params, vr, "expense_detail");
    }
}
