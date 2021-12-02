package com.example.financemanager.ui.budget;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financemanager.R;
import com.example.financemanager.database.budget.Budget;

import java.util.List;

public class BudgetListAdapter extends
        RecyclerView.Adapter<BudgetListAdapter.ViewHolder> {

    private int budgetItemLayout;

    private List<Budget> mBudgetList;

    public BudgetListAdapter(int layoutId) {
        budgetItemLayout = layoutId;
    }

    public void setBudgetList(List<Budget> budgetList) {
        this.mBudgetList = budgetList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mBudgetList == null ? 0 : mBudgetList.size();
    }

    @NonNull
    @Override
    public BudgetListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(budgetItemLayout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.budgetCategoryTextView.setText(mBudgetList.get(position).getCategory());
        double amount = (double) mBudgetList.get(position).getAmount();
        holder.budgetAmountTextView.setText(String.valueOf(amount));
        double amountSpent = mBudgetList.get(position).getAmountSpent();
        double progress = (amountSpent / amount) * 100;
        if (progress > 100) {
            progress = 100;
        }
        double amountLeft = amount - amountSpent;
        holder.progressBar.setProgress((int) Math.round(progress));
        holder.amountLeftTextView.setText("Left: " + amountLeft);
        String category = mBudgetList.get(position).getCategory();
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

        TextView budgetCategoryTextView, amountLeftTextView, budgetAmountTextView;
        ProgressBar progressBar;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            budgetCategoryTextView = itemView.findViewById(R.id.budget_category_view);
            amountLeftTextView = itemView.findViewById(R.id.budget_amount_left);
            budgetAmountTextView = itemView.findViewById(R.id.budget_amount);
            progressBar = itemView.findViewById(R.id.progressBarBudget);
            image = itemView.findViewById(R.id.budget_icon);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

    }

}
