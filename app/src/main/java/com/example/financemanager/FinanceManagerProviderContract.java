package com.example.financemanager;

import android.net.Uri;
import android.os.Parcel;
import android.provider.BaseColumns;

import javax.xml.transform.stream.StreamSource;

public final class FinanceManagerProviderContract {

    FinanceManagerProviderContract () {}

    // set the BASE URI of the apps provider
    public static final String AUTHORITY = "com.example.financemanager.provider";
    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    // Identify Columns Exposed by tables
    protected interface ExpenseColumns {
        String COLUMN_EXPENDITURE_ID = "expenditure_id";
        String COLUMN_EXPENDITURE_NAME = "expenditure_name";
        String COLUMN_EXPENDITURE_DAY = "expenditure_day";
        String COLUMN_EXPENDITURE_MONTH = "expenditure_month";
        String COLUMN_EXPENDITURE_YEAR = "expenditure_year";
        String COLUMN_EXPENDITURE_AMOUNT = "expenditure_amount";
        String COLUMN_EXPENDITURE_DESCRIPTION = "expenditure_description";
    }

    protected interface DepositColumns {
        String COLUMN_DEPOSIT_DATE = "deposit_date";
        String COLUMN_DEPOSIT_STATUS = "deposit_status";
        String COLUMN_DEPOSIT_AMOUNT = "deposit_amount";
    }

    protected interface CardColumns {
        String COLUMN_CARD_NAME = "card_name";
        String COLUMN_CARD_NUMBER = "card_number";
        String COLUMN_CARD_EXPIRY = "card_expiry";
    }

    protected interface IncomeColumns {
        String COLUMN_INCOME_DAY = "income_day";
        String COLUMN_INCOME_MONTH = "income_month";
        String COLUMN_INCOME_YEAR = "income_year";
        String COLUMN_INCOME_AMOUNT = "income_amount";
    }

    protected interface BudgetColumns {
        String COLUMN_BUDGET_DAY = "budget_day";
        String COLUMN_BUDGET_MONTH = "budget_month";
        String COLUMN_BUDGET_YEAR = "budget_year";
        String COLUMN_BUDGET_AMOUNT = "budget_amount";
        String COLUMN_BUDGET_AMOUNT_SPENT = "budget_amount_spent";
        String COLUMN_BUDGET_CATEGORY = "budget_category";
    }

    protected interface AmountColumn {
        String COLUMN_AMOUNT = "amount_amount";
    }

    // To create table specific uri content://com.example.financemanager.provider/{path}
    public static final class Expenses implements ExpenseColumns, BaseColumns {
        public static final String PATH = "expenses";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH);
    }

    public static final class Deposits implements BaseColumns, DepositColumns {
        public static final String PATH = "deposits";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH);
    }

    public static final class Cards implements CardColumns, BaseColumns {
        public static final String PATH = "cards";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH);
    }

    public static final class Incomes implements BaseColumns, IncomeColumns {
        public static final String PATH = "incomes";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH);
    }

    public static final class Budgets implements BaseColumns, BudgetColumns {
        public static final String PATH = "budgets";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH);
    }

    public static final class Amount implements BaseColumns, AmountColumn {
        public static final String PATH = "amount";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH);
    }
}
