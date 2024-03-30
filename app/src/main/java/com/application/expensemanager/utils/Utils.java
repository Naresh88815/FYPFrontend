package com.application.expensemanager.utils;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.application.expensemanager.R;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import id.zelory.compressor.Compressor;


public class Utils {
    private static AlertDialog dialog;
    private static BottomSheetDialog folderListDialog, createFDialog;
    private static RecyclerView bd_RecyclerView;
    public static boolean is_in_cart = false;
    public static JSONObject json;
    public static String cc_name = "";
    public static String category_name_wishlist = "";
    public static int selected_book_id = 0;
    public static final String AutoFocus = "AutoFocus";
    public static final String UseFlash = "UseFlash";
    private static CardView createFolder, moreItemCard;
    private static TextView moreItemsVisible;
    private static Button addBookBtn;
    private static Boolean showAllFolderlist = false;


    public static boolean is_contain_integer(String input1) {
        boolean is_int = false;
        char[] chars = input1.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (char c : chars) {
            if (Character.isDigit(c)) {
                is_int = true;
            }
        }
        return is_int;
    }


    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String generateRandom10DigitNumber() {
        Random random = new Random();
        long min = 1000000000L; // Minimum 10-digit number
        long max = 9999999999L; // Maximum 10-digit number

        // Generate a random number within the specified range
        long random10DigitNumber = min + ((long) (random.nextDouble() * (max - min + 1)));

        return "" + random10DigitNumber;
    }

