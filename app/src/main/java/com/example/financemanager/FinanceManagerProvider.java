package com.example.financemanager;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.financemanager.FinanceManagerDatabaseContract.AmountInfoEntry;
import com.example.financemanager.FinanceManagerDatabaseContract.BudgetInfoEntry;
import com.example.financemanager.FinanceManagerDatabaseContract.ExpenditureInfoEntry;
import com.example.financemanager.FinanceManagerDatabaseContract.IncomeInfoEntry;
import com.example.financemanager.FinanceManagerProviderContract.Amount;
import com.example.financemanager.FinanceManagerProviderContract.Budgets;
import com.example.financemanager.FinanceManagerProviderContract.Expenses;
import com.example.financemanager.FinanceManagerProviderContract.Incomes;
import com.google.firebase.FirebaseApiNotAvailableException;

public class FinanceManagerProvider extends ContentProvider {

    // instantiate a uri matcher to NO MATCH
    private static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    public static final int EXPENSES = 0;

    public static final int INCOMES = 1;

    public static final int BUDGETS = 2;

    public static final int AMOUNT = 3;

    // static initializer
    static {
        sUriMatcher.addURI(FinanceManagerProviderContract.AUTHORITY, Expenses.PATH, EXPENSES);
        sUriMatcher.addURI(FinanceManagerProviderContract.AUTHORITY, Incomes.PATH, INCOMES);
        sUriMatcher.addURI(FinanceManagerProviderContract.AUTHORITY, Budgets.PATH, BUDGETS);
        sUriMatcher.addURI(FinanceManagerProviderContract.AUTHORITY, Amount.PATH, AMOUNT);
    }
    public FinanceManagerProvider() {

    }

    FinanceManagerOpenHelper mDbOpenHelper;

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onCreate() {
        // connect to the database open helper
        mDbOpenHelper = new FinanceManagerOpenHelper(getContext());
        // return true to indicate that content provider has been successfully created
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // initialize a cursor to null
        Cursor cursor = null;
        // connect to database
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
        // match uri then perform uri specific action
        int uriMatch = sUriMatcher.match(uri);
        switch (uriMatch) {
            case EXPENSES:
                cursor = db.query(ExpenditureInfoEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case INCOMES:
                cursor = db.query(IncomeInfoEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case BUDGETS:
                cursor = db.query(BudgetInfoEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case AMOUNT:
                cursor = db.query(AmountInfoEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
        }
        // return Cursor
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
