package com.example.financemanager;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financemanager.FinanceManagerProviderContract.Cards;
import com.vinaygaba.creditcardview.CreditCardView;

//import androidx.loader.app.LoaderManager;
//import androidx.loader.content.Loader;

public class CardRecyclerAdapter extends RecyclerView.Adapter<CardRecyclerAdapter.ViewHolder> {

    private final Context mContext;
    //private final List<NoteInfo> mNotes;

    private Cursor mCursor;
    private final LayoutInflater mLayoutInflater;
    private int mIdPos;
    private int mCardNamePos;
    private int mCardNumberPos;
    private int mCardExpiryPos;
    private FragmentManager mFragmentManager;
    private BottomDialogFragmentCard mBottomDialogFragment;

    public CardRecyclerAdapter(Context context, Cursor cursor, FragmentManager fragmentManager) {
        mContext = context;
        mCursor = cursor;
        mFragmentManager = fragmentManager;
        //mNotes = notes;
        mLayoutInflater = LayoutInflater.from(mContext);
        populateColumnPositions();
    }

    private void populateColumnPositions() {
        if(mCursor == null) {
            return;
        } else {
            // get the column position for card name in the table
            mCardNamePos = mCursor.getColumnIndex(Cards.COLUMN_CARD_NAME);
            // get the column position for card number from table
            mCardNumberPos = mCursor.getColumnIndex(Cards.COLUMN_CARD_NUMBER);
            // get column position for card expiry date
            mCardExpiryPos = mCursor.getColumnIndex(Cards.COLUMN_CARD_EXPIRY);
            // get column position for the unique id from table
            mIdPos = mCursor.getColumnIndex(Cards._ID);
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
    public CardRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.item_card, parent, false);
        return new ViewHolder(itemView);
    }

//    @RequiresApi(api = Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // move the cursor position as required by the on bind view holder
        mCursor.moveToPosition(position);
        // get value for card name, number and expiry date
        String cardName = mCursor.getString(mCardNamePos);
        String cardNumber = mCursor.getString(mCardNumberPos);
        String cardExpiry = mCursor.getString(mCardExpiryPos);
        int idPrimary = mCursor.getInt(mIdPos);

        holder.mCreditCardView.setCardName(cardName);
        holder.mCreditCardView.setCardNumber(cardNumber);
        holder.mCreditCardView.setExpiryDate(cardExpiry);
        holder.mId = idPrimary;
        holder.itemView.setTag(idPrimary);
    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        public final CreditCardView mCreditCardView;
        public int mId;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            mCreditCardView = itemView.findViewById(R.id.card);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, AddCardActivity.class);
                    intent.putExtra(AddCardActivity.CARD_ID, mId);
                    mContext.startActivity(intent);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    openBottomSheetDialog();
                    return true;
                }

                public void openBottomSheetDialog() {
                    mBottomDialogFragment = BottomDialogFragmentCard.newInstance(mContext);
                    int id = (int) itemView.getTag();
                    Bundle bundle = new Bundle();
                    bundle.putInt("key", id);
                    mBottomDialogFragment.setArguments(bundle);
                    mBottomDialogFragment.show(mFragmentManager, BottomDialogFragmentCard.TAG);
                }
            });
        }
    }

}

