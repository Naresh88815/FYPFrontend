package com.application.expensemanager.network;

import org.json.JSONObject;

public interface VolleyResponse {

    void onResponse(JSONObject json) throws Exception;
    void onResponse2(String url_type, JSONObject json) throws Exception;
}