    public static void showProgressDialog(Context ctx, boolean cancelable) {
        ProgressBar progressBar;
        try {
            if (ctx != null && !Constants.isProgressDialogVisible) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                View v = LayoutInflater.from(ctx).inflate(R.layout.progressbar_layout, null, false);
                builder.setView(v);
                builder.setCancelable(cancelable);
                dialog = builder.create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                try {
                    dialog.show();
                    progressBar = dialog.findViewById(R.id.progressBar);
                    Constants.isProgressDialogVisible = true;
                } catch (Exception e) {
                    // Handle exception
                }
            }
        } catch (Exception e) {
            // Handle exception
        }
    }

    public static void dismisProgressDialog() {
        try {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
                Constants.isProgressDialogVisible = false;
            }
        } catch (Exception e) {
            // Handle exception
        }
    }



    public static String convertTimeStamp(String timestamp_str) {
        long timestamp = Long.parseLong(timestamp_str);

        Date date = new Date(timestamp * 1000L);

        // Define a SimpleDateFormat pattern for the desired datetime format
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a, dd MMM", Locale.ENGLISH);

        // Format the Date object to a string in the desired format
        String formattedDateTime = sdf.format(date);


        return formattedDateTime;
    }

    public static String formatNumber(double number) {
        String result;
        if (number >= 1_000_000) {
            result = String.format("%.1fm", number / 1_000_000);
        } else if (number >= 1_000) {
            result = String.format("%.1fk", number / 1_000);
        } else {
            result = String.format("%.0f", number);
        }
        return result;
    }

    public static String getOffPEr(String mrp, String selling_price) {
        int mrp_db = Integer.parseInt(mrp);
        int selling_db = Integer.parseInt(selling_price);
        int discount_db = mrp_db - selling_db;
        int per_discount = (discount_db / mrp_db) * 100;

        return new DecimalFormat("##").format(per_discount) + "% off";

    }


    public static boolean isAppMinimized() {
        ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(appProcessInfo);
        return appProcessInfo.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
    }


    public static String encryptedValue(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        int inputLen = input.length();
        int randKey = (int) (Math.random() * 9 + 1);
        int[] inputChr = new int[inputLen];
        for (int i = 0; i < inputLen; i++) {
            inputChr[i] = (int) input.charAt(i) - randKey;
        }
        StringBuilder sb = new StringBuilder();
        for (int i : inputChr) {
            sb.append(i).append("a");
        }
        sb.append((int) (String.valueOf(randKey).charAt(0)) + 50);
        return sb.toString();

    }

    public static String decryptedValue(String input) {
        String[] inputArr = input.split("a");
        int inputLen = inputArr.length - 1;
        // int randKey = (int) inputArr[inputLen].charAt(0) - 50;
        int val = Integer.parseInt(inputArr[inputLen]) - 50;
        String randKey = String.valueOf((char) val);

        int[] inputChr = new int[inputLen];
        for (int i = 0; i < inputLen; i++) {
            inputChr[i] = Integer.parseInt(inputArr[i]) + Integer.valueOf(randKey);
        }
        StringBuilder sb = new StringBuilder();
        for (int i : inputChr) {
            sb.append((char) (i));
        }
        return sb.toString();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                //  handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {


                final String id = DocumentsContract.getDocumentId(uri);
                if (!TextUtils.isEmpty(id)) {
                    if (id.startsWith("raw:")) {
                        return id.replaceFirst("raw:", "");
                    }
                    try {
                        final Uri contentUri = ContentUris.withAppendedId(
                                Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                        return getDataColumn(context, contentUri, null, null);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }


    public static Date getDateFromSTR(String date_Str) {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy");
        try {
            date = format.parse(date_Str);
            System.out.println(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }


    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static Bitmap CompressImage(Bitmap bmp) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 15, byteArrayOutputStream);

        // Convert the compressed ByteArrayOutputStream to a compressed Bitmap
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        Bitmap compressedBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        return compressedBitmap;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static Bitmap getBitmapFromUri(Context context, Uri uri) {
        if (uri == null) {
            return null;
        }

        try {
            ContentResolver resolver = context.getContentResolver();
            InputStream inputStream = resolver.openInputStream(uri);
            if (inputStream != null) {
                return BitmapFactory.decodeStream(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }

        } catch (IllegalArgumentException e) {
            Toast.makeText(context, "Failed to fetch the file.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();

        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

//    public static void checkForAppUpdate(Context context) {
//        if (Utils.isNetworkAvailable(context)) {
////            String url = Constants.IN_APP_UPDATE;
//            //String appVersion = ""+getAppVersion(context);
//            String appVersion = ""+getAppVersion(context);
//
//            Map<String, String> params = new HashMap<>();
//            params.put("type", "inapp_update");
//            params.put("app_version", appVersion);
//
//            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,
//                    new JSONObject(params),
//                    new Response.Listener<JSONObject>() {
//                        @Override
//                        public void onResponse(JSONObject response) {
//                            try {
//                                boolean inAppUpdateStatus = response.getBoolean("inapp_update_status");
//                                String updateTitle = response.getString("update_title");
//                                String updateDescription = response.getString("update_description");
//                                String updateAppLink = response.getString("update_app_link");
//                                String forceUpdate = response.getString("force_update");
//
//                                // Handle the update status and show dialog accordingly
//                                if (inAppUpdateStatus) {
//                                    showUpdateDialog(context, updateTitle, updateDescription, updateAppLink, forceUpdate);
//                                }
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                                Log.e("JSONError", "Error parsing in-app update response");
//                            }
//                        }
//                    },
//                    new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            Toast.makeText(context, "Error checking for app update", Toast.LENGTH_SHORT).show();
//                            Log.e("VolleyError", error.toString());
//                        }
//                    });
//
//            Volley.newRequestQueue(context).add(jsonObjectRequest);
//        } else {
//            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
//        }
//    }

    private static void showUpdateDialog(Context context, String title, String description, final String appLink, String forceUpdate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(description);
        builder.setCancelable(!"1".equals(forceUpdate));

        builder.setPositiveButton("Update Now", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Open the update link in the browser
                openLinkInBrowser(context, appLink);
            }
        });

        if (!"1".equals(forceUpdate)) {
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private static void openLinkInBrowser(Context context, String link) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        context.startActivity(browserIntent);
    }


    public static boolean isUpiIdValid(String upiId) {
        String upiPattern = "^[a-zA-Z0-9.-]{2,256}@[a-zA-Z][a-zA-Z]{2,64}$";
        Pattern pattern = Pattern.compile(upiPattern);
        Matcher matcher = pattern.matcher(upiId);
        return matcher.matches();
    }


    public static boolean isValidAccountNumber(String accountNumber) {
        return accountNumber != null && !accountNumber.isEmpty() && accountNumber.matches("\\d+");
    }

    public static boolean isValidIFSCCode(String ifscCode) {
        return ifscCode != null && ifscCode.length() == 11 && ifscCode.matches("[A-Z|a-z]{4}[0][\\d]{6}");
    }

}
