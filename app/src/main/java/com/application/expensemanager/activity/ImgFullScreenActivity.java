package com.application.expensemanager.activity;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.application.expensemanager.R;
import com.bumptech.glide.Glide;

public class ImgFullScreenActivity extends AppCompatActivity {
    ImageView fullscreenImage;
    String image, transferImage, r_billImage, r_transfer_image;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_fullscreen_layout);

        fullscreenImage = findViewById(R.id.fullscreenImage);

        // Initialize the context properly
        context = this;

        viewFullScreen();
    }

    private void viewFullScreen() {
        if (getIntent().hasExtra("billImage")) {
            image = getIntent().getStringExtra("billImage");
            Glide.with(context)
                    .load(image)
                    .placeholder(R.drawable.img_placeholder)
                    .into(fullscreenImage);

        } else if (getIntent().hasExtra("transferImage")) {
            transferImage = getIntent().getStringExtra("transferImage");
            Glide.with(context)
                    .load(transferImage)
                    .placeholder(R.drawable.img_placeholder)
                    .into(fullscreenImage);
        } else if (getIntent().hasExtra("r_billImage")) {
            r_billImage = getIntent().getStringExtra("r_billImage");
            Glide.with(context)
                    .load(r_billImage)
                    .placeholder(R.drawable.img_placeholder)
                    .into(fullscreenImage);
        } else if (getIntent().hasExtra("r_transfer_image")) {
            r_transfer_image = getIntent().getStringExtra("r_transfer_image");
            Glide.with(context)
                    .load(r_transfer_image)
                    .placeholder(R.drawable.img_placeholder)
                    .into(fullscreenImage);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
