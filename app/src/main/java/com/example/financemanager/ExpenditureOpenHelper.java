package com.example.financemanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.financemanager.ExpenditureDatabaseContract.ExpenditureInfoEntry;

public class ExpenditureOpenHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Expenditure.db";
    public static final int DATABASE_VERSION = 1;

    public ExpenditureOpenHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ExpenditureInfoEntry.SQL_CREATE_TABLE);
        db.execSQL(ExpenditureInfoEntry.SQL_CREATE_INDEX1);

        DatabaseDataWorker worker = new DatabaseDataWorker(db);
        worker.insertSampleNotes();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
