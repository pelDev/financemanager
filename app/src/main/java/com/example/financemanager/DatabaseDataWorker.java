package com.example.financemanager;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.example.financemanager.FinanceManagerDatabaseContract.AmountInfoEntry;
import com.example.financemanager.FinanceManagerDatabaseContract.BudgetInfoEntry;
import com.example.financemanager.FinanceManagerDatabaseContract.CardInfoEntry;
import com.example.financemanager.FinanceManagerDatabaseContract.ExpenditureInfoEntry;
import com.example.financemanager.FinanceManagerDatabaseContract.IncomeInfoEntry;


public class DatabaseDataWorker {
    private SQLiteDatabase mDb;

    public DatabaseDataWorker(SQLiteDatabase db) {
        mDb = db;
    }


    public void insertAmount() {
        ContentValues values = new ContentValues();
        values.put(AmountInfoEntry.COLUMN_AMOUNT, "0.0");

        long newRowId = mDb.insert(AmountInfoEntry.TABLE_NAME, null, values);
    }

    public void insertCard() {
        ContentValues values = new ContentValues();
        values.put(CardInfoEntry.COLUMN_CARD_NAME, "Akinrele Pelumi");
        values.put(CardInfoEntry.COLUMN_CARD_NUMBER, "4848609884896666");
        values.put(CardInfoEntry.COLUMN_CARD_EXPIRY, "07/23");

        long newRowId = mDb.insert(CardInfoEntry.TABLE_NAME, null, values);
    }
}
