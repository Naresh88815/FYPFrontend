package com.application.expensemanager.fragments;

import static android.app.Activity.RESULT_OK;
import static com.application.expensemanager.utils.MyApplication.apinetwork;
import static com.application.expensemanager.utils.Utils.CompressImage;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ContentResolver;
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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;
import com.application.expensemanager.R;
import com.application.expensemanager.activity.AdminMainActivity;
import com.application.expensemanager.adapter.ExpenseLabelAdapter;
import com.application.expensemanager.adapter.OptionsAdapter;
import com.application.expensemanager.adapter.SelectedImageAdapter;
import com.application.expensemanager.interfaces.Backpressedlistener;
import com.application.expensemanager.model.ChooseFileOptionModel;
import com.application.expensemanager.model.ExpenseHeadModel;
import com.application.expensemanager.model.ExpenseLabelModel;
import com.application.expensemanager.network.VolleyResponse;
import com.application.expensemanager.utils.Constants;
import com.application.expensemanager.utils.MyApplication;
import com.application.expensemanager.utils.SPCsnstants;
import com.application.expensemanager.utils.Utils;
import com.application.expensemanager.utils.VolleyMultipartRequest;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment implements Backpressedlistener {
    View view, popupBg;
    AppCompatButton createExpenseLabelBtn;
    SearchView searchLabel;
    Spinner expenseLabelSpinner, expenseHeadSpinner;
    String selectedlabelId, selectlabelname;
    String selectedHeadId, selectedHeadName;
    ImageView IVPreviewImage;
    Boolean img_status = false;
    AppCompatButton submitBtn;
    EditText expenseLabelET, expenseAmountET;
    List<ExpenseLabelModel> modelList = new ArrayList<>();
    List<ExpenseHeadModel> expenseHeadList = new ArrayList<>();
    List<ExpenseLabelModel> filteredList = new ArrayList<>();
    private boolean imageSelected = false;
    ImageView close_icon;
    RecyclerView searchLabelRecyclerView;
    ExpenseLabelAdapter expenseLabelAdapter;
    ArrayAdapter<String> adapter;
    ArrayAdapter<String> arrayAdapter;
    List<String> labelsList;
    List<String> headsList;
    public static Backpressedlistener backpressedlistener;
    String label;
    Boolean show_image_btn = false;
    String label_id;
    String id;
    String amount;
    Bitmap bitmap = null;
    String paymentStatus;
    EditText note;
    String str_note;
    Uri outputFileUri;
    String is_role;
    RelativeLayout previewimage, uploadImgBtn;
    String status_img;
    LinearLayout paymentLayout, bankAccountLayout, accountDetailsLayout, bankAccountDetailsLayout;
    RadioGroup paymentRadioGroup, bankAccountRadioGroup, amountPaidTypeRadioGroup;
    RadioButton radioPrepaid, radioPostpaid;
    String paymentType = "";
    EditText upiIdEditText, nameEditText, accountNumberEditText, ifscCodeEditText;
    private static final int SELECT_MULTIPLE_IMAGES = 123;
    private static final int SELECT_PDF = 3;
    int SELECT_IMAGE = 002;
    RecyclerView imageRecyclerView;
    SelectedImageAdapter imageAdapter;
    List<Uri> selectedImagesList = new ArrayList<>();
    RelativeLayout imageRecvRelativeLayput;
    List<ChooseFileOptionModel> optionItems = new ArrayList<>();
    List<String> imageUrlList = new ArrayList<>();
    int amountValue;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.home_fragment, container, false);
        initView();
        Log.d("user_id", MyApplication.mSp.getKey(SPCsnstants.id));
        Log.d("super_user", MyApplication.mSp.getKey(SPCsnstants.super_user));
        fetchExpenseHeads();
        viewLabel();
        updateSpinnerWithModelList();
        searchQuery();


        createExpenseLabelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreateLabelBottomSheet();

            }
        });

        uploadImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                    // Request permissions
                    Dexter.withActivity(getActivity())
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
                    Dexter.withActivity(getActivity())
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
            public void onClick(View v) {
                cancelImage("0");
            }
        });

        expenseLabelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position >= 1 && position < modelList.size()) {
                    ExpenseLabelModel expenseLabelModel = new ExpenseLabelModel(adapter.getItem(position).toString(), "");
                    modelList.set(position, expenseLabelModel);
                    expenseLabelET.setText(adapter.getItem(position));
                    expenseAmountET.setVisibility(View.VISIBLE);
                    uploadImgBtn.setVisibility(View.VISIBLE);
                    note.setVisibility(View.VISIBLE);
                    submitBtn.setVisibility(View.VISIBLE);
                    paymentLayout.setVisibility(View.VISIBLE);

                    showImageBtn();

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Your code here
            }
        });


        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentStatus = "1";
                amount = expenseAmountET.getText().toString();
                try {
                    amountValue = Integer.parseInt(amount);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                str_note = note.getText().toString();

                ExpenseLabelModel expenseLabelModel = new ExpenseLabelModel(label, id);
                label_id = expenseLabelModel.getLabelId();

                int selectedHeadPosition = expenseHeadSpinner.getSelectedItemPosition();

                int selectedLabelPosition = expenseLabelSpinner.getSelectedItemPosition();

                if (selectedHeadId == null || selectedHeadId.trim().isEmpty() || selectedHeadPosition == 0) {
                    Toast.makeText(getContext(), "Please select heads", Toast.LENGTH_SHORT).show();

                } else if (selectedlabelId.trim().isEmpty() || selectedLabelPosition == 0) {
                    Toast.makeText(getContext(), "Please select label", Toast.LENGTH_SHORT).show();

                } else if (amount.isEmpty()) {
                    expenseAmountET.setError("This field is required");
                } else if (amount.equals("0") || amountValue == 0) {
                    Toast.makeText(getContext(), "Please Enter Valid Amount", Toast.LENGTH_SHORT).show();
                } else {
                    if (paymentRadioGroup.getCheckedRadioButtonId() == -1) {
                        showAlertDialog("Please select a payment type.");
                    } else {
                        if (radioPrepaid.isChecked()) {
                                paymentType = "Prepaid";
                        } else if (radioPostpaid.isChecked()) {
                            paymentType = "Postpaid";
                        }
                    }
                }
                createExpense(paymentType);
            }
        });

        paymentRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                bankAccountRadioGroup.clearCheck();
                if (checkedId == R.id.radioPrepaid) {
                    accountDetailsLayout.setVisibility(View.GONE);
                    amountPaidTypeRadioGroup.setVisibility(View.GONE);
                    amountPaidTypeRadioGroup.clearCheck();
                    upiIdEditText.setVisibility(View.GONE);
                    bankAccountDetailsLayout.setVisibility(View.GONE);
                    accountDetailsLayout.setVisibility(View.GONE);
                    nameEditText.setText("");
                    accountNumberEditText.setText("");
                    ifscCodeEditText.setText("");
                    upiIdEditText.setText("");
                    submitBtn.setVisibility(View.VISIBLE);
                } else if (checkedId == R.id.radioPostpaid) {
                    view.findViewById(R.id.bankAccountLayout).setVisibility(View.GONE);
                    accountDetailsLayout.setVisibility(View.VISIBLE);
                    amountPaidTypeRadioGroup.setVisibility(View.GONE);
                    amountPaidTypeRadioGroup.clearCheck();
                    upiIdEditText.setVisibility(View.GONE);
                    bankAccountDetailsLayout.setVisibility(View.GONE);
                    nameEditText.setText("");
                    accountNumberEditText.setText("");
                    ifscCodeEditText.setText("");
                    upiIdEditText.setText("");
                    submitBtn.setVisibility(View.VISIBLE);

                }
            }
        });


