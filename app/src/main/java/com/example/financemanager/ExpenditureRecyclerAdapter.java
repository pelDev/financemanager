package com.example.financemanager;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financemanager.ExpenditureDatabaseContract.ExpenditureInfoEntry;

public class ExpenditureRecyclerAdapter extends RecyclerView.Adapter<ExpenditureRecyclerAdapter.ViewHolder> {

    private final Context mContext;
    //private final List<NoteInfo> mNotes;
    private Cursor mCursor;
    private final LayoutInflater mLayoutInflater;
    private int mIdPos;
    private int mExpenditureNamePos;
    private int mExpenditureTimestampPos;
    private int mExpenditureAmountPos;
    private int mExpenditureIdPos;

    public ExpenditureRecyclerAdapter(Context context, Cursor cursor) {
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
            // get the column position for expenditure timestamp from table
            mExpenditureTimestampPos = mCursor.getColumnIndex(ExpenditureInfoEntry.COLUMN_EXPENDITURE_TIMESTAMP);
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
        String expenditureTimestamp = mCursor.getString(mExpenditureTimestampPos);
        String expenditureAmount = Integer.toString(mCursor.getInt(mExpenditureAmountPos));
        String expenditureId = mCursor.getString(mExpenditureIdPos);
        int id = mCursor.getInt(mIdPos);

        holder.mTextExpenditureName.setText(expenditureName);
        holder.mTextExpenditureTimestamp.setText(expenditureTimestamp);
        holder.mTextExpenditureAmount.setText(expenditureAmount);
        if (expenditureId.equals("shelter")) {
            holder.mExpenditureIcon.setImageResource(R.drawable.ic_housing);
            holder.mExpenditureIcon.setBackgroundColor(Color.parseColor("#393ab5"));
        } else if (expenditureId.equals("food")) {
            holder.mExpenditureIcon.setImageResource(R.drawable.ic_food);
            holder.mExpenditureIcon.setBackgroundColor(Color.parseColor("#ec5b22"));
        }
        holder.mId = id;
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
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(mContext, NoteActivity.class);
//                    intent.putExtra(NoteActivity.NOTE_ID, mId);
//                    mContext.startActivity(intent);
//                }
//            });
        }
    }
}

