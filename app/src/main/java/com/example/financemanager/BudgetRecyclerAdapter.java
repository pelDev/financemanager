package com.example.financemanager;

import android.content.ContentValues;
import android.content.Loader;
import android.app.LoaderManager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
//import androidx.loader.app.LoaderManager;
//import androidx.loader.content.Loader;
import android.content.CursorLoader;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financemanager.FinanceManagerDatabaseContract.BudgetInfoEntry;
import com.example.financemanager.FinanceManagerDatabaseContract.ExpenditureInfoEntry;

import java.text.NumberFormat;

public class BudgetRecyclerAdapter extends RecyclerView.Adapter<BudgetRecyclerAdapter.ViewHolder> {

    private final Context mContext;
    //private final List<NoteInfo> mNotes;

    private Cursor mCursor;
    private final LayoutInflater mLayoutInflater;
    private int mIdPos;
    private int mBudgetCategoryPos;
    private int mBudgetAmountPos;
    private int mBudgetAmountLeftPos;
    private int mBudgetAmount;
    private double mAmountSpent;

    public BudgetRecyclerAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
        //mNotes = notes;
        mLayoutInflater = LayoutInflater.from(mContext);
        populateColumnPositions();
    }

    private void populateColumnPositions() {
        if(mCursor == null) {
            return;
        } else {
            // get the column position for budget category in the table
            mBudgetCategoryPos = mCursor.getColumnIndex(BudgetInfoEntry.COLUMN_BUDGET_CATEGORY);
            // get the column position for budget amount from table
            mBudgetAmountPos = mCursor.getColumnIndex(BudgetInfoEntry.COLUMN_BUDGET_AMOUNT);
            // get column position for budget amount left
            mBudgetAmountLeftPos = mCursor.getColumnIndex(BudgetInfoEntry.COLUMN_BUDGET_AMOUNT_SPENT);
            // get column position for the unique id from table
            mIdPos = mCursor.getColumnIndex(BudgetInfoEntry._ID);
        }
    }

    public void changeCursor(Cursor cursor) {
        if(mCursor != null)
            mCursor.close();
        mCursor = cursor;
        // populate the column for the new cursor
        populateColumnPositions();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BudgetRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.item_budget, parent, false);
        return new ViewHolder(itemView);
    }

//    @RequiresApi(api = Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // move the cursor position as required by the on bind view holder
        mCursor.moveToPosition(position);
        // get value for course, title and id
        String budgetCategory = mCursor.getString(mBudgetCategoryPos);
        mBudgetAmount = Integer.parseInt(mCursor.getString(mBudgetAmountPos));
        Long amnt = new Long(mBudgetAmount);
        NumberFormat myFormat = NumberFormat.getInstance();
        myFormat.setGroupingUsed(true);
        String n = myFormat.format(amnt);
        int id = mCursor.getInt(mIdPos);

        holder.mBudgetCategoryName.setText(capitalize(budgetCategory));
        holder.mTextBudgetAmount.setText(n);
        switch (budgetCategory) {
            case "housing":
                holder.mBudgetIcon.setImageResource(R.drawable.ic_housing);
                holder.mBudgetIcon.setBackgroundColor(Color.parseColor("#393ab5"));
                getAmountSpent("housing");
                break;
            case "food":
                holder.mBudgetIcon.setImageResource(R.drawable.ic_food);
                holder.mBudgetIcon.setBackgroundColor(Color.parseColor("#ec5b22"));
                break;
            case "recreation":
                holder.mBudgetIcon.setImageResource(R.drawable.ic_recreation);
                holder.mBudgetIcon.setBackgroundColor(Color.parseColor("#62b7d5"));
                break;
            case "education":
                holder.mBudgetIcon.setImageResource(R.drawable.ic_education);
                holder.mBudgetIcon.setBackgroundColor(Color.parseColor("#FF0000"));
                break;
            case "entertainment":
                holder.mBudgetIcon.setImageResource(R.drawable.ic_entertainment);
                holder.mBudgetIcon.setBackgroundColor(Color.parseColor("#782D2D"));
                holder.mProgressBar.setProgress(50, true);
                break;
            case "transportation":
                holder.mBudgetIcon.setImageResource(R.drawable.ic_transport);
                holder.mBudgetIcon.setBackgroundColor(Color.parseColor("#62b7d5"));
                break;
            case "investment":
                holder.mBudgetIcon.setImageResource(R.drawable.ic_investement);
                holder.mBudgetIcon.setBackgroundColor(Color.parseColor("#09094C"));
                break;
            case "technology":
                holder.mBudgetIcon.setImageResource(R.drawable.ic_tech);
                holder.mBudgetIcon.setBackgroundColor(Color.parseColor("#ec5b22"));
                break;
            case "fashion":
                holder.mBudgetIcon.setImageResource(R.drawable.ic_fashion);
                holder.mBudgetIcon.setBackgroundColor(Color.parseColor("#12536A"));
                break;
            case "others":
                holder.mBudgetIcon.setImageResource(R.drawable.ic_others);
                holder.mBudgetIcon.setBackgroundColor(Color.parseColor("#000000"));
                break;
        }

        int progress = getBudgetProgress();
        holder.mProgressBar.setProgress(progress, true);
        holder.mTextBudgetAmountLeft.setText(getAmountLeft());
        holder.mId = id;
    }

    private void getAmountSpent(String id) {

    }

    private String getAmountLeft() {
        double amountLeft = mBudgetAmount - mAmountSpent;
        return "Left: " + amountLeft;
    }

    private int getBudgetProgress() {
        mAmountSpent = Double.parseDouble(mCursor.getString(mBudgetAmountLeftPos));
        double budgetAmount = mBudgetAmount;
        double percent = (mAmountSpent / budgetAmount) * 100.0;
        return (int) Math.round(percent);
    }

    private String capitalize(String str) {
        if (str.isEmpty() || str == null) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }


    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView mBudgetCategoryName;
        public final TextView mTextBudgetAmount;
        public final TextView mTextBudgetAmountLeft;
        public final ImageView mBudgetIcon;
        public final ProgressBar mProgressBar;
        public int mId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mProgressBar = itemView.findViewById(R.id.progressBarBudget);
            mBudgetCategoryName = (TextView) itemView.findViewById(R.id.budget_category_view);
            mTextBudgetAmount = (TextView) itemView.findViewById(R.id.budget_amount);
            mTextBudgetAmountLeft = (TextView) itemView.findViewById(R.id.budget_amount_left);
            mBudgetIcon = (ImageView) itemView.findViewById(R.id.budget_icon);
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(mContext, AddExpenseActivity.class);
//                    intent.putExtra(AddExpenseActivity.EXPENDITURE_ID, mId);
//                    mContext.startActivity(intent);
//                }
//            });
        }
    }
}

