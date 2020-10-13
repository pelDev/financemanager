package com.example.financemanager;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.example.financemanager.ExpenditureDatabaseContract.ExpenditureInfoEntry;


public class DatabaseDataWorker {
    private SQLiteDatabase mDb;

    public DatabaseDataWorker(SQLiteDatabase db) {
        mDb = db;
    }

    public void insertSampleNotes() {
        insertExpenditure("food", "Food",
                "27, December 2002", "3000");
        insertExpenditure("shelter", "Housing",
                "1, January 2010", "20000");


    }


     private void insertExpenditure(String expenditure_id, String expenditure_name, String expenditure_timestamp, String expenditure_amount) {
        ContentValues values = new ContentValues();
        values.put(ExpenditureInfoEntry.COLUMN_EXPENDITURE_ID, expenditure_id);
        values.put(ExpenditureInfoEntry.COLUMN_EXPENDITURE_NAME, expenditure_name);
        values.put(ExpenditureInfoEntry.COLUMN_EXPENDITURE_TIMESTAMP, expenditure_timestamp);
        values.put(ExpenditureInfoEntry.COLUMN_EXPENDITURE_AMOUNT, Integer.parseInt(expenditure_amount));

        long newRowId = mDb.insert(ExpenditureInfoEntry.TABLE_NAME, null, values);
    }

}
