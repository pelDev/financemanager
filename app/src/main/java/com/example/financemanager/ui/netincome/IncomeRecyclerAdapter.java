package com.example.financemanager.ui.netincome;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financemanager.R;
import com.example.financemanager.database.income.Income;

import java.util.List;

public class IncomeRecyclerAdapter extends RecyclerView.Adapter<IncomeRecyclerAdapter.ViewHolder> {

    private List<Income> mIncomes;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_income, parent, false);

        return new ViewHolder(view);
    }

    public void setIncomes(List<Income> incomes) {
        this.mIncomes = incomes;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.incomeAmount.setText(Integer.toString(mIncomes.get(position).getAmount()));
        String day = String.valueOf(mIncomes.get(position).getDay());
        String month = mIncomes.get(position).getMonth();
        String year = String.valueOf(mIncomes.get(position).getYear());
        holder.incomeDate.setText(day + ", " + month + " " + year);
    }

    @Override
    public int getItemCount() {
        return mIncomes != null ? mIncomes.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView incomeAmount, incomeDate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            incomeAmount = itemView.findViewById(R.id.text_income_amount);
            incomeDate = itemView.findViewById(R.id.text_income_date);
        }

    }
}
