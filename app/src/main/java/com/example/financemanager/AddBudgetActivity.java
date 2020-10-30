package com.example.financemanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Selection;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.financemanager.FinanceManagerDatabaseContract.BudgetInfoEntry;
import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class AddBudgetActivity extends AppCompatActivity {

    private EditText mBudgetAmount;
    private Spinner mSpinnerCategory;
    private FinanceManagerOpenHelper mDbOpenHelper;
    private String mMonthName;
    private int mYear;
    private int mDay;
    private ConstraintLayout mParent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_budget);

        // Change the Color of the status manager
        Window window = AddBudgetActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));

        mDbOpenHelper = new FinanceManagerOpenHelper(this);
        mParent = findViewById(R.id.add_budget_parent_layout);

        // Set the Height of the button to 10% of the devices height
        final Button button = findViewById(R.id.button_add_budget);
        ConstraintLayout.LayoutParams buttonParams = (ConstraintLayout.LayoutParams) button.getLayoutParams();
        buttonParams.height = (int) Math.round(getScreenHeight() * 0.1);
        button.setLayoutParams(buttonParams);

        //get all views that interacts with data
        mSpinnerCategory = findViewById(R.id.spinner_budget_category);
        mBudgetAmount = findViewById(R.id.editTextBudgetAmount);

        // get the current date
        Calendar calendar = Calendar.getInstance();
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        mMonthName = getMonthFromInt(month);
        mYear = calendar.get(Calendar.YEAR);

        // Populate Spinner With Lists of available Categories
        String[] Categories = new String[]{"Food", "Housing",
                "Fashion", "Education", "Entertainment", "Transportation",
                "Investment", "Technology", "Recreation", "Others"};

        final List<String> categoryList = new ArrayList<>(Arrays.asList(Categories));
        // Create an array adapter for the spinner
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerCategory.setAdapter(arrayAdapter);
        // Set an on click event to the spinner
        mSpinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String category = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //
    }

    // Get the height of devices screen at runtime
    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public void addBudget(View view) {
        // setup alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage("This Budget cannot be Edited!");

        //add buttons
        builder.setPositiveButton("Continue",
                // perform an action when user clicks
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveBudgetToDatabase();
            }
        });
        builder.setNegativeButton("Cancel",
                // perform an action when user clicks
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Create and show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void saveBudgetToDatabase() {

        String budgetCategory = deCapitalize(mSpinnerCategory.getSelectedItem().toString());
        String budgetAmount = mBudgetAmount.getText().toString();

        checkIfBudgetForCurrentMonthExists(budgetCategory, budgetAmount);

    }

    private void checkIfBudgetForCurrentMonthExists(final String category, final String amount) {
        AsyncTask task = new AsyncTask() {

            private Cursor mCursor;

            @Override
            protected Object doInBackground(Object[] objects) {
                String[] columns = {BudgetInfoEntry.COLUMN_BUDGET_CATEGORY};
                String selection = BudgetInfoEntry.COLUMN_BUDGET_CATEGORY + " = ? AND " +
                        BudgetInfoEntry.COLUMN_BUDGET_MONTH + " = ? AND " +
                        BudgetInfoEntry.COLUMN_BUDGET_YEAR + " = ?";
                String[] selectionArgs = {category, mMonthName, Integer.toString(mYear)};
                SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
                mCursor = db.query(BudgetInfoEntry.TABLE_NAME, columns, selection, selectionArgs, null, null, null);
                if (mCursor.getCount() <= 0) {
                    insertBudget(category, amount);
                    finish();
                } else {
                    Snackbar.make(mParent, "Budget Already exists for this month", Snackbar.LENGTH_LONG).show();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                if (mCursor != null)
                    mCursor.close();

                super.onPostExecute(o);
            }
        };
        task.execute();
    }

    private void insertBudget(String category, String amount) {
        final ContentValues values = new ContentValues();
        values.put(BudgetInfoEntry.COLUMN_BUDGET_CATEGORY, category);
        values.put(BudgetInfoEntry.COLUMN_BUDGET_AMOUNT, amount);
        values.put(BudgetInfoEntry.COLUMN_BUDGET_DAY, mDay);
        values.put(BudgetInfoEntry.COLUMN_BUDGET_MONTH, mMonthName);
        values.put(BudgetInfoEntry.COLUMN_BUDGET_YEAR, mYear);
        values.put(BudgetInfoEntry.COLUMN_BUDGET_AMOUNT_SPENT, "0");


        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        db.insert(BudgetInfoEntry.TABLE_NAME, null, values);
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


    private String deCapitalize(String str) {
        if(str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

}