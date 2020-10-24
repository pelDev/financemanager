package com.example.financemanager;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
    private int mBudgetDayPos;
    private int mBudgetAmountPos;
    private int mBudgetMonthPos;
    private int mBudgetYearPos;
    private String mExpenditureDay;
    private String mExpenditureMonth;
    private String mExpenditureYear;

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
            // get column indexes from cursor

            // get the column position for budget category in the table
            mBudgetCategoryPos = mCursor.getColumnIndex(BudgetInfoEntry.COLUMN_BUDGET_CATEGORY);
            // get the column position for budget day from table
            mBudgetDayPos = mCursor.getColumnIndex(BudgetInfoEntry.COLUMN_BUDGET_DAY);
            // get the column position for budget month from table
            mBudgetMonthPos = mCursor.getColumnIndex(BudgetInfoEntry.COLUMN_BUDGET_MONTH);
            // get the column position for budget year from table
            mBudgetYearPos = mCursor.getColumnIndex(BudgetInfoEntry.COLUMN_BUDGET_YEAR);
            // get the column position for budget amount from table
            mBudgetAmountPos = mCursor.getColumnIndex(BudgetInfoEntry.COLUMN_BUDGET_AMOUNT);
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
        View itemView = mLayoutInflater.inflate(R.layout.item_expenditure, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetRecyclerAdapter.ViewHolder holder, int position) {
        // move the cursor position as required by the on bind view holder
        mCursor.moveToPosition(position);
        // get value for course, title and id
        String expenditureName = mCursor.getString(mBudgetCategoryPos);
        mExpenditureDay = mCursor.getString(mBudgetDayPos);
        mExpenditureMonth = mCursor.getString(mBudgetMonthPos);
        mExpenditureYear = mCursor.getString(mBudgetYearPos);
        int expenditureAmount = mCursor.getInt(mBudgetAmountPos);
        Long amnt = new Long(expenditureAmount);
        NumberFormat myFormat = NumberFormat.getInstance();
        myFormat.setGroupingUsed(true);
        String n = myFormat.format(amnt);
        String expenditureId = mCursor.getString(mExpenditureIdPos);
        int id = mCursor.getInt(mIdPos);
        Log.d("Expense", "Formatted Amount " + n);
        holder.mTextExpenditureName.setText(expenditureName);
        holder.mTextExpenditureTimestamp.setText(expenditureTimestamp());
        holder.mTextExpenditureAmount.setText(n);
        if (expenditureId.equals("housing")) {
            holder.mExpenditureIcon.setImageResource(R.drawable.ic_housing);
            holder.mExpenditureIcon.setBackgroundColor(Color.parseColor("#393ab5"));
        } else if (expenditureId.equals("food")) {
            holder.mExpenditureIcon.setImageResource(R.drawable.ic_food);
            holder.mExpenditureIcon.setBackgroundColor(Color.parseColor("#ec5b22"));
        } else if (expenditureId.equals("recreation")) {
            holder.mExpenditureIcon.setImageResource(R.drawable.ic_recreation);
            holder.mExpenditureIcon.setBackgroundColor(Color.parseColor("#62b7d5"));
        } else if (expenditureId.equals("education")) {
            holder.mExpenditureIcon.setImageResource(R.drawable.ic_education);
            holder.mExpenditureIcon.setBackgroundColor(Color.parseColor("#FF0000"));
        } else if (expenditureId.equals("entertainment")) {
            holder.mExpenditureIcon.setImageResource(R.drawable.ic_entertainment);
            holder.mExpenditureIcon.setBackgroundColor(Color.parseColor("#782D2D"));
        } else if (expenditureId.equals("transportation")) {
            holder.mExpenditureIcon.setImageResource(R.drawable.ic_transport);
            holder.mExpenditureIcon.setBackgroundColor(Color.parseColor("#62b7d5"));
        } else if (expenditureId.equals("investment")) {
            holder.mExpenditureIcon.setImageResource(R.drawable.ic_investement);
            holder.mExpenditureIcon.setBackgroundColor(Color.parseColor("#09094C"));
        } else if (expenditureId.equals("technology")) {
            holder.mExpenditureIcon.setImageResource(R.drawable.ic_tech);
            holder.mExpenditureIcon.setBackgroundColor(Color.parseColor("#ec5b22"));
        } else if (expenditureId.equals("fashion")) {
            holder.mExpenditureIcon.setImageResource(R.drawable.ic_fashion);
            holder.mExpenditureIcon.setBackgroundColor(Color.parseColor("#12536A"));
        } else if (expenditureId.equals("others")) {
            holder.mExpenditureIcon.setImageResource(R.drawable.ic_others);
            holder.mExpenditureIcon.setBackgroundColor(Color.parseColor("#000000"));
        }
        holder.mId = id;
    }

    private String expenditureTimestamp() {
        return mExpenditureDay + ", " + mExpenditureMonth + " " + mExpenditureYear;
    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView mTextExpenditureName;
        public final TextView mTextExpenditureAmount;
        public final TextView mTextExpenditureTimestamp;
        public final ImageView mExpenditureIcon;
        public int mId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextExpenditureName = (TextView) itemView.findViewById(R.id.textViewF);
            mTextExpenditureAmount = (TextView) itemView.findViewById(R.id.textView_amount);
            mTextExpenditureTimestamp = (TextView) itemView.findViewById(R.id.textViewG);
            mExpenditureIcon = (ImageView) itemView.findViewById(R.id.expenditure_icon);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, AddExpenseActivity.class);
                    intent.putExtra(AddExpenseActivity.EXPENDITURE_ID, mId);
                    mContext.startActivity(intent);
                }
            });
        }
    }
}

