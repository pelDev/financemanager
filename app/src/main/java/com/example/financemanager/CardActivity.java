package com.example.financemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentUris;
import android.content.CursorLoader;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.content.Loader;
import android.app.LoaderManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.financemanager.FinanceManagerProviderContract.Cards;
import com.google.android.material.bottomappbar.BottomAppBar;

public class CardActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private LinearLayoutManager mCardLayoutManager;
    private RecyclerView mRecyclerView_cards;
    private CardRecyclerAdapter mCardRecyclerAdapter;
    public static final int LOADER_CARDS = 0;
    private Cursor mCardCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        mRecyclerView_cards = findViewById(R.id.recyclerView_cards);

//        mRecyclerView_cards.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                int id = (int) v.getTag();
//                openBottomSheetDialog();
//                return true;
//            }
//        });



        initializeDisplayContent();
    }


    private void initializeDisplayContent() {
        mCardLayoutManager = new LinearLayoutManager(this);
        mCardRecyclerAdapter = new CardRecyclerAdapter(CardActivity.this, null, getSupportFragmentManager());
        mRecyclerView_cards.setLayoutManager(mCardLayoutManager);
        mRecyclerView_cards.setAdapter(mCardRecyclerAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(LOADER_CARDS, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = null;
        if (id == LOADER_CARDS)
            loader = createLoaderCards();
        return loader;
    }

    private CursorLoader createLoaderCards() {
        String[] columns = {
          Cards.COLUMN_CARD_NAME,
          Cards.COLUMN_CARD_NUMBER,
          Cards.COLUMN_CARD_EXPIRY,
          Cards._ID
        };
        return new CursorLoader(this, Cards.CONTENT_URI, columns, null, null, null);
    }

//    public int deleteCard(int id) {
//
//        int rows = CardActivity.this.getContentResolver().delete(cardUri, null, null);
//        return rows;
//    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_CARDS) {
            mCardCursor = data;
            mCardRecyclerAdapter.changeCursor(mCardCursor);
            Log.d("Card", "Cursor Length " + mCardCursor.getCount());
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == LOADER_CARDS) {
            if (mCardCursor != null)
                mCardCursor.close();
        }
    }

    public void navigateToAddCard(View view) {
        startActivity( new Intent(this, AddCardActivity.class));
        //openBottomSheetDialog();
    }
}