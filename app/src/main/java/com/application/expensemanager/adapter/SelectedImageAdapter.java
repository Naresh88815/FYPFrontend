package com.application.expensemanager.adapter;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.application.expensemanager.R;
import com.application.expensemanager.activity.PdfWebViewActivity;
import com.application.expensemanager.utils.Constants;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.List;
import java.util.Objects;

public class SelectedImageAdapter extends RecyclerView.Adapter<SelectedImageAdapter.ImageViewHolder> {
    private List<Uri> imagesList;
    private View parentLayout;
    private View uploadImgBtn;
    private List<String> imageUrlList;
    private String whereFrom;
    Context context;

    public SelectedImageAdapter(Context context, List<Uri> imagesList, View parentLayout, View uploadImgBtn, List<String> imageUrlList, String whereFrom) {
        this.context = context;
        this.imagesList = imagesList;
        this.parentLayout = parentLayout;
        this.uploadImgBtn = uploadImgBtn;
        this.imageUrlList = imageUrlList;
        this.whereFrom = whereFrom;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.selected_image_preview, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        //Log.d("onBindImageView", "Image:  " + imagesList.get(position).toString() + "\nLength: " + imageUrlList.size() +"\n"+imageUrlList.get(position).toString() + "\nLength: " + imageUrlList.size());
        if (whereFrom.equals("storage") && position <= imagesList.size()) {
            Log.d("onBindImageView", "Storage Image:  " + imagesList.get(position).toString() + "\nLength: " + imagesList.size());
            Uri currentItem = imagesList.get(position);
            if (isUriPdfFile(currentItem)) {
                holder.PreviewImage.setImageResource(R.drawable.baseline_picture_as_pdf_24);
            } else {
                // Display images using Glide for non-PDF items
                Glide.with(holder.itemView.getContext())
                        .load(currentItem)
                        .into(holder.PreviewImage);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openItem(currentItem);
                }
            });
        } else if (whereFrom.equals("response") && position <= imageUrlList.size()) {
            Log.d("onBindImageView", "Server Image:  " + imageUrlList.get(position) + "\nLength: " + imageUrlList.size());
            String currentItem = imageUrlList.get(position);
            if (isStrinfPdfFile(currentItem)) {
                holder.PreviewImage.setImageResource(R.drawable.baseline_picture_as_pdf_24);
            } else {
                Glide.with(holder.itemView.getContext())
                        .load(currentItem)
                        .placeholder(R.drawable.img_placeholder)
                        .into(holder.PreviewImage);
            }
            holder.DeleteButton.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isStrinfPdfFile(currentItem)) {
                        Log.d("FileType", "FILE: "+currentItem);
                        openPdfInWebView(holder.itemView.getContext(), currentItem);
                    } else {
                        openServerImageViewer(currentItem);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (whereFrom.equals("response")) {
            if (imageUrlList == null) {
                return 0;
            }
            return imageUrlList.size();
        } else if (whereFrom.equals("storage")) {
            if (imagesList == null) {
                return 0;
            }
            return imagesList.size();
        }
        return imageUrlList.size();
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {

        ImageView PreviewImage;
        ImageView DeleteButton;

        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            PreviewImage = itemView.findViewById(R.id.PreviewImage);
            DeleteButton = itemView.findViewById(R.id.DeleteButton);
            DeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        imagesList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, imagesList.size());
                        if (imagesList.isEmpty()) {
                            parentLayout.setVisibility(View.GONE);
                            uploadImgBtn.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
        }
    }

    private boolean isUriPdfFile(Uri uri) {
        ContentResolver contentResolver = context.getContentResolver();
        String type = contentResolver.getType(uri);
        return type != null && type.startsWith("application/pdf");
    }

    private boolean isStrinfPdfFile(String fileName) {
        return fileName != null && fileName.toLowerCase().endsWith(".pdf");
    }


    private void openItem(Uri itemUri) {
        if (isUriPdfFile(itemUri)) {
            openPdfViewer(itemUri);
        } else {
            openImageViewer(itemUri);
        }
    }

    private void openPdfInWebView(Context context, String pdfUrl) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(pdfUrl));
        context.startActivity(browserIntent);
    }


    private void openPdfViewer(Uri pdfUri) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(pdfUri.toString()));
        context.startActivity(browserIntent);
    }

    private void openImageViewer(Uri imageUri) {
        // Implement your logic to open an image viewer
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(imageUri, "image/*");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);
    }

    public void openServerImageViewer(String imageUrl) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.FullScreenDialog);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_image_viewer, null);
        ImageView imageView = view.findViewById(R.id.imageView);

        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_placeholder)
                .into(imageView);

        builder.setView(view);
        AlertDialog dialog = builder.create();

        // Set click listener for the image to close the dialog
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // Set dialog window layout parameters
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(layoutParams);
        }

        dialog.show();
    }
}

