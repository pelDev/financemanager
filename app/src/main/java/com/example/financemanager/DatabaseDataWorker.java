package com.example.financemanager;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.example.financemanager.FinanceManagerDatabaseContract.AmountInfoEntry;
import com.example.financemanager.FinanceManagerDatabaseContract.BudgetInfoEntry;
import com.example.financemanager.FinanceManagerDatabaseContract.ExpenditureInfoEntry;
import com.example.financemanager.FinanceManagerDatabaseContract.IncomeInfoEntry;


public class DatabaseDataWorker {
    private SQLiteDatabase mDb;

    public DatabaseDataWorker(SQLiteDatabase db) {
        mDb = db;
    }

    public void insertSampleExpenditures() {
        insertExpenditure("food", "Food",
                "27", "January", "2020", "4000", "I " +
                        "bought akara and moi-moi at aunty kudi");
        insertExpenditure("housing", "Housing",
                "1", "January", "2020", "10000",
                "Paid my sons house rent");


    }

    public void insertAmount() {
        ContentValues values = new ContentValues();
        values.put(AmountInfoEntry.COLUMN_AMOUNT, 0);

        long newRowId = mDb.insert(AmountInfoEntry.TABLE_NAME, null, values);
    }


     private void insertExpenditure(String expenditure_id, String expenditure_name, String expenditure_day,
                                    String expenditure_month, String expenditure_year, String expenditure_amount, String description) {
        ContentValues values = new ContentValues();
        values.put(ExpenditureInfoEntry.COLUMN_EXPENDITURE_ID, expenditure_id);
        values.put(ExpenditureInfoEntry.COLUMN_EXPENDITURE_NAME, expenditure_name);
        values.put(ExpenditureInfoEntry.COLUMN_EXPENDITURE_DAY, expenditure_day);
        values.put(ExpenditureInfoEntry.COLUMN_EXPENDITURE_MONTH, expenditure_month);
        values.put(ExpenditureInfoEntry.COLUMN_EXPENDITURE_YEAR, expenditure_year);
        values.put(ExpenditureInfoEntry.COLUMN_EXPENDITURE_AMOUNT, Integer.parseInt(expenditure_amount));
        values.put(ExpenditureInfoEntry.COLUMN_EXPENDITURE_DESCRIPTION, description);

        long newRowId = mDb.insert(ExpenditureInfoEntry.TABLE_NAME, null, values);
    }

    public void insertSampleIncome() {
        insertIncome("50000", "7", "January", "2020", "Work Salary");

        insertIncome("10000", "2", "January", "2020", "Side Job");

        insertIncome("15000", "15", "January", "2020", "Gift");
    }

    private void insertIncome(String income_amount, String income_day, String income_month, String income_year, String incomeSource) {
        ContentValues values = new ContentValues();
        values.put(IncomeInfoEntry.COLUMN_INCOME_AMOUNT, Integer.parseInt(income_amount));
        values.put(IncomeInfoEntry.COLUMN_INCOME_DAY, income_day);
        values.put(IncomeInfoEntry.COLUMN_INCOME_MONTH, income_month);
        values.put(IncomeInfoEntry.COLUMN_INCOME_SOURCE, incomeSource);
        values.put(IncomeInfoEntry.COLUMN_INCOME_YEAR, income_year);

//        Cursor cursor = mDb.query(AmountInfoEntry.TABLE_NAME, new String[]{AmountInfoEntry.COLUMN_AMOUNT},
//                null, null, null, null, null);
//        int amountPos = cursor.getColumnIndex(AmountInfoEntry.COLUMN_AMOUNT);
//        int amount = cursor.getInt(amountPos);
//
//        int newAmount = Integer.parseInt(income_amount) + amount;
//        ContentValues newAmountValues = new ContentValues();
//        newAmountValues.put(AmountInfoEntry.COLUMN_AMOUNT, newAmount);
//        mDb.update(AmountInfoEntry.TABLE_NAME, newAmountValues, null, null);

        long newRowId = mDb.insert(IncomeInfoEntry.TABLE_NAME, null, values);
    }

    public void insertBudget() {
        ContentValues values = new ContentValues();
        values.put(BudgetInfoEntry.COLUMN_BUDGET_CATEGORY, "food");
        values.put(BudgetInfoEntry.COLUMN_BUDGET_AMOUNT, "20000");
        values.put(BudgetInfoEntry.COLUMN_BUDGET_DAY, 25);
        values.put(BudgetInfoEntry.COLUMN_BUDGET_MONTH, "October");
        values.put(BudgetInfoEntry.COLUMN_BUDGET_YEAR, 2020);
        values.put(BudgetInfoEntry.COLUMN_BUDGET_AMOUNT_SPENT, "8000");

        long newRowId = mDb.insert(BudgetInfoEntry.TABLE_NAME, null, values);
    }
}
