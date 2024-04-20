package com.application.expensemanager.utils;

import android.app.Application;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.application.expensemanager.Storage.MySharedPreferences;
import com.application.expensemanager.network.Network;

public class MyApplication extends Application {


    protected static MyApplication instance;

    public static MySharedPreferences mSp;
    private static RequestQueue mRequest;

    public static Network apinetwork;
    public MyApplication() {
        super();
        instance = this;
    }

    public static MyApplication get() {
        return instance;
    }
    //private static Preferences instance;
    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSp = MySharedPreferences.getInstance(this);
        apinetwork = Network.getInstance(this);
        mRequest= Volley.newRequestQueue(this);
    }

    public RequestQueue getRequestQueue(){
        return mRequest;
    }









}
