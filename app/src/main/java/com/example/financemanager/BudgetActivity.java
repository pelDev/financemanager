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
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.financemanager.FinanceManagerDatabaseContract.BudgetInfoEntry;

public class BudgetActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private RecyclerView mRecyclerBudgets;
    private FinanceManagerOpenHelper mDbHelper;
    private BudgetRecyclerAdapter mBudgetRecyclerAdapter;
    private static final int LOADER_BUDGETS = 0;
    private static final int LOADER_EXPENDITURE = 1;
    private Cursor mBudgetCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.budget);

        Window window = BudgetActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.bckGround));

        mDbHelper = new FinanceManagerOpenHelper(this);
        mRecyclerBudgets = findViewById(R.id.recycler_budgets);

        initializeDisplayContent();

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
        return new CursorLoader(this) {
            @Override
            public Cursor loadInBackground() {
                SQLiteDatabase db = mDbHelper.getReadableDatabase();
                String[] columns = {
                        BudgetInfoEntry.COLUMN_BUDGET_CATEGORY,
                        BudgetInfoEntry.COLUMN_BUDGET_AMOUNT,
                        BudgetInfoEntry.COLUMN_BUDGET_AMOUNT_SPENT,
                        BudgetInfoEntry._ID,
                };
                return db.query(BudgetInfoEntry.TABLE_NAME, columns, null, null,
                        null, null, null);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int id = loader.getId();
        if (id == LOADER_BUDGETS) {
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