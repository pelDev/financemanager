package com.example.financemanager;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financemanager.FinanceManagerProviderContract.Deposits;

//import androidx.loader.app.LoaderManager;
//import androidx.loader.content.Loader;

public class DepositTransactionsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    //private final List<NoteInfo> mNotes;
    private Cursor mCursor;
    private final LayoutInflater mLayoutInflater;
    private int mDepositDatePos;
    private int mDepositStatusPos;
    private int mDepositAmountPos;
    private int mIdPos;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    public DepositTransactionsRecyclerAdapter(Context context, Cursor cursor) {
        this.mContext = context;
        this.mCursor = cursor;
        //mNotes = notes;
        mLayoutInflater = LayoutInflater.from(mContext);
        populateColumnPositions();
    }

    private void populateColumnPositions() {
        if(mCursor == null) {
            return;
        } else {
            // get column position of deposit date
            mDepositDatePos = mCursor.getColumnIndex(Deposits.COLUMN_DEPOSIT_DATE);
            // get column position of deposit status
            mDepositStatusPos = mCursor.getColumnIndex(Deposits.COLUMN_DEPOSIT_STATUS);
            // get column position for deposit amount
            mDepositAmountPos = mCursor.getColumnIndex(Deposits.COLUMN_DEPOSIT_AMOUNT);
            // get column position for id
            mIdPos = mCursor.getColumnIndex(Deposits._ID);
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

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            // Here Inflating your recyclerview item layout
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_deposit_transaction, parent, false);
            return new ItemViewHolder(itemView);
        } else if (viewType == TYPE_HEADER) {
            // Here Inflating your header view
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.deposit_recycler_header, parent, false);
            return new HeaderViewHolder(itemView);
        }
        else return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;

            // Header Item Values
            headerViewHolder.date.setText("DATE");
            headerViewHolder.status.setText("STATUS");
            headerViewHolder.amount.setText("AMOUNT");
        } else if (holder instanceof ItemViewHolder) {
            int count = mCursor.getCount();
            mCursor.moveToPosition(position - 1);

            final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;

            String date = mCursor.getString(mDepositDatePos);
            String status = mCursor.getString(mDepositStatusPos);
            String amount = mCursor.getString(mDepositAmountPos);

            // Item Values
            itemViewHolder.depositDate.setText(date);
            itemViewHolder.depositStatus.setText(status);
            itemViewHolder.depositAmount.setText(new StringBuilder().append("NGN").append(amount).toString());
        }
    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount() + 1;
    }



        private class HeaderViewHolder extends RecyclerView.ViewHolder {

            public TextView date, status, amount;

            public HeaderViewHolder(@NonNull View itemView) {
                super(itemView);
                date = (TextView) itemView.findViewById(R.id.deposit_header_date);
                status = (TextView) itemView.findViewById(R.id.deposit_header_status);
                amount = (TextView) itemView.findViewById(R.id.deposit_header_amount);

            }
        }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView depositDate, depositStatus, depositAmount;
        public int mId;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            depositDate = (TextView) itemView.findViewById(R.id.textView_deposit_date);
            depositStatus = (TextView) itemView.findViewById(R.id.deposit_status_text);
            depositAmount = (TextView) itemView.findViewById(R.id.textView_deposit_amount);
        }
    }
}

