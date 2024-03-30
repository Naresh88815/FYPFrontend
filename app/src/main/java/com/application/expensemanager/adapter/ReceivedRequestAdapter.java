package com.application.expensemanager.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.application.expensemanager.R;
import com.application.expensemanager.model.ReceivedRequestModel;
import com.application.expensemanager.model.RequestMoneyModel;
import com.application.expensemanager.utils.MyApplication;
import com.application.expensemanager.utils.SPCsnstants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReceivedRequestAdapter extends RecyclerView.Adapter<ReceivedRequestAdapter.MyViewHolder> {
    List<ReceivedRequestModel> receivedRequestModelList = new ArrayList<>();
    Context context;

    private List<ReceivedRequestModel> mFilteredData;

    public interface OnItemClickListener {
        void onItemClick(ReceivedRequestModel model);
    }

    private OnItemClickListener listener;
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public ReceivedRequestAdapter(List<ReceivedRequestModel> receivedRequestModelList, Context context) {
        this.receivedRequestModelList = receivedRequestModelList;
        this.context = context;
    }


    @NonNull
    @Override
    public ReceivedRequestAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.received_request_list_item, null);
        MyViewHolder viewHolder=new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ReceivedRequestAdapter.MyViewHolder holder, int position) {
        ReceivedRequestModel receivedRequestModel = receivedRequestModelList.get(position);
        if (MyApplication.mSp.getKey(SPCsnstants.super_user).equals("0")){
            holder.requestByTV.setVisibility(View.GONE);
            holder.layout.setWeightSum(8);
            holder.dateTV.setGravity(Gravity.RIGHT);

        } else if (MyApplication.mSp.getKey(SPCsnstants.super_user).equals("1")) {
            holder.requestByTV.setVisibility(View.VISIBLE);
            holder.layout.setWeightSum(9);
        }
        holder.requestByTV.setText(receivedRequestModel.getRequestByTV());
        holder.requestForTV.setText("("+receivedRequestModel.getRequestLabelTV()+")");
        holder.amountTV.setText(receivedRequestModel.getAmountTV());
        holder.headstV.setText(receivedRequestModel.getHeadsTV());

//        if (image_status) {
//            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.light_red_cardview);
//            holder.layout.setBackground(drawable);
//            holder.statusimg.setVisibility(View.VISIBLE);
//            holder.statusimg.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Toast.makeText(context, "Please Upload Image.", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }else{
//            holder.statusimg.setVisibility(View.GONE);
//            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.corner_cardview);
//            holder.layout.setBackground(drawable);
//        }


        String status = ""+receivedRequestModel.getStatusTV();
        holder.statusTV.setText(receivedRequestModel.getStatusTV());
        if (status.equals("Pending")) {
            int yellowColor = ContextCompat.getColor(context, R.color.dark_yellow);
            holder.statusTV.setTextColor(yellowColor);
        }else if (status.equals("Approved")) {
            int orangeColor = ContextCompat.getColor(context, R.color.orange);
            holder.statusTV.setTextColor(orangeColor);
        }
        else if (status.equals("Transferred")) {
            int yellowColor = ContextCompat.getColor(context, R.color.green);
            holder.statusTV.setTextColor(yellowColor);
        } else if (status.equals("Cancelled") || status.equals("Declined")) {
            int yellowColor = ContextCompat.getColor(context, R.color.red);
            holder.statusTV.setTextColor(yellowColor);
        }




        SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = receivedRequestModel.getDateTV(); // Assuming it's a String in the format "yyyy-MM-dd HH:mm:ss"
        try {
            Date date = inputDateFormat.parse(dateStr);
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = outputDateFormat.format(date);

            holder.dateTV.setText(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return receivedRequestModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView requestByTV;
        TextView requestForTV;
        TextView amountTV;
        TextView statusTV;
        TextView dateTV, headstV;
        LinearLayout layout;
        ImageView statusimg;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            requestByTV = itemView.findViewById(R.id.requestByTV);
            requestForTV = itemView.findViewById(R.id.requestForTV);
            amountTV = itemView.findViewById(R.id.amountTV);
            statusTV = itemView.findViewById(R.id.statusTV);
            dateTV= itemView.findViewById(R.id.dateTV);
            headstV= itemView.findViewById(R.id.headstv);
            layout= itemView.findViewById(R.id.layout);
            statusimg= itemView.findViewById(R.id.statusimg);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(receivedRequestModelList.get(position)); // Change here
                        }
                    }
                }
            });
        }
    }
    public void setFilter(List<ReceivedRequestModel> filteredData) {
        mFilteredData = new ArrayList<ReceivedRequestModel>();
        mFilteredData.addAll(filteredData);
        notifyDataSetChanged();
    }
}
