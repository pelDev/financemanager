package com.example.financemanager;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financemanager.FinanceManagerDatabaseContract.IncomeInfoEntry;

import java.text.NumberFormat;

//import androidx.loader.app.LoaderManager;
//import androidx.loader.content.Loader;

public class IncomeRecyclerAdapter extends RecyclerView.Adapter<IncomeRecyclerAdapter.ViewHolder> {

    private final Context mContext;
    //private final List<NoteInfo> mNotes;

    private Cursor mCursor;
    private final LayoutInflater mLayoutInflater;
    private int mIdPos;
    private int mIncomeAmountPos;
    private int mIncomeDayPos;
    private int mIncomeMonthPos;
    private double mAmountSpent;
    private int mIncomeYearPos;

    public IncomeRecyclerAdapter(Context context, Cursor cursor) {
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
            // get the column position for income amount in the table
            mIncomeAmountPos = mCursor.getColumnIndex(IncomeInfoEntry.COLUMN_INCOME_AMOUNT);
            // get the column position for income day from table
            mIncomeDayPos = mCursor.getColumnIndex(IncomeInfoEntry.COLUMN_INCOME_DAY);
            // get column position for income month from table
            mIncomeMonthPos = mCursor.getColumnIndex(IncomeInfoEntry.COLUMN_INCOME_MONTH);
            // get column position for income year from table
            mIncomeYearPos = mCursor.getColumnIndex(IncomeInfoEntry.COLUMN_INCOME_YEAR);
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
    public IncomeRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.item_income, parent, false);
        return new ViewHolder(itemView);
    }

//    @RequiresApi(api = Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // move the cursor position as required by the on bind view holder
        mCursor.moveToPosition(position);
        // get value for course, title and id
        String incomeAmount = mCursor.getString(mIncomeAmountPos);
        Long amnt = new Long(Integer.parseInt(incomeAmount));
        NumberFormat myFormat = NumberFormat.getInstance();
        myFormat.setGroupingUsed(true);
        String n = myFormat.format(amnt);
        String incomeDay = mCursor.getString(mIncomeDayPos);
        String incomeMonth = mCursor.getString(mIncomeMonthPos);
        String incomeYear = mCursor.getString(mIncomeYearPos);
        String timeStamp = incomeTimestamp(incomeDay, incomeMonth, incomeYear);
        holder.mTextIncomeAmount.setText(n);
        holder.mTextTimeStampName.setText(timeStamp);

    }

    private String incomeTimestamp(String day, String month, String year) {
        return day + ", " + month + " " + year;
    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView mTextTimeStampName;
        public final TextView mTextIncomeAmount;
        public int mId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextTimeStampName = (TextView) itemView.findViewById(R.id.textView_income_date);
            mTextIncomeAmount = (TextView) itemView.findViewById(R.id.textView_income_amount);
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

