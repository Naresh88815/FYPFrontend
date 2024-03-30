package com.application.expensemanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.expensemanager.R;
import com.application.expensemanager.model.ExpenseLabelModel;

import java.util.ArrayList;
import java.util.List;

public class ExpenseLabelAdapter extends RecyclerView.Adapter<ExpenseLabelAdapter.MyViewHolder> {

    List <ExpenseLabelModel> modelList = new ArrayList<>();

    private List<ExpenseLabelModel> mFilteredData;
    Context context;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(ExpenseLabelModel model);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
    public ExpenseLabelAdapter(List<ExpenseLabelModel> modelList, Context context){
        this.context = context;
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public ExpenseLabelAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_label_item,null);
        ExpenseLabelAdapter.MyViewHolder viewHolder= new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseLabelAdapter.MyViewHolder holder, int position) {
        ExpenseLabelModel expenseLabelModel = modelList.get(position);

        holder.expenseLabel.setText(expenseLabelModel.getExpenseLabel());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClick(modelList.get(position));
                }
            }
        });



    }

    @Override
    public int getItemCount() {
        return  modelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView expenseLabel;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            expenseLabel= itemView.findViewById(R.id.expenseLabel);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mListener.onItemClick(modelList.get(position));
                        }
                    }
                }
            });


        }
    }
    public void setFilter(List<ExpenseLabelModel> filteredData) {
        mFilteredData = new ArrayList<>();
        mFilteredData.addAll(filteredData);
        notifyDataSetChanged();
    }
}