//        amountPaidTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                if (checkedId == R.id.upiRadioButton) {
//                    upiIdEditText.setVisibility(View.VISIBLE);
//                    bankAccountDetailsLayout.setVisibility(View.GONE);
//                    nameEditText.setText("");
//                    accountNumberEditText.setText("");
//                    ifscCodeEditText.setText("");
//                } else if (checkedId == R.id.bankRadioButton) {
//                    bankAccountDetailsLayout.setVisibility(View.VISIBLE);
//                    upiIdEditText.setVisibility(View.GONE);
//                    upiIdEditText.setText("");
//                } else {
//                    upiIdEditText.setVisibility(View.GONE);
//                    bankAccountDetailsLayout.setVisibility(View.GONE);
//                    nameEditText.setText("");
//                    accountNumberEditText.setText("");
//                    ifscCodeEditText.setText("");
//                    upiIdEditText.setText("");
//                }
//            }
//        });

        optionItems.add(new ChooseFileOptionModel("Gallery", R.drawable.gallery_icon));
        optionItems.add(new ChooseFileOptionModel("Camera", R.drawable.camera_icon));
        optionItems.add(new ChooseFileOptionModel("PDF", R.drawable.baseline_picture_as_pdf_24));

        imageRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        imageAdapter = new SelectedImageAdapter(getContext(), selectedImagesList, imageRecvRelativeLayput, uploadImgBtn, imageUrlList, "storage");
        imageRecyclerView.setAdapter(imageAdapter);

        return view;
    }

    private void showAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Alert")
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void showImageBtn() {
        if (show_image_btn.equals(true)) {
            if (status_img.equals("0")) {
                uploadImgBtn.setVisibility(View.VISIBLE);
                previewImageShowHide();

            } else if (status_img.equals("2")) {
                uploadImgBtn.setVisibility(View.GONE);
            } else {
                uploadImgBtn.setVisibility(View.GONE);
            }
        }
    }

    private void previewImageShowHide() {
        cancelImage("0");
    }

    private void showSettingsDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
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
        Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void clearEdittext() {
        searchLabel.setQuery("", true);
        searchLabel.clearFocus();
    }

    private void searchQuery() {
        searchLabel.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String value) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String value) {
                performSearch(value);
                return true;
            }
        });

    }

    private void initView() {
        createExpenseLabelBtn = view.findViewById(R.id.createExpenseLabelBtn);
        searchLabel = view.findViewById(R.id.searchLabel);
        expenseLabelSpinner = view.findViewById(R.id.expenseLabelSpinner);
        expenseHeadSpinner = view.findViewById(R.id.expenseHeadSpinner);
        IVPreviewImage = view.findViewById(R.id.IVPreviewImage);
        uploadImgBtn = view.findViewById(R.id.uploadImgBtn);
        close_icon = view.findViewById(R.id.close_icon);
        expenseLabelET = view.findViewById(R.id.expenseLabelET);
        expenseAmountET = view.findViewById(R.id.expenseAmountET);
        searchLabelRecyclerView = view.findViewById(R.id.searchLabelRecyclerView);
        submitBtn = view.findViewById(R.id.submitBtn);
        popupBg = view.findViewById(R.id.popupBg);
        note = view.findViewById(R.id.note);
        getprefences();
        previewimage = view.findViewById(R.id.previewimage);
        paymentLayout = (LinearLayout) view.findViewById(R.id.paymentLayout);
        bankAccountLayout = (LinearLayout) view.findViewById(R.id.bankAccountLayout);
        paymentRadioGroup = (RadioGroup) view.findViewById(R.id.paymentRadioGroup);
        radioPrepaid = (RadioButton) view.findViewById(R.id.radioPrepaid);
        radioPostpaid = (RadioButton) view.findViewById(R.id.radioPostpaid);
        bankAccountRadioGroup = (RadioGroup) view.findViewById(R.id.bankAccountRadioGroup);
        accountDetailsLayout = (LinearLayout) view.findViewById(R.id.accountDetailsLayout);
        bankAccountDetailsLayout = (LinearLayout) view.findViewById(R.id.bankAccountDetailsLayout);
        amountPaidTypeRadioGroup = (RadioGroup) view.findViewById(R.id.amountPaidTypeRadioGroup);
        upiIdEditText = (EditText) view.findViewById(R.id.upiIdEditText);
        nameEditText = (EditText) view.findViewById(R.id.nameEditText);
        accountNumberEditText = (EditText) view.findViewById(R.id.accountNumberEditText);
        ifscCodeEditText = (EditText) view.findViewById(R.id.ifscCodeEditText);
        imageRecyclerView = (RecyclerView) view.findViewById(R.id.imageRecyclerView);
        imageRecvRelativeLayput = view.findViewById(R.id.imageRecvRelativeLayput);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_MULTIPLE_IMAGES) {
                if (data != null) {
                    handleSelectedMultipleImages(data);
                }
            } else if (requestCode == SELECT_IMAGE) {
                if (outputFileUri != null) {
                    // Camera result
                    handleCameraResult();
                }
            } else if (requestCode == SELECT_PDF) {
                if (outputFileUri != null) {
                    // PDF result
                    handlePdfResult(data);
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
            }
            Log.d("afterNotify", selectedImagesList.toString());
        }
    }

    private void handleCameraResult() {
        // Add the camera image to the list or perform any necessary actions
        Log.d("CameraResult", "outputFileUri" + outputFileUri.toString());
        try {
            // Call the method to handle the result
            handleSelectedImage(outputFileUri);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void handleSelectedMultipleImages(Intent data) {
        if (data != null) {
            ClipData clipData = data.getClipData();

            if (clipData != null) {
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    Uri selectedImage = clipData.getItemAt(i).getUri();
                    try {
                        handleSelectedImage(selectedImage);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            } else {
                // Handle single image
                Uri selectedImage = data.getData();
                try {
                    handleSelectedImage(selectedImage);
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
                // Camera image (file URI)
                String filePath = selectedImage.getPath();
                Log.d("CameraResult", "File path: " + filePath); // Log the file path
                main_bitmap = BitmapFactory.decodeFile(filePath);

                if (main_bitmap == null) {
                    Log.e("CameraResult", "Failed to decode file: " + filePath);
                } else {
                    Log.d("CameraResult", "main_bitmap" + main_bitmap.toString());
                }
            } else {
                // Gallery image (content URI)
                main_bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), selectedImage);
                Log.d("CameraResult", "main_bitmap" + main_bitmap.toString());
            }

            // Process the bitmap as needed (e.g., compress, display)
            bitmap = CompressImage(main_bitmap);
            IVPreviewImage.setImageBitmap(bitmap);

            // Add the URI to the list
            selectedImagesList.add(selectedImage);

            // Notify the adapter that the data set has changed
            imageSelected = true;
            imageAdapter.notifyDataSetChanged();

            // Show or hide layout based on the list
            if (selectedImagesList.isEmpty()) {
                imageRecvRelativeLayput.setVisibility(View.GONE);
            } else {
                imageRecvRelativeLayput.setVisibility(View.VISIBLE);
            }

            // Log for debugging
            Log.d("afterNotify", selectedImagesList.toString());
            IVPreviewImage.setTag(selectedImage.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void chooseImage() {
        showBottomSheetDialog();
    }

    private void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = LayoutInflater.from(requireContext()).inflate(R.layout.file_option_item_layout, null);
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

            recyclerViewOptions.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));
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

        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = new File(storageDir, imageFileName);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            outputFileUri = FileProvider.getUriForFile(requireContext(),
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
                startActivityForResult(takePhoto, SELECT_IMAGE);
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

    public void cancelImage(String status) {
        IVPreviewImage.setImageResource(android.R.color.transparent);
        IVPreviewImage.setVisibility(View.GONE);
        previewimage.setVisibility(View.GONE);
        imageSelected = false;
        bitmap = null;
        close_icon.setVisibility(View.GONE);
        if (status.equals("2")) {
            uploadImgBtn.setVisibility(View.GONE);
        } else {
            uploadImgBtn.setVisibility(View.VISIBLE);
        }
    }

    private List<String> getLabelsFromModelList() {
        List<String> labels = new ArrayList<>();
        for (ExpenseLabelModel model : modelList) {
            labels.add(model.getExpenseLabel()); // Assuming there's a getExpenseLabel() method in ExpenseLabelModel
        }
        return labels;
    }

    private List<String> getExpenseHeadsFromHeadList() {
        List<String> heads = new ArrayList<>();
        for (ExpenseHeadModel headModel : expenseHeadList) {
            Log.d("namcnjfd", "" + headModel.getHead_name());
            heads.add(headModel.getHead_name()); // Assuming there's a getExpenseLabel() method in ExpenseLabelModel
        }
        return heads;
    }

    private void updateSpinnerWithModelList() {
        labelsList = getLabelsFromModelList();

        // Add the instruction as the first item
        labelsList.add(0, "Select a label");

        adapter = new ArrayAdapter<>(requireContext(), // Use requireContext() if this is not a Context
                android.R.layout.simple_spinner_item, labelsList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        expenseLabelSpinner.setAdapter(adapter); // Set the adapter

        // Set the default selection to the instruction
        expenseLabelSpinner.setSelection(0);

        expenseLabelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i > 0) {
                    selectedlabelId = modelList.get(i - 1).getLabelId();
                    selectlabelname = modelList.get(i - 1).getExpenseLabel();
//                    submitBtn.setVisibility(View.VISIBLE);
                    searchLabelRecyclerView.setVisibility(View.GONE);
                    expenseAmountET.setVisibility(View.VISIBLE);
                    note.setVisibility(View.VISIBLE);
                    createExpenseLabelBtn.setVisibility(View.GONE);
                    paymentLayout.setVisibility(View.VISIBLE);
                    showImageBtn();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Handle the case where nothing is selected (if needed)
            }
        });

    }

    private void updateExpenseHeadSpinnerWithModelList() {
        headsList = getExpenseHeadsFromHeadList();

        // Add the instruction as the first item
        headsList.add(0, "Select an Expense Head");

        arrayAdapter = new ArrayAdapter<>(requireContext(), // Use requireContext() if this is not a Context
                android.R.layout.simple_spinner_item, headsList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        expenseHeadSpinner.setAdapter(arrayAdapter); // Set the adapter

        // Set the default selection to the instruction
        expenseHeadSpinner.setSelection(0);

        expenseHeadSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i > 0) {
                    selectedHeadId = expenseHeadList.get(i - 1).getHead_id();
                    selectedHeadName = expenseHeadList.get(i - 1).getHead_name();
//                    selectedhead_img = expenseHeadList.get(i - 1).getHead_imgstatus();
//                    status_img = expenseHeadList.get(i - 1).getHead_imgstatus();
                    searchLabel.setVisibility(View.VISIBLE);
                    expenseLabelSpinner.setVisibility(View.VISIBLE);
                    expenseAmountET.setVisibility(View.VISIBLE);
                    uploadImgBtn.setVisibility(View.VISIBLE);
                    close_icon.setVisibility(View.VISIBLE);
                    note.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Handle the case where nothing is selected (if needed)
            }
        });

    }

    public void performSearch(String value) {
        filteredList.clear();
        String searchValue = value.toLowerCase(); // Convert search query to lowercase
        if (expenseLabelAdapter == null) {
            expenseLabelAdapter = new ExpenseLabelAdapter(new ArrayList<>(), getContext());
        }
        try {
            if (searchValue.isEmpty()) { // If the search query is empty, clear the filtered list and hide the RecyclerView
                filteredList.clear();
                searchLabelRecyclerView.setVisibility(View.GONE);
            } else {
                for (ExpenseLabelModel expenseLabelModel : modelList) {
                    String label = expenseLabelModel.getExpenseLabel().toLowerCase(); // Convert label to lowercase
                    if (label.contains(searchValue)) {
                        filteredList.add(expenseLabelModel);
                    }
                }

                if (filteredList.size() > 0) {
                    searchLabelRecyclerView.setVisibility(View.VISIBLE);
                    searchLabelRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                    expenseLabelAdapter = new ExpenseLabelAdapter(filteredList, getContext());
                    searchLabelRecyclerView.setAdapter(expenseLabelAdapter);

                    expenseLabelAdapter.setOnItemClickListener(new ExpenseLabelAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(ExpenseLabelModel model) {
                            clearEdittext();
                            String selectedLabel = model.getExpenseLabel();
                            Log.d("label", selectedLabel);
                            selectedlabelId = "" + model.getLabelId();
                            uploadImgBtn.setVisibility(View.VISIBLE);
                            cancelImage("0");
                            submitBtn.setVisibility(View.VISIBLE);
                            searchLabelRecyclerView.setVisibility(View.GONE);
                            expenseAmountET.setVisibility(View.VISIBLE);
                            note.setVisibility(View.VISIBLE);
                            createExpenseLabelBtn.setVisibility(View.GONE);
                            int position = labelsList.indexOf(selectedLabel);

                            // Set the selection in the spinner
                            expenseLabelSpinner.setSelection(position);
                        }

                    });
                } else {
                    searchLabelRecyclerView.setVisibility(View.GONE);
                }

                expenseLabelAdapter.setFilter(filteredList);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }


    }


    private void openCreateLabelBottomSheet() {
        EditText expenseLabelTxt;
        TextView submit_btn;
        final BottomSheetDialog sheet = new BottomSheetDialog(getActivity());
        sheet.setContentView(R.layout.bottomsheet_add_label);
        expenseLabelTxt = sheet.findViewById(R.id.expenseLabelTxt);
        submit_btn = sheet.findViewById(R.id.submit_btn);
        sheet.show();

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                label = expenseLabelTxt.getText().toString();
                if (label.isEmpty()) {
                    expenseLabelTxt.setError("");
                } else {
                    sheet.cancel();
                    addExpenseLabel();
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
                if (url_type.equals("add_label")) {
                    if (json != null) {
                        boolean status = json.getBoolean("status");
                        String message = json.getString("message");
                        Toast.makeText(getContext(), "" + message, Toast.LENGTH_SHORT).show();
                        if (status) {
                            refreshFragment();
                            Toast.makeText(getContext(), "" + message, Toast.LENGTH_SHORT).show();
                        }

                    }
                } else if (url_type.equals("view_label")) {
                    if (json != null) {
                        boolean status = json.getBoolean("status");
                        if (status) {
                            try {
                                JSONArray labelListArray = json.getJSONArray("label");

                                Log.d("labels", String.valueOf(labelListArray));

                                modelList.clear();

                                for (int i = 0; i < labelListArray.length(); i++) {
                                    try {
                                        JSONObject object = labelListArray.getJSONObject(i);
                                        Log.d("expenselabels", "Lable name: " + object.toString());
                                        String labelName = object.getString("label_name");
                                        String label_id = object.getString("label_id");


                                        ExpenseLabelModel expenseLabelModel = new ExpenseLabelModel(labelName, label_id);
                                        modelList.add(expenseLabelModel);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                updateSpinnerWithModelList(); // Update the spinner with the new data

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getContext(), "" + json.getString("msg"), Toast.LENGTH_SHORT).show();

                        }

                    }
                } else if (url_type == "create_expense") {
                    if (json != null) {
                        boolean status = json.getBoolean("status");
                        if (status) {
                            Toast.makeText(getContext(), "" + json.getString("msg"), Toast.LENGTH_SHORT).show();
                            refreshFragment();
                        } else {
                            Toast.makeText(getContext(), "", Toast.LENGTH_SHORT).show();

                        }
                    }
                } else if (url_type == "view_exp_heads") {
                    if (json != null) {
                        boolean status = json.getBoolean("status");
                        if (status) {
                            try {
                                JSONArray expenseHeadArray = json.getJSONArray("head");
                                expenseHeadList.clear();
                                for (int i = 0; i < expenseHeadArray.length(); i++) {
                                    try {
                                        JSONObject object = expenseHeadArray.getJSONObject(i);

                                        String Name = object.getString("name");
                                        String img_status = "" + object.getInt("image_status");
                                        id = object.getString("head_id");
                                        Log.d("expenseHeadsData", "expense heads: " + object.toString());
                                        ExpenseHeadModel expenseHeadModel = new ExpenseHeadModel(id, Name, img_status);
                                        expenseHeadList.add(expenseHeadModel);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                updateExpenseHeadSpinnerWithModelList();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getContext(), "", Toast.LENGTH_SHORT).show();

                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    };


    private void addExpenseLabel() {
        Utils.showProgressDialog(getContext(), false);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("type", "add_label");
        params.put("label_name", label);
        params.put("emp_id", "" + MyApplication.mSp.getKey(SPCsnstants.id));
        apinetwork.requestWithJsonObject(Constants.ADD_LABEL, params, vr, "add_label");
    }

    private void viewLabel() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("type", "view_label");
        apinetwork.requestWithJsonObject(Constants.VIEW_LABEL, params, vr, "view_label");
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private byte[] getFileDataFromFile(File file) {
        byte[] bytes = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    private byte[] getPdfFileBytes(Uri pdfUri) {
        try (InputStream inputStream = getContext().getContentResolver().openInputStream(pdfUri)) {
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
        ContentResolver contentResolver = requireContext().getContentResolver();
        String type = contentResolver.getType(uri);
        Log.d("PDFType", "Detected type: " + type);
        return type != null && type.startsWith("application/pdf");
    }


    private boolean timeoutErrorOccurred = false;


//    private void createExpense(String paymentType, String accountType, String selectedBankId, String paymentToBePaidType, String upiIdText, String nameText, String accountNumberText, String ifscCodeText) {
//        if (Utils.isNetworkAvailable(getContext())) {
//            Utils.showProgressDialog(getContext(), false);
//            String url = Constants.CREATE_EXPENSE;
//            if (!timeoutErrorOccurred) {
//                VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
//                        new Response.Listener<NetworkResponse>() {
//                            @Override
//                            public void onResponse(NetworkResponse response) {
//                                Utils.dismisProgressDialog();
//                                try {
//                                    if (!timeoutErrorOccurred) {
//                                        String responseData = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
//                                        Log.d("ResponseString", responseData);
//
//                                        JSONObject json = new JSONObject(responseData);
//                                        boolean status = json.getBoolean("status");
//                                        String msg = json.getString("message");
//                                        if (status) {
//                                            amount = "";
//                                            bitmap = null;
//                                            selectedlabelId = "";
//                                            selectedHeadId = "";
//                                            uploadImgBtn.setVisibility(View.GONE);
//                                            submitBtn.setVisibility(View.GONE);
//                                            searchLabelRecyclerView.setVisibility(View.VISIBLE);
//                                            expenseAmountET.setVisibility(View.GONE);
//                                            createExpenseLabelBtn.setVisibility(View.VISIBLE);
//                                            labelsList.clear();
//                                            Log.d("is_role", is_role);
//
//                                            if (is_role.equals(SPCsnstants.user)) {
//                                                AdminMainActivity.adminBottomNavigationView.setSelectedItemId(R.id.navigate_requests);
//                                                AdminMainActivity.title.setText("Expense Manager");
//                                            } else {
//                                                AdminMainActivity.adminBottomNavigationView.setSelectedItemId(R.id.navigate_received_requests);
//                                                AdminMainActivity.title.setText("Expense Manager");
//                                            }
//                                        }
//                                        Toast.makeText(getContext(), "" + msg, Toast.LENGTH_LONG).show();
//                                        Log.d("ErrorType", "" + msg);
//                                    }
//                                } catch (Exception e) {
//                                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
//                                    Log.d("ErrorType", "ErrorType: " + e.toString());
//                                    e.printStackTrace();
//                                }
//                            }
//                        },
//                        new Response.ErrorListener() {
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//                                Utils.dismisProgressDialog();
//                                if (error instanceof TimeoutError) {
//                                    Log.e("VolleyTimeoutError", "Volley timeout error occurred");
//                                    Toast.makeText(getContext(), "Expense created successfully.", Toast.LENGTH_SHORT).show();
//                                    if (is_role.equals(SPCsnstants.user)) {
//                                        AdminMainActivity.adminBottomNavigationView.setSelectedItemId(R.id.navigate_requests);
//                                        AdminMainActivity.title.setText("Expense Manager");
//                                    } else {
//                                        AdminMainActivity.adminBottomNavigationView.setSelectedItemId(R.id.navigate_received_requests);
//                                        AdminMainActivity.title.setText("Expense Manager");
//                                    }
//                                    timeoutErrorOccurred = true;
//                                } else {
//                                    Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        }
//                ) {
//                    @Override
//                    protected Map<String, String> getParams() throws Error {
//                        Map<String, String> params = new HashMap<>();
//                        params.put("type", "create_expense");
//                        params.put("emp_id", "" + MyApplication.mSp.getKey(SPCsnstants.id));
//                        params.put("head_id", selectedHeadId);
//                        params.put("label_id", selectedlabelId);
//                        params.put("amount", amount);
//                        params.put("note", str_note);
//                        params.put("payment_type", paymentType);
//                        Log.d("expense params", "" + params);
//                        return params;
//                    }
//
//                    @Override
//                    protected Map<String, DataPart> getByteData() {
//                        Map<String, DataPart> params = new HashMap<>();
//                        Log.d("selectedImagesList", "Images: " + selectedImagesList.toString());
//                        for (int i = 0; i < selectedImagesList.size(); i++) {
//                            if (isPdfFile(selectedImagesList.get(i))) {
//                                Uri pdfUri = selectedImagesList.get(i);
//                                byte[] pdfByteArray = getPdfFileBytes(pdfUri);
//                                if (pdfByteArray != null) {
//                                    String imagename = Constants.getcustomer_id(getContext()) + System.currentTimeMillis() + i;
//                                    Log.d("selectedImagesList", "Pdf after uploding: " + pdfByteArray.toString());
//                                    params.put("image[" + i + "]", new DataPart(imagename + ".pdf", pdfByteArray));
//                                } else {
//                                    Toast.makeText(getContext(), "Failed to convert PDF file", Toast.LENGTH_SHORT).show();
//                                }
//                            } else {
//                                Bitmap bitmap1 = null;
//                                try {
//                                    Log.d("selectedImagesList", "selectedImagesList before uploding images: " + selectedImagesList.toString() + "\n Size: " + selectedImagesList.size());
//                                    bitmap1 = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), selectedImagesList.get(i));
//                                    bitmap1 = CompressImage(bitmap1);
//                                    String imagename = Constants.getcustomer_id(getContext()) + System.currentTimeMillis() + i;
//                                    params.put("image[" + i + "]", new DataPart(imagename + ".jpg", getFileDataFromDrawable(bitmap1)));
//                                } catch (IOException e) {
//                                    throw new RuntimeException(e);
//                                }
//                            }
//                        }
//                        Log.d("FileUploadInParams", "FileUploadInParams: " + params);
//                        return params;
//                    }
//
//                    @Override
//                    public RetryPolicy getRetryPolicy() {
//                        return new DefaultRetryPolicy(
//                                0,
//                                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
//                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
//                        );
//                    }
//                };
//                Volley.newRequestQueue(getContext()).add(volleyMultipartRequest);
//            } else {
//                Toast.makeText(getContext(), "No internet connection", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
private void createExpense(String paymentType) {
    if (Utils.isNetworkAvailable(getContext())) {
        Utils.showProgressDialog(getContext(), false);
        String url = Constants.CREATE_EXPENSE;
        if (!timeoutErrorOccurred) {
            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                            Utils.dismisProgressDialog();
                            try {
                                if (!timeoutErrorOccurred) {
                                    JSONObject json = new JSONObject(new String(response.data));
                                    Log.d("ResponseString", new String(response.data));
                                    if (json != null) {
                                        boolean status = json.getBoolean("status");
                                        Log.d("status", "Create Expense: " + String.valueOf(status));
                                        String msg = json.getString("message");
                                        if (status) {
                                            amount = "";
                                            bitmap = null;
                                            selectedlabelId = "";
                                            selectedHeadId = "";
                                            uploadImgBtn.setVisibility(View.GONE);
                                            submitBtn.setVisibility(View.GONE);
                                            searchLabelRecyclerView.setVisibility(View.VISIBLE);
                                            expenseAmountET.setVisibility(View.GONE);
                                            createExpenseLabelBtn.setVisibility(View.VISIBLE);
                                            labelsList.clear();
                                            // ... other logic for successful response ...
                                            Log.d("is_role", is_role);

                                            if (is_role.equals(SPCsnstants.user)) {
                                                AdminMainActivity.adminBottomNavigationView.setSelectedItemId(R.id.navigate_requests);
                                                AdminMainActivity.title.setText("Expense Manager");
                                            } else {
                                                AdminMainActivity.adminBottomNavigationView.setSelectedItemId(R.id.navigate_received_requests);
                                                AdminMainActivity.title.setText("Expense Manager");
                                            }
                                        }
                                        Toast.makeText(getContext(), "" + msg, Toast.LENGTH_LONG).show();
                                        Log.d("ErrorType", "" + msg);
                                    }
                                }
                            } catch (Exception e) {
                                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                                Log.d("ErrorType", "ErrorType: " + e.toString());
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
                                Toast.makeText(getContext(), "Expense created successfully.", Toast.LENGTH_SHORT).show();
                                if (is_role.equals(SPCsnstants.user)) {
                                    AdminMainActivity.adminBottomNavigationView.setSelectedItemId(R.id.navigate_requests);
                                    AdminMainActivity.title.setText("Expense Manager");
                                } else {
                                    AdminMainActivity.adminBottomNavigationView.setSelectedItemId(R.id.navigate_received_requests);
                                    AdminMainActivity.title.setText("Expense Manager");
                                }
                                timeoutErrorOccurred = true;
                            } else {
                                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() throws Error {
                    Map<String, String> params = new HashMap<>();
                    params.put("type", "create_expense");
                    params.put("head_id", selectedHeadId);
                    params.put("label_id", selectedlabelId);
                    params.put("emp_id", "" + MyApplication.mSp.getKey(SPCsnstants.id));
                    params.put("amount", amount);
                    params.put("note", str_note);
                    params.put("payment_type", paymentType);
                    params.put("status",paymentStatus);
                    Log.d("expense params", "" + params);
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
                                String imagename = Constants.getcustomer_id(getContext()) + System.currentTimeMillis() + i;
                                Log.d("selectedImagesList", "Pdf after uploding: " + pdfByteArray.toString());
                                params.put("image[" + i + "]", new DataPart(imagename + ".pdf", pdfByteArray));
                            } else {
                                Toast.makeText(getContext(), "Failed to convert PDF file", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Bitmap bitmap1 = null;
                            try {
                                Log.d("selectedImagesList", "selectedImagesList before uploding images: " + selectedImagesList.toString() + "\n Size: " + selectedImagesList.size());
                                bitmap1 = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), selectedImagesList.get(i));
                                bitmap1 = CompressImage(bitmap1);
                                String imagename = Constants.getcustomer_id(getContext()) + System.currentTimeMillis() + i;
                                params.put("image[" + i + "]", new DataPart(imagename + ".jpg", getFileDataFromDrawable(bitmap1)));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    Log.d("FileUploadInParams", "FileUploadInParams: " + params);
                    return params;
                }
                @Override
                public RetryPolicy getRetryPolicy() {
                    return new DefaultRetryPolicy(
                            0,
                            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                    );
                }
            };
            Volley.newRequestQueue(getContext()).add(volleyMultipartRequest);
        } else {
            Toast.makeText(getContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }
}


    private void getprefences() {
        is_role = MyApplication.mSp.getKey(SPCsnstants.IS_screen);
    }

    public void fetchExpenseHeads() {
        Utils.showProgressDialog(getContext(), false);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("type", "view_exp_heads");
        apinetwork.requestWithJsonObject(Constants.EXPENSE_HEAD, params, vr, "view_exp_heads");
    }

    public void refreshFragment() {
        if (is_role.equals(SPCsnstants.user)) {
            FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, new HomeFragment()); // Replace with your fragment class
            ft.addToBackStack(null);
            ft.commit();
        } else if (is_role.equals(SPCsnstants.Admin)) {
            FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.admin_fragment_container, new HomeFragment()); // Replace with your fragment class
            ft.addToBackStack(null);
            ft.commit();
        }

    }

    @Override
    public void onPause() {
        backpressedlistener = null;
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        refreshFragment();
    }

}
