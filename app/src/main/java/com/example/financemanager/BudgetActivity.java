package com.example.financemanager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.content.CursorLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.Loader;
import android.app.LoaderManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.financemanager.FinanceManagerDatabaseContract.BudgetInfoEntry;
import com.example.financemanager.FinanceManagerProviderContract.Budgets;

import java.text.DateFormatSymbols;
import java.util.Calendar;

public class BudgetActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private RecyclerView mRecyclerBudgets;
    private FinanceManagerOpenHelper mDbHelper;
    private BudgetRecyclerAdapter mBudgetRecyclerAdapter;
    private static final int LOADER_BUDGETS = 0;
    private static final int LOADER_EXPENDITURE = 1;
    private Cursor mBudgetCursor;
    private int mDay;
    private String mMonthName;
    private int mYear;
    private TextView mEmptyRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.budget);

//        Window window = BudgetActivity.this.getWindow();
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        window.setStatusBarColor(ContextCompat.getColor(this, R.color.bckGround));

        mDbHelper = new FinanceManagerOpenHelper(this);
        // get the current date
        Calendar calendar = Calendar.getInstance();
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        mMonthName = getMonthFromInt(month);
        mYear = calendar.get(Calendar.YEAR);

        TextView title = findViewById(R.id.budget_list_title);
        title.setText(getTitleText());

        mRecyclerBudgets = findViewById(R.id.recycler_budgets);
        mEmptyRecycler = findViewById(R.id.empty_view_income);

        initializeDisplayContent();

    }

    private String getTitleText() {
        return "Budget for " + mMonthName + ", " + mYear;
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

    private void initializeDisplayContent() {
        // Set up the Recycler View
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerBudgets.setLayoutManager(layoutManager);
        mBudgetRecyclerAdapter = new BudgetRecyclerAdapter(this, null);
        mRecyclerBudgets.setAdapter(mBudgetRecyclerAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(LOADER_BUDGETS, null, this);
    }

    public void gotToAddBudget(View view) {
        // Navigate to Add Budget Screen
        startActivity(new Intent(this, AddBudgetActivity.class));
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        CursorLoader loader = null;
        if (id == LOADER_BUDGETS) {
            loader = createLoaderBudgets();
        }
        return loader;
    }


    private CursorLoader createLoaderBudgets() {
                Uri uri = Budgets.CONTENT_URI;
                String[] columns = {
                        Budgets.COLUMN_BUDGET_CATEGORY,
                        Budgets.COLUMN_BUDGET_AMOUNT,
                        Budgets.COLUMN_BUDGET_AMOUNT_SPENT,
                        Budgets._ID,
                };
                String selection = Budgets.COLUMN_BUDGET_MONTH + " = ? AND " + Budgets.COLUMN_BUDGET_YEAR + " = ?";
                String[] selectionArgs = {mMonthName, Integer.toString(mYear)};
                return new CursorLoader(this, uri, columns, selection, selectionArgs, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int id = loader.getId();
        if (id == LOADER_BUDGETS) {
            if (data.getCount() != 0) {
                mRecyclerBudgets.setVisibility(View.VISIBLE);
                mEmptyRecycler.setVisibility(View.GONE);
            } else {
                mRecyclerBudgets.setVisibility(View.GONE);
                mEmptyRecycler.setVisibility(View.VISIBLE);
            }
            mBudgetCursor = data;
            mBudgetRecyclerAdapter.changeCursor(mBudgetCursor);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == LOADER_BUDGETS) {
            if (mBudgetCursor != null)
                mBudgetCursor.close();
        }
    }
}