package com.example.financemanager;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.example.financemanager.FinanceManagerDatabaseContract.AmountInfoEntry;
import com.example.financemanager.FinanceManagerProviderContract.Amount;
import com.example.financemanager.FinanceManagerProviderContract.Budgets;
import com.example.financemanager.FinanceManagerProviderContract.Expenses;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class AddExpenseActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String EXPENDITURE_ID = "com.example.financemanager.EXPENDITURE_ID";
    public static final int ID_NOT_SET = -1;
    public static final int LOADER_EXPENSE = 0;
    private int mExpenditureId;
    private boolean mIsNewExpense;
    private TextView mHeader;
    private Cursor mExpenseCursor;
    private int mExpenseIdPosition;
    private int mExpenseNamePosition;
    private int mExpenseAmountPosition;
    private int mExpenditureDescriptionPosition;
    private EditText mExpNameEditText;
    private EditText mExpAmountEditText;
    private EditText mExpDescriptionEditText;
    private Spinner mSpinner;
    private ArrayAdapter<String> mArrayAdapter;
    private double mOriginalAmount;
    private String mOriginalId;
    private double mBudgetAmountSpent;
    private double mNewAmount;
    private Cursor mCursor;
    private String mNewExpenseAmount;
    private int mDay;
    private String mMonthName;
    private int mYear;
    private ConstraintLayout mParentLayout;
    private Uri mExpenseUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);
        mHeader = findViewById(R.id.textView);
        mExpNameEditText = findViewById(R.id.editTextTextPersonName);
        mExpAmountEditText = findViewById(R.id.editTextTextNumber);
        mExpDescriptionEditText = findViewById(R.id.editTextTextMultiLine);
        mParentLayout =findViewById(R.id.add_expense_parent_layout);

        // set layout entry animation
        int resId = R.anim.layout_animation_down_to_up;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, resId);
        mParentLayout.setLayoutAnimation(animation);

        getWindow().setEnterTransition(new Fade());
        getWindow().setExitTransition(new Fade());

