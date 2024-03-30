package com.application.expensemanager.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class ImageUploader {

    private static final String TAG = "ImageUploader";

    public static void uploadImageToServerVolley(Context context, Bitmap imageBitmap, String url) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();
            final String base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT);



            RequestQueue queue = Volley.newRequestQueue(context);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Handle the server response here
                            Log.d(TAG, "Image uploaded successfully!");
                            Toast.makeText(context, "Image uploaded successfully!", Toast.LENGTH_SHORT).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Handle the error response here
                            Log.e(TAG, "Error uploading image to server: " + error.toString());
                            Toast.makeText(context, "Error uploading image to server", Toast.LENGTH_SHORT).show();
                        }
                    }) {
                @Override
                public byte[] getBody() throws AuthFailureError {
                    return imageBytes;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("image", base64Image);
                    return headers;
                }
            };

            // Set a tag to the request for future cancellation if needed
            stringRequest.setTag(TAG);
            queue.add(stringRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
