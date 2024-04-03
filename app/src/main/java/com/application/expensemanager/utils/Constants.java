package com.application.expensemanager.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * Created by prateek on 18-06-2015.
 */
public class Constants {
    public static boolean is_live = true;
    public static boolean isProgressDialogVisible = false;
    public static String BASE_URL = "http://192.168.100.23:80/";
    public static String END_POINT = BASE_URL+"api/";
    public static String LOGIN = END_POINT+"login";
    public static String LOGOUT = END_POINT+"logout";
    public static String UPDATE_BANK_DETAILS = END_POINT+"login.php";
    public static String ADD_LABEL =END_POINT +  "exp_label";
    public static String ADD_HEAD =END_POINT +  "exp_head";
    public static String ADD_USER =END_POINT +  "user";
    public static String VIEW_LABEL = END_POINT + "exp_label";
    public static String CREATE_EXPENSE = END_POINT + "expenses";
    public static String VIEW_EXPENSE = END_POINT+ "expenses";
    public static String EXPENSE_STATUS_UPDATE = END_POINT + "update_status";
    public static String EXPENSE_HEAD = END_POINT + "exp_head";
    public static String BANKS_NAME = END_POINT + "v1/exp_expenses.php";
    public static String EXPENSE_DETAIL = END_POINT + "expenses";
    public static String COUNT_EXPENSE = END_POINT + "expense_summary";
    public static String EXP_EXPENSE = END_POINT + "expenses";
    public static String publicIp = "0";

    public static String getAppVersionName(Context context) {
        String appVersionName = "";
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            appVersionName = packageInfo.versionName;
        } catch (Exception e) {
            appVersionName = "";
        }
        return appVersionName;
    }


    public static String getAppVersion(Context context) {
        String app_version = "";
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            app_version = String.valueOf(packageInfo.versionCode);
        } catch (Exception e) {
            app_version = "";
        }
        return app_version;
    }

    public static String getAndroidVersion() {
        String release = Build.VERSION.RELEASE;
        int sdkVersion = Build.VERSION.SDK_INT;
        return release;
    }

    private static String uniqueID = null;
    private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";

    public synchronized static String getUniqueId(Context context) {
        String androidId = "";
        try {
            androidId = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            androidId = "";
        }

        return androidId;
    }

    public static class GetPublicIP extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            String publicIP = "";
            try {
                java.util.Scanner s = new java.util.Scanner(
                        new URL(
                                "https://api.ipify.org")
                                .openStream(), "UTF-8")
                        .useDelimiter("\\A");
                publicIP = s.next();
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }

            return publicIP;
        }

        @Override
        protected void onPostExecute(String publicIp) {
            super.onPostExecute(publicIp);
            Constants.publicIp = publicIp;
            Log.e("PublicIP", publicIp + "");
        }
    }

    static String[] RoughArray;

    public static String getcustomer_id(Context activity) {
        String customerid = "";
        try {
            customerid = activity.getSharedPreferences("custom_detail", 0).getString("c_id", "0");
        } catch (Exception e) {
            customerid = "";
        }
        return customerid;
    }
}
