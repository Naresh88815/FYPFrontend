package com.application.expensemanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.expensemanager.R;
import com.application.expensemanager.model.ChooseFileOptionModel;

import java.util.List;

public class OptionsAdapter extends RecyclerView.Adapter<OptionsAdapter.ViewHolder> {

    private List<ChooseFileOptionModel> optionItems;
    private OnOptionClickListener onOptionClickListener;

    public OptionsAdapter(List<ChooseFileOptionModel> optionItems, OnOptionClickListener onOptionClickListener) {
        this.optionItems = optionItems;
        this.onOptionClickListener = onOptionClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_option_bottom_sheet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChooseFileOptionModel optionItem = optionItems.get(position);

        holder.textOption.setText(optionItem.getText());
        holder.imageOption.setImageResource(optionItem.getIconResId());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onOptionClickListener != null) {
                    onOptionClickListener.onOptionClick(optionItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return optionItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageOption;
        TextView textOption;

        ViewHolder(View itemView) {
            super(itemView);
            imageOption = itemView.findViewById(R.id.imageOption);
            textOption = itemView.findViewById(R.id.textOption);
        }
    }

    public interface OnOptionClickListener {
        void onOptionClick(ChooseFileOptionModel optionItem);
    }
}

