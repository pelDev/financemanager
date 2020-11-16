package com.example.financemanager;

import android.app.ActivityOptions;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financemanager.FinanceManagerDatabaseContract.ExpenditureInfoEntry;

import java.text.NumberFormat;

public class ExpenditureRecyclerAdapter extends RecyclerView.Adapter<ExpenditureRecyclerAdapter.ViewHolder> {

    private final AppCompatActivity mContext;
    //private final List<NoteInfo> mNotes;
    private Cursor mCursor;
    private final LayoutInflater mLayoutInflater;
    private int mIdPos;
    private int mExpenditureNamePos;
    private int mExpenditureDayPos;
    private int mExpenditureAmountPos;
    private int mExpenditureIdPos;
    private int mExpenditureMonthPos;
    private int mExpenditureYearPos;
    private String mExpenditureDay;
    private String mExpenditureMonth;
    private String mExpenditureYear;

    public ExpenditureRecyclerAdapter(AppCompatActivity context, Cursor cursor) {
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

            // get the column position for expenditure name in the table
            mExpenditureNamePos = mCursor.getColumnIndex(ExpenditureInfoEntry.COLUMN_EXPENDITURE_NAME);
            // get the column position for expenditure day from table
            mExpenditureDayPos = mCursor.getColumnIndex(ExpenditureInfoEntry.COLUMN_EXPENDITURE_DAY);
            // get the column position for expenditure month from table
            mExpenditureMonthPos = mCursor.getColumnIndex(ExpenditureInfoEntry.COLUMN_EXPENDITURE_MONTH);
            // get the column position for expenditure year from table
            mExpenditureYearPos = mCursor.getColumnIndex(ExpenditureInfoEntry.COLUMN_EXPENDITURE_YEAR);
            // get the column position for expenditure amount from table
            mExpenditureAmountPos = mCursor.getColumnIndex(ExpenditureInfoEntry.COLUMN_EXPENDITURE_AMOUNT);
            // get column position for expenditure id
            mExpenditureIdPos = mCursor.getColumnIndex(ExpenditureInfoEntry.COLUMN_EXPENDITURE_ID);
            // get column position for the unique id from table
            mIdPos = mCursor.getColumnIndex(ExpenditureInfoEntry._ID);
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
    public ExpenditureRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.item_expenditure, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenditureRecyclerAdapter.ViewHolder holder, int position) {
        // move the cursor position as required by the on bind view holder
        mCursor.moveToPosition(position);
        // get value for course, title and id
        String expenditureName = mCursor.getString(mExpenditureNamePos);
        mExpenditureDay = mCursor.getString(mExpenditureDayPos);
        mExpenditureMonth = mCursor.getString(mExpenditureMonthPos);
        mExpenditureYear = mCursor.getString(mExpenditureYearPos);
        String expenditureAmount = mCursor.getString(mExpenditureAmountPos);
        //long amnt = Long.parseLong(expenditureAmount);
        NumberFormat myFormat = NumberFormat.getInstance();
        myFormat.setGroupingUsed(true);
        String n;
        if (expenditureAmount.equals("")) {
            n = myFormat.format(0);
        } else {
            n = myFormat.format(Double.parseDouble(expenditureAmount));
        }
        if (n.length() > 6) {
            n = n.substring(0, 4) + "..";
        }
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
        holder.itemView.setTag(id);
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
            mTextExpenditureName = itemView.findViewById(R.id.textViewF);
            mTextExpenditureAmount = itemView.findViewById(R.id.textView_amount);
            mTextExpenditureTimestamp = itemView.findViewById(R.id.textViewG);
            mExpenditureIcon = itemView.findViewById(R.id.expenditure_icon);
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

