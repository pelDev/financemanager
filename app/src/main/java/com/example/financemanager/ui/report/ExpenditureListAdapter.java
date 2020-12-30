package com.example.financemanager.ui.report;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financemanager.R;
import com.example.financemanager.database.expense.Expenditure;

import java.util.Calendar;
import java.util.List;

public class ExpenditureListAdapter extends
        RecyclerView.Adapter<ExpenditureListAdapter.ViewHolder> {

    private int expenditureItemLayout;

    private List<Expenditure> mExpenditureList;

    public ExpenditureListAdapter(int layoutId) {
        expenditureItemLayout = layoutId;
    }

    public void setExpenditureList(List<Expenditure> expenditureList) {
        this.mExpenditureList = expenditureList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mExpenditureList == null ? 0 : mExpenditureList.size();
    }

    @NonNull
    @Override
    public ExpenditureListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(expenditureItemLayout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.expenseNameTextView.setText(mExpenditureList.get(position).getName());
        holder.expenseAmountTextView.setText(String.valueOf(mExpenditureList.get(position).getAmount()));
        String day = String.valueOf(mExpenditureList.get(position).getDay());
        String month = mExpenditureList.get(position).getMonth();
        String year = String.valueOf(mExpenditureList.get(position).getYear());
        holder.expenseDateTextView.setText(day + ", " + month + " " + year);
        String category = mExpenditureList.get(position).getCategory();
        switch (category) {
            case "Housing":
                holder.image.setImageResource(R.drawable.ic_housing);
                holder.image.setBackgroundColor(Color.parseColor("#393ab5"));
                break;
            case "Food":
                holder.image.setImageResource(R.drawable.ic_food);
                holder.image.setBackgroundColor(Color.parseColor("#ec5b22"));
                break;
            case "Recreation":
                holder.image.setImageResource(R.drawable.ic_recreation);
                holder.image.setBackgroundColor(Color.parseColor("#62b7d5"));
                break;
            case "Education":
                holder.image.setImageResource(R.drawable.ic_education);
                holder.image.setBackgroundColor(Color.parseColor("#FF0000"));
                break;
            case "Entertainment":
                holder.image.setImageResource(R.drawable.ic_entertainment);
                holder.image.setBackgroundColor(Color.parseColor("#782D2D"));
                break;
            case "Transportation":
                holder.image.setImageResource(R.drawable.ic_transport);
                holder.image.setBackgroundColor(Color.parseColor("#62b7d5"));
                break;
            case "Investment":
                holder.image.setImageResource(R.drawable.ic_investement);
                holder.image.setBackgroundColor(Color.parseColor("#09094C"));
                break;
            case "Technology":
                holder.image.setImageResource(R.drawable.ic_tech);
                holder.image.setBackgroundColor(Color.parseColor("#ec5b22"));
                break;
            case "Fashion":
                holder.image.setImageResource(R.drawable.ic_fashion);
                holder.image.setBackgroundColor(Color.parseColor("#12536A"));
                break;
            case "Others":
                holder.image.setImageResource(R.drawable.ic_others);
                holder.image.setBackgroundColor(Color.parseColor("#000000"));
                break;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView expenseNameTextView, expenseDateTextView, expenseAmountTextView;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            expenseNameTextView = itemView.findViewById(R.id.textView_expenseName);
            expenseDateTextView = itemView.findViewById(R.id.textView_expenseDate);
            expenseAmountTextView = itemView.findViewById(R.id.textView_amount);
            image = itemView.findViewById(R.id.expenditure_icon);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

    }

}
