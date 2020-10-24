package com.example.financemanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
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

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class AddBudgetActivity extends AppCompatActivity {

    private EditText mBudgetAmount;
    private Spinner mSpinnerCategory;
    private FinanceManagerOpenHelper mDbOpenHelper;

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

        // Set the Height of the button to 10% of the devices height
        final Button button = findViewById(R.id.button_add_budget);
        ConstraintLayout.LayoutParams buttonParams = (ConstraintLayout.LayoutParams) button.getLayoutParams();
        buttonParams.height = (int) Math.round(getScreenHeight() * 0.1);
        button.setLayoutParams(buttonParams);

        //get all views that interacts with data
        mSpinnerCategory = findViewById(R.id.spinner_budget_category);
        mBudgetAmount = findViewById(R.id.editTextBudgetAmount);



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
                // close add budget screen after saving to database
                finish();
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

        // get the current date
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        String monthName = getMonthFromInt(month);
        int year = calendar.get(Calendar.YEAR);

        final ContentValues values = new ContentValues();
        values.put(BudgetInfoEntry.COLUMN_BUDGET_CATEGORY, budgetCategory);
        values.put(BudgetInfoEntry.COLUMN_BUDGET_AMOUNT, budgetAmount);
        values.put(BudgetInfoEntry.COLUMN_BUDGET_DAY, day);
        values.put(BudgetInfoEntry.COLUMN_BUDGET_MONTH, monthName);
        values.put(BudgetInfoEntry.COLUMN_BUDGET_YEAR, year);

        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
                db.insert(BudgetInfoEntry.TABLE_NAME, null, values);
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

}