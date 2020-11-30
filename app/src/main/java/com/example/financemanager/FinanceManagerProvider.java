package com.example.financemanager;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.financemanager.FinanceManagerDatabaseContract.AmountInfoEntry;
import com.example.financemanager.FinanceManagerDatabaseContract.BudgetInfoEntry;
import com.example.financemanager.FinanceManagerDatabaseContract.CardInfoEntry;
import com.example.financemanager.FinanceManagerDatabaseContract.ExpenditureInfoEntry;
import com.example.financemanager.FinanceManagerDatabaseContract.IncomeInfoEntry;
import com.example.financemanager.FinanceManagerProviderContract.Amount;
import com.example.financemanager.FinanceManagerProviderContract.Budgets;
import com.example.financemanager.FinanceManagerProviderContract.Cards;
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

    public static final int EXPENSE_ROW = 4;

    public static final int CARDS = 5;

    public static final int CARD_ROW = 6;

    // static initializer
    static {
        sUriMatcher.addURI(FinanceManagerProviderContract.AUTHORITY, Expenses.PATH, EXPENSES);
        sUriMatcher.addURI(FinanceManagerProviderContract.AUTHORITY, Incomes.PATH, INCOMES);
        sUriMatcher.addURI(FinanceManagerProviderContract.AUTHORITY, Budgets.PATH, BUDGETS);
        sUriMatcher.addURI(FinanceManagerProviderContract.AUTHORITY, Amount.PATH, AMOUNT);
        sUriMatcher.addURI(FinanceManagerProviderContract.AUTHORITY, Expenses.PATH + "/#", EXPENSE_ROW);
        sUriMatcher.addURI(FinanceManagerProviderContract.AUTHORITY, Cards.PATH, CARDS);
        sUriMatcher.addURI(FinanceManagerProviderContract.AUTHORITY, Cards.PATH + "/#", CARD_ROW);
    }
    public FinanceManagerProvider() {

    }

    FinanceManagerOpenHelper mDbOpenHelper;

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        long rowId = -1;
        String rowSelection = null;
        String[] rowSelectionArgs = null;
        int nRows = -1;
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();

        int uriMatch = sUriMatcher.match(uri);
        switch (uriMatch) {
            case EXPENSE_ROW:
                rowId = ContentUris.parseId(uri);
                rowSelection = ExpenditureInfoEntry._ID + " = ?";
                rowSelectionArgs = new String[]{Long.toString(rowId)};
                nRows = db.delete(ExpenditureInfoEntry.TABLE_NAME, rowSelection, rowSelectionArgs);
                break;
            case CARD_ROW:
                rowId = ContentUris.parseId(uri);
                rowSelection = CardInfoEntry._ID + " = ?";
                rowSelectionArgs = new String[]{Long.toString(rowId)};
                nRows = db.delete(CardInfoEntry.TABLE_NAME, rowSelection, rowSelectionArgs);
                break;
        }
        return nRows;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        // this is the id of the newly inserted row
        long rowId = -1;
        // row uri indicates the newly inserted row
        // row uri should look like this:
        // content://com.example.financemanager.provider/{table_name}/{row_id}
        Uri rowUri = null;

        int uriMatch = sUriMatcher.match(uri);
        switch (uriMatch) {
            case INCOMES:
                rowId = db.insert(IncomeInfoEntry.TABLE_NAME, null, values);
                rowUri = ContentUris.withAppendedId(Incomes.CONTENT_URI, rowId);
                break;
            case EXPENSES:
                rowId = db.insert(ExpenditureInfoEntry.TABLE_NAME, null, values);
                rowUri = ContentUris.withAppendedId(Expenses.CONTENT_URI, rowId);
                break;
            case BUDGETS:
                rowId = db.insert(BudgetInfoEntry.TABLE_NAME, null, values);
                rowUri = ContentUris.withAppendedId(Budgets.CONTENT_URI, rowId);
                break;
            case AMOUNT:
                // This is a read only table
                break;
            case CARDS:
                rowId = db.insert(CardInfoEntry.TABLE_NAME, null, values);
                rowUri = ContentUris.withAppendedId(Cards.CONTENT_URI, rowId);
                break;
        }
        return rowUri;
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
        long rowId;
        String rowSelection;
        String[] rowSelectionArgs;
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
            case EXPENSE_ROW:
                rowId = ContentUris.parseId(uri);
                rowSelection = ExpenditureInfoEntry._ID + " = ?";
                rowSelectionArgs = new String[] {Long.toString(rowId)};
                cursor = db.query(ExpenditureInfoEntry.TABLE_NAME, projection, rowSelection, rowSelectionArgs,
                        null, null, null);
                break;
            case CARDS:
                cursor = db.query(CardInfoEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case CARD_ROW:
                rowId = ContentUris.parseId(uri);
                rowSelection = CardInfoEntry._ID + " = ?";
                rowSelectionArgs = new String[] {Long.toString(rowId)};
                cursor = db.query(CardInfoEntry.TABLE_NAME, projection, rowSelection, rowSelectionArgs,
                        null, null, null);
                break;
        }
        // return Cursor
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        String rowSelection = null;
        String[] rowSelectionArgs = null;
        long rowId = -1;
        int nRows = -1;
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();

        int uriMatch = sUriMatcher.match(uri);
        switch (uriMatch) {
            case EXPENSE_ROW:
                rowId = ContentUris.parseId(uri);
                rowSelection = ExpenditureInfoEntry._ID + " = ?";
                rowSelectionArgs = new String[]{Long.toString(rowId)};
                nRows = db.update(ExpenditureInfoEntry.TABLE_NAME, values, rowSelection, rowSelectionArgs);
                break;
            case BUDGETS:
                nRows = db.update(BudgetInfoEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case AMOUNT:
                nRows = db.update(AmountInfoEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case CARDS:
                nRows = db.update(CardInfoEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case CARD_ROW:
                rowId = ContentUris.parseId(uri);
                rowSelection = CardInfoEntry._ID + " = ?";
                rowSelectionArgs = new String[]{Long.toString(rowId)};
                nRows = db.update(CardInfoEntry.TABLE_NAME, values, rowSelection, rowSelectionArgs);
                break;
        }
        return nRows;
    }
}
