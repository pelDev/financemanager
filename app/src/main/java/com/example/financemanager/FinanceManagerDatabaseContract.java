package com.example.financemanager;

import android.provider.BaseColumns;

public final class FinanceManagerDatabaseContract {

    private FinanceManagerDatabaseContract() {};


    // Expenditure Information Table
    public static final class ExpenditureInfoEntry implements BaseColumns {
        public static final String TABLE_NAME = "expenditure_info";
        public static final String COLUMN_EXPENDITURE_ID = "expenditure_id";
        public static final String COLUMN_EXPENDITURE_NAME = "expenditure_name";
        public static final String COLUMN_EXPENDITURE_DAY = "expenditure_day";
        public static final String COLUMN_EXPENDITURE_MONTH = "expenditure_month";
        public static final String COLUMN_EXPENDITURE_YEAR = "expenditure_year";
        public static final String COLUMN_EXPENDITURE_AMOUNT = "expenditure_amount";
        public static final String COLUMN_EXPENDITURE_DESCRIPTION = "expenditure_description";

        // create index expenditure_info_index1 ON expenditure_info (expenditure_name)
        public static final String INDEX1 = TABLE_NAME + "_index1";
        public static final String SQL_CREATE_INDEX1 =
                "CREATE INDEX " + INDEX1 + " ON " + TABLE_NAME + " (" + COLUMN_EXPENDITURE_NAME + ")";

        public static final String getQName(String columnName) {
            return TABLE_NAME + "." + columnName;
        }


        // CREATE TABLE expenditure_info
        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_EXPENDITURE_ID + " TEXT NOT NULL, " +
                        COLUMN_EXPENDITURE_NAME + " TEXT NOT NULL, " +
                        COLUMN_EXPENDITURE_DAY + " TEXT NOT NULL, " +
                        COLUMN_EXPENDITURE_MONTH + " TEXT NOT NULL, " +
                        COLUMN_EXPENDITURE_YEAR + " TEXT NOT NULL, " +
                        COLUMN_EXPENDITURE_AMOUNT + " TEXT, " +
                        COLUMN_EXPENDITURE_DESCRIPTION + " TEXT)";
    }

    // Income information Table
    public static final class IncomeInfoEntry implements BaseColumns {
        public static final String TABLE_NAME = "income_info";
        public static final String COLUMN_INCOME_AMOUNT = "income_amount";
        public static final String COLUMN_INCOME_SOURCE = "income_source";
        public static final String COLUMN_INCOME_DAY = "income_day";
        public static final String COLUMN_INCOME_MONTH = "income_month";
        public static final String COLUMN_INCOME_YEAR = "income_year";

        // create index income_infO_index1 ON income_info (income_amount)
        public static final String INDEX1 = TABLE_NAME + "_index1";
        public static final String SQL_CREATE_INDEX1 =
                "CREATE INDEX " + INDEX1 + " ON " + TABLE_NAME + " (" + COLUMN_INCOME_AMOUNT + ")";

        public static final String getQName(String columnName) {
            return TABLE_NAME + "." + columnName;
        }

        // CREATE TABLE income_info
        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY, "  +
                        COLUMN_INCOME_SOURCE + " TEXT NOT NULL, " +
                        COLUMN_INCOME_AMOUNT + " TEXT, " +
                        COLUMN_INCOME_DAY + " TEXT NOT NULL, " +
                        COLUMN_INCOME_MONTH + " TEXT NOT NULL, " +
                        COLUMN_INCOME_YEAR + " TEXT NOT NULL)";
    }

    // Amount Information Table
    public static final class AmountInfoEntry {
        public static final String TABLE_NAME = "amount_info";
        public static final String COLUMN_AMOUNT = "amount_amount";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_AMOUNT + " INTEGER)";

    }

    // Budget Information Table
    public static final class BudgetInfoEntry implements BaseColumns {
        public static final String TABLE_NAME = "budget_info";
        public static final String COLUMN_BUDGET_DAY = "budget_day";
        public static final String COLUMN_BUDGET_MONTH = "budget_month";
        public static final String COLUMN_BUDGET_YEAR = "budget_year";
        public static final String COLUMN_BUDGET_CATEGORY = "budget_category";
        public static final String COLUMN_BUDGET_AMOUNT = "budget_amount";
        public static final String COLUMN_BUDGET_AMOUNT_SPENT = "budget_amount_spent";

        // create table budget_info (columns)
        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_BUDGET_DAY + " INTEGER, " +
                        COLUMN_BUDGET_MONTH + " TEXT NOT NULL, " +
                        COLUMN_BUDGET_YEAR + " INTEGER, " +
                        COLUMN_BUDGET_CATEGORY + " TEXT NOT NULL, " +
                        COLUMN_BUDGET_AMOUNT + " TEXT, " +
                        COLUMN_BUDGET_AMOUNT_SPENT + " TEXT)";
    }

}
