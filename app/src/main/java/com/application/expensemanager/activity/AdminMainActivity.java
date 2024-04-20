package com.application.expensemanager.activity;

import static com.application.expensemanager.utils.MyApplication.apinetwork;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.application.expensemanager.R;
import com.application.expensemanager.fragments.AdminHomeFragment;
import com.application.expensemanager.fragments.HomeFragment;
import com.application.expensemanager.fragments.ReceivedRequest;
import com.application.expensemanager.fragments.UserProfile;
import com.application.expensemanager.network.VolleyResponse;
import com.application.expensemanager.utils.Constants;
import com.application.expensemanager.utils.MyApplication;
import com.application.expensemanager.utils.SPCsnstants;
import com.application.expensemanager.utils.Utils;
import com.dcastalia.localappupdate.DownloadApk;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminMainActivity extends AppCompatActivity {
    public static FrameLayout admin_fragment_container;
    public static BottomNavigationView adminBottomNavigationView;
    private long lastBackPressTime = 0;

    RelativeLayout topBarLayout;
    public static TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_main_activity);
        initView();
        selectedtab();
        openBottomNavigation();
    }

    private void selectedtab(){
        if (MyApplication.mSp.getKey(SPCsnstants.super_user).equals("0")) {
            Log.d("super_user_1", MyApplication.mSp.getKey(SPCsnstants.super_user));
            // Hide navigate_dash_admin menu item if super_user is 0
            Menu menu = adminBottomNavigationView.getMenu();
            MenuItem dashAdminMenuItem = menu.findItem(R.id.navigate_dash_admin);
            dashAdminMenuItem.setVisible(false);
            adminBottomNavigationView.setSelectedItemId(R.id.navigate_home_admin);
        }
        else {
            Log.d("super_user_2", MyApplication.mSp.getKey(SPCsnstants.super_user));
            Menu menu = adminBottomNavigationView.getMenu();
            MenuItem dashAdminMenuItem = menu.findItem(R.id.navigate_dash_admin);
            dashAdminMenuItem.setVisible(true);

            // Automatically select the navigate_dash_admin menu item
            adminBottomNavigationView.setSelectedItemId(R.id.navigate_dash_admin);
        }
    }

    private void getIntentData() {
        if (MyApplication.mSp.getKey(SPCsnstants.super_user).equals("0")) {
            Log.d("super_user_1", MyApplication.mSp.getKey(SPCsnstants.super_user));
            getSupportFragmentManager().beginTransaction()
                    .replace(admin_fragment_container.getId(), new HomeFragment())
                    .commit();
        } else {
            Log.d("super_user_2", MyApplication.mSp.getKey(SPCsnstants.super_user));
            getSupportFragmentManager().beginTransaction()
                    .replace(admin_fragment_container.getId(), new AdminHomeFragment())
                    .commit();
        }
    }

    private void setprefences() {
        MyApplication.mSp.setKey(SPCsnstants.IS_screen, SPCsnstants.Admin);
    }

    public void initView() {
        topBarLayout = findViewById(R.id.top_bar_layout);
        title = findViewById(R.id.title);
        adminBottomNavigationView = findViewById(R.id.adminBottomNavigation);
        ColorStateList colorStateList = getResources().getColorStateList(R.color.bottom_nav_icon_colors);
        //  ColorStateList colorbgList = getResources().getColorStateList(R.color.white);
        adminBottomNavigationView.setItemIconTintList(colorStateList);

// Set color state list for item icons
        ColorStateList iconColorStateList = getResources().getColorStateList(R.color.bottom_nav_icon_colors);
        adminBottomNavigationView.setItemIconTintList(iconColorStateList);

// Set color state list for item text
        ColorStateList textColorStateList = getResources().getColorStateList(R.color.bottom_nav_text_colors);
        adminBottomNavigationView.setItemTextColor(textColorStateList);

        int selectedItemIndicatorColor = getResources().getColor(R.color.white);
        adminBottomNavigationView.setItemBackgroundResource(R.drawable.selected_indicator_background);
        adminBottomNavigationView.isItemActiveIndicatorEnabled();
        ColorStateList colorindecator = getResources().getColorStateList(R.color.mint_green);

        adminBottomNavigationView.setItemActiveIndicatorColor(colorindecator);

        Drawable selectedItemIndicatorDrawable = new GradientDrawable();
        ((GradientDrawable) selectedItemIndicatorDrawable).setColor(selectedItemIndicatorColor);

        adminBottomNavigationView.setItemBackground(selectedItemIndicatorDrawable);
        admin_fragment_container = findViewById(R.id.admin_fragment_container);


        admin_fragment_container = findViewById(R.id.admin_fragment_container);

    }

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastBackPressTime < 2000) {
            super.onBackPressed();
            finish();
        } else {
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            lastBackPressTime = currentTime;
        }

    }

    private void openBottomNavigation(){
        selectedtab();
        adminBottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;


                        if (item.getItemId() == R.id.navigate_dash_admin) {
                            selectedFragment = new AdminHomeFragment();
                            title.setText("Expense Manager");
//                           getProfileData();

                        } else if (item.getItemId() == R.id.navigate_home_admin) {
                            setprefences();
                            selectedFragment = new HomeFragment();
                            title.setText("Expense Manager");
//                           getProfileData();
                        } else if (item.getItemId() == R.id.navigate_received_requests) {
                            selectedFragment = new ReceivedRequest();
                            title.setText("Expense Manager");
//                            getProfileData();
                        } else if (item.getItemId() == R.id.navigate_admin_profile) {
                            selectedFragment = new UserProfile();
                            title.setText("Profile");
//                            getProfileData();
                        }
//                        }

                        if (selectedFragment != null) {
                            topBarLayout.setVisibility(View.VISIBLE);
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(admin_fragment_container.getId(), selectedFragment);
                            fragmentTransaction.commit();
                            return true;
                        }
                        return false;
                    }
                }
        );
        getIntentData();
    }

    @Override
    protected void onResume() {

        super.onResume();
    }


}