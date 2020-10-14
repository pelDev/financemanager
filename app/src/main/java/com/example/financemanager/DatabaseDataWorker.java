package com.example.financemanager;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.example.financemanager.ExpenditureDatabaseContract.ExpenditureInfoEntry;
import com.example.financemanager.ExpenditureDatabaseContract.IncomeInfoEntry;


public class DatabaseDataWorker {
    private SQLiteDatabase mDb;

    public DatabaseDataWorker(SQLiteDatabase db) {
        mDb = db;
    }

    public void insertSampleExpenditures() {
        insertExpenditure("food", "Food",
                "27", "January", "2020", "40000");
        insertExpenditure("shelter", "Housing",
                "1", "January", "2020", "10000");


    }


     private void insertExpenditure(String expenditure_id, String expenditure_name, String expenditure_day,
                                    String expenditure_month, String expenditure_year, String expenditure_amount) {
        ContentValues values = new ContentValues();
        values.put(ExpenditureInfoEntry.COLUMN_EXPENDITURE_ID, expenditure_id);
        values.put(ExpenditureInfoEntry.COLUMN_EXPENDITURE_NAME, expenditure_name);
        values.put(ExpenditureInfoEntry.COLUMN_EXPENDITURE_DAY, expenditure_day);
        values.put(ExpenditureInfoEntry.COLUMN_EXPENDITURE_MONTH, expenditure_month);
        values.put(ExpenditureInfoEntry.COLUMN_EXPENDITURE_YEAR, expenditure_year);
        values.put(ExpenditureInfoEntry.COLUMN_EXPENDITURE_AMOUNT, Integer.parseInt(expenditure_amount));

        long newRowId = mDb.insert(ExpenditureInfoEntry.TABLE_NAME, null, values);
    }

    public void insertSampleIncome() {
        insertIncome("50000", "7", "January", "2020", "Work Salary");

        insertIncome("10000", "2", "January", "2020", "Side Job");

        insertIncome("5000", "15", "January", "2020", "Gift");
    }

    private void insertIncome(String income_amount, String income_day, String income_month, String income_year, String incomeSource) {
        ContentValues values = new ContentValues();
        values.put(IncomeInfoEntry.COLUMN_INCOME_AMOUNT, Integer.parseInt(income_amount));
        values.put(IncomeInfoEntry.COLUMN_INCOME_DAY, income_day);
        values.put(IncomeInfoEntry.COLUMN_INCOME_MONTH, income_month);
        values.put(IncomeInfoEntry.COLUMN_INCOME_SOURCE, incomeSource);
        values.put(IncomeInfoEntry.COLUMN_INCOME_YEAR, income_year);

        long newRowId = mDb.insert(IncomeInfoEntry.TABLE_NAME, null, values);
    }

}
