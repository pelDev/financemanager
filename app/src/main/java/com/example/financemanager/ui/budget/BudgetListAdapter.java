package com.example.financemanager.ui.budget;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView budgetCategoryTextView, amountLeftTextView, budgetAmountTextView;
        ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            budgetCategoryTextView = itemView.findViewById(R.id.budget_category_view);
            amountLeftTextView = itemView.findViewById(R.id.budget_amount_left);
            budgetAmountTextView = itemView.findViewById(R.id.budget_amount);
            progressBar = itemView.findViewById(R.id.progressBarBudget);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

    }

}
