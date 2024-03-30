//package com.application.expensemanager.adapter;
//
//import android.content.Context;
//import android.graphics.Color;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.core.content.ContextCompat;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.application.expensemanager.R;
//import com.application.expensemanager.model.RequestMoneyModel;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//public class RequestMoneyAdapter extends RecyclerView.Adapter<RequestMoneyAdapter.MyViewHolder>{
//    List<RequestMoneyModel> requestMoneyModelList = new ArrayList<>();
//    Context context;
//    private List<RequestMoneyModel> mFilteredData;
//
//    public interface OnItemClickListener {
//        void onItemClick(RequestMoneyModel model);
//    }
//
//    private OnItemClickListener listener;
//
//    public RequestMoneyAdapter(List<RequestMoneyModel> requestMoneyModelList, Context context) {
//        this.requestMoneyModelList = requestMoneyModelList;
//        this.context = context;
//    }
//
//    public void setOnItemClickListener(OnItemClickListener listener) {
//        this.listener = listener;
//    }
//
//
//    @NonNull
//    @Override
//    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_for_money_list_item,null);
//        MyViewHolder viewHolder=new MyViewHolder(view);
//        return viewHolder;
//    }
//
//    @Override
//    public void onBindViewHolder(RequestMoneyAdapter.MyViewHolder holder, int position) {
//        RequestMoneyModel requestMoneyModel = requestMoneyModelList.get(position);
//        String status = ""+requestMoneyModel.getStatusTV();
//
//        holder.requestForTV.setText("("+requestMoneyModel.getRequestLabelTV()+")");
//        holder.headsTv.setText(requestMoneyModel.getHeadsTV());
//        holder.amountTV.setText(requestMoneyModel.getAmountTV());
//        holder.statusTV.setText(requestMoneyModel.getStatusTV());
//
//        if (status.equals("Pending")) {
//            int yellowColor = ContextCompat.getColor(context, R.color.dark_yellow);
//            holder.statusTV.setTextColor(yellowColor);
//        } else if (status.equals("Approved")) {
//            int yellowColor = ContextCompat.getColor(context, R.color.orange);
//            holder.statusTV.setTextColor(yellowColor);
//        }
//        else if (status.equals("Transferred")) {
//            int yellowColor = ContextCompat.getColor(context, R.color.green);
//            holder.statusTV.setTextColor(yellowColor);
//        } else if (status.equals("Cancelled") || status.equals("Declined")) {
//            int yellowColor = ContextCompat.getColor(context, R.color.red);
//            holder.statusTV.setTextColor(yellowColor);
//        }
//
//        SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String dateStr = requestMoneyModel.getDateTV(); // Assuming it's a String in the format "yyyy-MM-dd HH:mm:ss"
//
//        try {
//            Date date = inputDateFormat.parse(dateStr);
//
//            SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//            String formattedDate = outputDateFormat.format(date);
//
//            holder.dateTV.setText(formattedDate);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return requestMoneyModelList.size();
//    }
//
//    public class MyViewHolder extends RecyclerView.ViewHolder{
//        TextView requestForTV,headsTv;
//        TextView amountTV;
//        TextView statusTV;
//        TextView dateTV;
//
//        public MyViewHolder(@NonNull View itemView) {
//            super(itemView);
//            requestForTV = itemView.findViewById(R.id.requestForTV);
//            amountTV = itemView.findViewById(R.id.amountTV);
//            statusTV = itemView.findViewById(R.id.statusTV);
//            dateTV = itemView. findViewById(R.id.dateTV);
//            headsTv = itemView. findViewById(R.id.headstv);
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (listener != null) {
//                        int position = getAdapterPosition();
//                        if (position != RecyclerView.NO_POSITION) {
//                            listener.onItemClick(requestMoneyModelList.get(position)); // Change here
//                        }
//                    }
//                }
//            });
//        }
//    }
//
//    public void setFilter(List<RequestMoneyModel> filteredData) {
//        mFilteredData = new ArrayList<>();
//        mFilteredData.addAll(filteredData);
//        notifyDataSetChanged();
//    }
//}
