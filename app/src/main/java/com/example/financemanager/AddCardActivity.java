package com.example.financemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.content.Loader;
import android.database.Cursor;
import android.app.LoaderManager;

import com.example.financemanager.FinanceManagerProviderContract.Cards;

public class AddCardActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private EditText mEditTextExpiryDate;
    private static final int LOADER_CARD = 0;
    public static final int ID_NOT_SET = -1;
    public static final String CARD_ID = "com.example_financemanager.CARD_ID";
    private int mCardId;
    private boolean mIsNewCard;
    private Uri mCardUri;
    private Cursor mCardCursor;
    private int mCardNamePos;
    private int mCardNumberPos;
    private int mCardExpiryPos;
    private EditText mEditTextCardName;
    private EditText mEditTextCardNumber;
    private boolean mIsCancelling = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        mEditTextCardName = findViewById(R.id.editTextCardName);
        mEditTextCardNumber = findViewById(R.id.editTextCardNumber);
        mEditTextExpiryDate = findViewById(R.id.editTextExpiryDate);
        mEditTextExpiryDate.addTextChangedListener(new TextWatcher() {
            int previousLength = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                previousLength = mEditTextExpiryDate.getText().toString().length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int length = s.length();
                if (previousLength < length && length == 2) {
                    s.append("/");
                }
            }
        });

        readDisplayStateValues();
        if(!mIsNewCard)
            getLoaderManager().initLoader(LOADER_CARD, null, this);

    }

    private void readDisplayStateValues() {
        Intent intent = getIntent();

        // get the id passed from card activity
        mCardId = intent.getIntExtra(CARD_ID, ID_NOT_SET);
        mIsNewCard = this.mCardId == ID_NOT_SET;
        if (mIsNewCard) {
            createNewCard();
        }
    }


    private void createNewCard() {
        final ContentValues values = new ContentValues();
        values.put(Cards.COLUMN_CARD_NAME, "");
        values.put(Cards.COLUMN_CARD_NUMBER, "");
        values.put(Cards.COLUMN_CARD_EXPIRY, "");

        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                mCardUri = getContentResolver().insert(Cards.CONTENT_URI, values);
                return null;
            }
        };
        task.execute();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mIsCancelling = true;
    }

    private void saveCardToDatabase() {
        String cardName = mEditTextCardName.getText().toString();
        String cardNumber = mEditTextCardNumber.getText().toString();
        String cardExpiryDate = mEditTextExpiryDate.getText().toString();
        final ContentValues values = new ContentValues();
        values.put(Cards.COLUMN_CARD_NAME, cardName);
        values.put(Cards.COLUMN_CARD_NUMBER, cardNumber);
        values.put(Cards.COLUMN_CARD_EXPIRY, cardExpiryDate);

        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                getContentResolver().update(mCardUri, values, null, null);
                return null;
            }
        };
        task.execute();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mIsNewCard) {
            if (mIsCancelling)
            getContentResolver().delete(mCardUri, null, null);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = null;
        if (id == LOADER_CARD) {
            loader = createLoaderCard();
        }
        return loader;
    }

    private CursorLoader createLoaderCard() {
        mCardUri = ContentUris.withAppendedId(Cards.CONTENT_URI, mCardId);
        String[] columns = {
                Cards.COLUMN_CARD_NAME,
                Cards.COLUMN_CARD_NUMBER,
                Cards.COLUMN_CARD_EXPIRY
        };
        return new CursorLoader(this, mCardUri, columns, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_CARD) {
            mCardCursor = data;
            if (mCardCursor.getCount() != 0) {
                mCardNamePos = mCardCursor.getColumnIndex(Cards.COLUMN_CARD_NAME);
                mCardNumberPos = mCardCursor.getColumnIndex(Cards.COLUMN_CARD_NUMBER);
                mCardExpiryPos = mCardCursor.getColumnIndex(Cards.COLUMN_CARD_EXPIRY);
                mCardCursor.moveToNext();
                displayCardDetails();
            }
        }
    }

    private void displayCardDetails() {
        String cardName = mCardCursor.getString(mCardNamePos);
        String cardNumber = mCardCursor.getString(mCardNumberPos);
        String cardExpiryDate = mCardCursor.getString(mCardExpiryPos);

        mEditTextCardName.setText(cardName);
        mEditTextCardNumber.setText(cardNumber);
        mEditTextExpiryDate.setText(cardExpiryDate);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == LOADER_CARD) {
            if (mCardCursor != null)
                mCardCursor.close();
        }
    }

    public void saveCard(View view) {
        saveCardToDatabase();
        finish();
    }
}