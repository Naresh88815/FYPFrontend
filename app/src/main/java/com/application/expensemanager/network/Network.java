package com.application.expensemanager.network;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.application.expensemanager.activity.LoginActivity;
import com.application.expensemanager.utils.Constants;
import com.application.expensemanager.utils.MyApplication;
import com.application.expensemanager.utils.SPCsnstants;
import com.application.expensemanager.utils.Utils;
//import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Network {
    public static Network network;
    Context context;
//    List<NameValuePair> pairs;


    public Network(Context context) {
        this.context = context;
//        pairs = new ArrayList<>();
    }

    public static Network getInstance(Context context) {
        if (network == null)
            network = new Network(context);
        return network;
    }


    public void requestWithJsonObject(final String url, HashMap<String, String> params, final VolleyResponse vr, String type) {
        params.put("unique_device_id", Constants.getUniqueId(context));
        params.put("platform","android");
        params.put("app_version", Constants.getAppVersion(context));
        params.put("android_version", Constants.getAndroidVersion());
        params.put("emp_id", MyApplication.mSp.getKey(SPCsnstants.id));
        if (params.size() != 0) {
            for (String key : params.keySet()) {
                if (!key.equals("platform") & !key.equals("app_version") &  !key.equals("unique_device_id") &  !key.equals("app_status") & !key.equals("json")) {
                    params.put(key,params.get(key));
                } else {
                    Log.d("sbparams_prms", params.get(key));
                    params.put(key,params.get(key));
                }
            }
        }
        Log.d("url_with_params", url + "\nparams=" + params.toString());

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("URL_MAIN ", url + ",Response= " + response);
                            JSONObject jsonObject = new JSONObject(response);
                            if (type.equals("")) {
                                vr.onResponse(jsonObject);
                            } else {
                                vr.onResponse2(type, jsonObject);
                            }
                        } catch (Exception e) {
                            Log.d("catch_error", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.dismisProgressDialog();

                if (error instanceof TimeoutError) {
                    Log.d("ErrorType", "TimeoutError: " + error.toString());
                } else if (error instanceof NetworkError) {
                    Log.d("ErrorType", "NetworkError: " + error.toString());
                } else {
                    Log.d("ErrorType", "OtherError: " + error.toString());
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                return headers;
            }
        };

        new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        MyApplication.getInstance().getRequestQueue().add(request);
    }

}