//        Window window = AddExpenseActivity.this.getWindow();
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorAccent));

        final Button button = findViewById(R.id.button_add_expense);
        ConstraintLayout.LayoutParams buttonParams = (ConstraintLayout.LayoutParams) button.getLayoutParams();
        buttonParams.height = (int) Math.round(getScreenHeight() * 0.1);
        button.setLayoutParams(buttonParams);
        button.post(new Runnable() {
            @Override
            public void run() {
                Drawable image = getApplicationContext().getResources().getDrawable(R.drawable.ic_forward);
                image.setBounds(0, 0, getIconSize() * 2, getIconSize());
                //button.setCompoundDrawables(null, null, image, null);
            }
        });

        // Populate Spinner with List of Categories
        mSpinner = findViewById(R.id.spinner_category);
        String[] Categories = new String[]{"Food", "Housing",
        "Fashion", "Education", "Entertainment", "Transportation",
        "Investment", "Technology", "Recreation", "Others"};

        Calendar calendar = Calendar.getInstance();
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        mMonthName = getMonthFromInt(month);
        mYear = calendar.get(Calendar.YEAR);

        final List<String> categoryList = new ArrayList<>(Arrays.asList(Categories));
        mArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryList);
        mArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(mArrayAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String category = parent.getItemAtPosition(position).toString();
                //Toast.makeText(parent.getContext(), "Category " + category, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        readDisplayStateValues();

        if(!mIsNewExpense)
            getLoaderManager().initLoader(LOADER_EXPENSE, null, this);

    }

    private void readDisplayStateValues() {
        Intent intent = getIntent();

        // get the note id passes from the list of notes
        mExpenditureId = intent.getIntExtra(EXPENDITURE_ID, ID_NOT_SET);
        mIsNewExpense = this.mExpenditureId == ID_NOT_SET;
        if (mIsNewExpense) {
            mHeader.setText("Add Expense");
            createNewExpense();
        } else {
            mHeader.setText("Edit Expense");
        }

        Log.i("Position", "Expense Position: " + mExpenditureId);
        //mNote = DataManager.getInstance().getNotes().get(this.mNoteId);
    }

    private void displayExpense() {
        String expenseId = mExpenseCursor.getString(mExpenseIdPosition);
        String expenseName = mExpenseCursor.getString(mExpenseNamePosition);
        String expenseAmount = mExpenseCursor.getString(mExpenseAmountPosition);
        String expenseDescription = mExpenseCursor.getString(mExpenditureDescriptionPosition);
        mExpNameEditText.setText(expenseName);
        mExpAmountEditText.setText(expenseAmount);
        mExpDescriptionEditText.setText(expenseDescription);
        String formattedExpenseId = capitalize(expenseId);
        Log.i("Expense", "Expense id " + formattedExpenseId);
        int coursePosition = mArrayAdapter.getPosition(formattedExpenseId);
        mSpinner.setSelection(coursePosition);
        if (expenseAmount.equals("")) {
            mOriginalAmount = 0.0;
        } else {
            mOriginalAmount = Double.parseDouble(expenseAmount);
        }
        mOriginalId = expenseId;
    }


    private String capitalize(String str) {
        if (str.isEmpty() || str == null) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private String deCapitalize(String str) {
        if(str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    private void createNewExpense() {
        final ContentValues values = new ContentValues();
        values.put(Expenses.COLUMN_EXPENDITURE_NAME, "");
        values.put(Expenses.COLUMN_EXPENDITURE_AMOUNT, "0");
        values.put(Expenses.COLUMN_EXPENDITURE_DESCRIPTION, "");
        values.put(Expenses.COLUMN_EXPENDITURE_ID, "");
        values.put(Expenses.COLUMN_EXPENDITURE_DAY, "");
        values.put(Expenses.COLUMN_EXPENDITURE_MONTH, "");
        values.put(Expenses.COLUMN_EXPENDITURE_YEAR, "");

        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                // insert expense through content provider
                mExpenseUri = getContentResolver().insert(Expenses.CONTENT_URI, values);
                return null;
            }
        };
        task.execute();

        mOriginalId = "food";
        mOriginalAmount = 0;
        Log.i("Expenditure", "New Expense at position " + mExpenditureId);
    }

    private static int getIconSize() {
        return (int) Math.round(getScreenHeight() * 0.05);
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }


    public void saveExpense(View view) {
        String expenseName = mExpNameEditText.getText().toString();
        mNewExpenseAmount = mExpAmountEditText.getText().toString();
        String expenseDescription = mExpDescriptionEditText.getText().toString();
        String expenseId = deCapitalize(mSpinner.getSelectedItem().toString());
        Log.i("Expenditure", " New Expense Id " + expenseId);

        Log.i("Expenditure", "Today's Date " + mDay + " " +
                mMonthName + " " + mYear);
        saveExpenseToDatabase(expenseName, mNewExpenseAmount, expenseDescription, expenseId, mDay, mMonthName, mYear);
        updateAmountSpentInBudget(expenseId);
        updateAmount();
        finish();
    }

    private void updateAmount() {
        double originalAmountInDatabase = getOriginalAmount();
        double newAmount = Double.parseDouble(mNewExpenseAmount);
        double originalAmount = mOriginalAmount;
        double amountToBeRemoved = newAmount - originalAmount;
        double newAmountForDatabase = originalAmountInDatabase - amountToBeRemoved;
        if (newAmountForDatabase < 0)
            newAmountForDatabase = 0;
        final String selection = Amount.COLUMN_AMOUNT + " = ?";
        final String[] selectionArgs = {Double.toString(originalAmountInDatabase)};
        final ContentValues values = new ContentValues();
        values.put(Amount.COLUMN_AMOUNT, newAmountForDatabase);
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                //SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
                //db.update(AmountInfoEntry.TABLE_NAME, values, selection, selectionArgs);
                getContentResolver().update(Amount.CONTENT_URI, values, selection, selectionArgs);
                return null;
            }
        };
        task.execute();
    }

    private double getOriginalAmount() {
        String[] columns = {AmountInfoEntry.COLUMN_AMOUNT};
        //SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
        Cursor cursor = getContentResolver().query(Amount.CONTENT_URI, columns, null,
                null, null);
        cursor.moveToFirst();
        int amountPos = cursor.getColumnIndex(Amount.COLUMN_AMOUNT);
        String amount = cursor.getString(amountPos);
        double amountDouble = Double.parseDouble(amount);
        cursor.close();
        return amountDouble;
    }

    private void updateAmountSpentInBudget(String expenseId) {
        if (mOriginalId.equals(expenseId)) {
            getOriginalAmountSpentInBudget(mOriginalId);
            if (mCursor.getCount() != 0) setNewAmountForSameId();
        } else {
            getOriginalAmountSpentInBudget(mOriginalId);
            if (mCursor.getCount() != 0) {
                removeOriginalAmountFromOriginalBudget();
            }
            setNewAmountForDifferentId(expenseId);
        }
    }

    private void setNewAmountForDifferentId(String id) {
        mNewAmount = Double.parseDouble(mNewExpenseAmount);
        getOriginalAmountSpentInBudget(id);
        if (mCursor.getCount() != 0) {
            double newBudgetSpentAmount = mBudgetAmountSpent + mNewAmount;
            update(newBudgetSpentAmount, id);
        } else {
            Toast.makeText(this, "Budget " + id + " not found!", Toast.LENGTH_LONG).show();
        }

    }

    private void removeOriginalAmountFromOriginalBudget() {
        // Since Expense id has been changed remove the original amount from the corresponding budget
        double newAmount = mBudgetAmountSpent - mOriginalAmount;
        if (newAmount < 0)
            newAmount = 0;
        update(newAmount, mOriginalId);
    }

    private void update(double amount, String id) {
        final ContentValues values = new ContentValues();
        final String selection = Budgets.COLUMN_BUDGET_CATEGORY + " = ? AND " +
                Budgets.COLUMN_BUDGET_MONTH + " = ? AND " +
                Budgets.COLUMN_BUDGET_YEAR + " = ?";
        final String[] selectionArgs = {id, mMonthName, Integer.toString(mYear)};
        values.put(Budgets.COLUMN_BUDGET_AMOUNT_SPENT, Double.toString(amount));

        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
//                SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
//                db.update(BudgetInfoEntry.TABLE_NAME, values, selection, selectionArgs);
                getContentResolver().update(Budgets.CONTENT_URI, values, selection, selectionArgs);
                return null;
            }
        };
        task.execute();

    }

    private void getOriginalAmountSpentInBudget(String id) {
        String[] columns = {
                Budgets.COLUMN_BUDGET_AMOUNT_SPENT
        };
        String selection = Budgets.COLUMN_BUDGET_CATEGORY + " = ? AND " +
                Budgets.COLUMN_BUDGET_MONTH + " = ? AND " +
                Budgets.COLUMN_BUDGET_YEAR + " = ?";
        String[] selectionArgs = {id, mMonthName, Integer.toString(mYear)};

        //SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
        mCursor = getContentResolver().query(Budgets.CONTENT_URI, columns, selection, selectionArgs, null);
        if (mCursor.getCount() != 0) {
            mCursor.moveToFirst();
            int budgetAmountSpentPos = mCursor.getColumnIndex(Budgets.COLUMN_BUDGET_AMOUNT_SPENT);
            String amountSpentString = mCursor.getString(budgetAmountSpentPos);
            mBudgetAmountSpent = Double.parseDouble(amountSpentString);
        }
    }

    private void setNewAmountForSameId() {
        mNewAmount = Double.parseDouble(mNewExpenseAmount);
        if (mCursor.getCount() != 0) {
            double amountToBeAdded = mBudgetAmountSpent + (mNewAmount - mOriginalAmount);
            update(amountToBeAdded, mOriginalId);
        } else {
            Toast.makeText(this, "Budget " + mOriginalId + " not found!", Toast.LENGTH_LONG);
        }
    }

    private void saveExpenseToDatabase(String expenseName, String expenseAmount, String expenseDescription,
                                       String expenseId, int day, String monthName, int year) {

//        final String selection = ExpenditureInfoEntry._ID + " = ?";
//        final String[] selectionArgs = new String[]{Integer.toString(mExpenditureId)};
        final ContentValues values = new ContentValues();
        values.put(Expenses.COLUMN_EXPENDITURE_NAME, expenseName);
        values.put(Expenses.COLUMN_EXPENDITURE_AMOUNT, expenseAmount);
        values.put(Expenses.COLUMN_EXPENDITURE_DESCRIPTION, expenseDescription);
        values.put(Expenses.COLUMN_EXPENDITURE_ID, expenseId);
        values.put(Expenses.COLUMN_EXPENDITURE_DAY, Integer.toString(day));
        values.put(Expenses.COLUMN_EXPENDITURE_MONTH, monthName);
        values.put(Expenses.COLUMN_EXPENDITURE_YEAR, Integer.toString(year));

        AsyncTask task = new AsyncTask() {
            final Uri uri = mExpenseUri;
            @Override
            protected Object doInBackground(Object[] objects) {
                getContentResolver().update(uri, values, null, null);
                return null;
            }
        };
        task.execute();
    }

    private String getMonthFromInt(int month) {
        String monthString = "";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (month >= 0 && month <= 11) {
            monthString = months[month];
        }
        return monthString;
    }


    @Override
    protected void onPause() {
        super.onPause();
        if(mIsNewExpense) {
            getContentResolver().delete(mExpenseUri, null, null);
        }

    }

    public void cancel(View view) {
        finish();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = null;
        if (id == LOADER_EXPENSE)
            loader = createLoaderExpense();
        return loader;
    }

    private CursorLoader createLoaderExpense() {
        mExpenseUri = ContentUris.withAppendedId(Expenses.CONTENT_URI, mExpenditureId);
        String[] expenseColumns = {
                Expenses.COLUMN_EXPENDITURE_NAME,
                Expenses.COLUMN_EXPENDITURE_AMOUNT,
                Expenses.COLUMN_EXPENDITURE_ID,
                Expenses.COLUMN_EXPENDITURE_DESCRIPTION
        };
        return new CursorLoader(this, mExpenseUri, expenseColumns, null,
                null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_EXPENSE) {
            mExpenseCursor = data;
            mExpenseIdPosition = mExpenseCursor.getColumnIndex(Expenses.COLUMN_EXPENDITURE_ID);
            mExpenseNamePosition = mExpenseCursor.getColumnIndex(Expenses.COLUMN_EXPENDITURE_NAME);
            mExpenseAmountPosition = mExpenseCursor.getColumnIndex(Expenses.COLUMN_EXPENDITURE_AMOUNT);
            mExpenditureDescriptionPosition = mExpenseCursor.getColumnIndex(Expenses.COLUMN_EXPENDITURE_DESCRIPTION);
            mExpenseCursor.moveToNext();
            displayExpense();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == LOADER_EXPENSE) {
            if(mExpenseCursor != null)
                mExpenseCursor.close();
        }
    }
}